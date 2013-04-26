package controllers

import java.net.URLEncoder
import org.joda.time.DateTime
import play.api.Play.current
import play.api._
import play.api.mvc._
import play.api.libs.ws._
import play.api.libs.json._
import play.api.libs.json.util._
import play.api.libs.json.Reads._
import play.api.libs.functional._
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util._

import models._

trait Authentication {

  def GoogleAuthenticated(f: User => Request[AnyContent] => Result) = Action { implicit request =>
    Authentication.TokenResponse.fromSession(request.session) match {
      case Some(token) =>
        Logger("oauth2.Authenticated").debug(s"Token: $token")
        Results.Async {
          Authentication.getProfile(token).flatMap {
            case Success(profile) => profile.toUser.map { user => f(user)(request) }
            case Failure(Authentication.OAuthErrorResult(errorResult)) => Future { errorResult }
            case Failure(error) => Future { Results.InternalServerError("Internal Error") }
          }
        }
      case None => Results.Redirect(Authentication.connectionURL)
    }
  }

  def authenticated[A](f: User => A)(implicit request: RequestHeader): Future[Option[A]] = {
    Authentication.TokenResponse.fromSession(request.session) match {
      case Some(token) =>
        Authentication.getProfile(token).flatMap {
          case Success(profile) => profile.toUser.map { user => Some(f(user)) }
          case _ => Future { None }
        }
      case None => Future { None }
    }
  }

}

object Authentication extends Controller {
  import play.api.data._
  import play.api.data.Forms._

  case class OAuth2Token(value: String)

  trait OauthError extends Throwable
  case class OAuthErrorJson(content: JsValue) extends OauthError
  case class OAuthErrorResult(content: Result) extends OauthError

  lazy val conf = current.configuration.getConfig("oauth2").get
  lazy val AuthProviderX509CertUrl = conf.getString("authproviderx509certurl").get
  lazy val AuthURI = conf.getString("authuri").get
  lazy val ClientEmail = conf.getString("clientemail").get
  lazy val ClientId = conf.getString("clientid").get
  lazy val ClientSecret = conf.getString("clientsecret").get
  lazy val ClientX509CertUrl = conf.getString("clientx509certurl").get
  lazy val JavascriptOrigin = conf.getString("javascriptorigin").get
  lazy val RedirectURI = conf.getString("redirecturi").get
  lazy val TokenURI = conf.getString("tokenuri").get

  val scopes = Seq(
    "https://www.googleapis.com/auth/userinfo.email",
    "https://www.googleapis.com/auth/userinfo.profile"
  )
  val scope = scopes.mkString(" ")

  def urlEncode(str: String) = URLEncoder.encode(str, "UTF-8")

  def connectionURL: String = connectionURL(None)
  def connectionURL(state: Option[String]): String = {
    AuthURI + "?" + Seq(
      "scope" -> Some(urlEncode(scope)),
      "state" -> state,
      "redirect_uri" -> Some(urlEncode(RedirectURI)),
      "response_type" -> Some("code"),
      "client_id" -> Some(ClientId)
    ).collect{ case (k,Some(v)) => k + "=" + v }.mkString("&")
  }
  def accessTokenBody(code: String) = {
    Map(
      "code" -> Some(code),
      "client_id" -> Some(ClientId),
      "client_secret" -> Some(ClientSecret),
      "redirect_uri" -> Some(RedirectURI),
      "grant_type" -> Some("authorization_code")
    ).collect{ case (k,Some(v)) => (k, Seq(v)) }
  }

  def login = Action { implicit request =>
    Redirect(connectionURL)
  }

  val oauth2callbackForm = Form(tuple(
    "code" -> optional(text),
    "state" -> optional(text),
    "error" -> optional(text)
  ))
  def oauth2callback = Action { implicit request =>
    oauth2callbackForm.bindFromRequest.fold(
      err => BadRequest("invalid request"),
      {
        case (_, _, Some(error)) =>
          Logger("oauth2.callback").debug(s"Received error: $error")
          if (error == Some("access_denied")) Unauthorized("Access denied") else BadRequest(error)
        case (Some(code), state, _) =>
          Logger("oauth2.callback").debug(s"Received code (code: $code, state: $state)")
          Async {
            getToken(code).map {
              case Success(token) =>
                val target = routes.Application.main("") // state.getOrElse(routes.Application.index) ?
                Redirect(target).withSession(
                  "accessToken" -> token.accessToken.value,
                  "expiresIn" -> token.expiresIn.getMillis.toString
                )
              case Failure(_) =>
                // TODO use state to prevent infinite loops
                Redirect(connectionURL).withNewSession
            }
          }
        case other =>
          Logger("oauth2.callback").debug(s"Received bad request: $other")
          BadRequest("Bad parameters")
      }
    )
  }

  case class TokenResponse(
    accessToken: OAuth2Token,
    expiresIn: DateTime//,
    //token_type: String,
    //id_token: Option[String]
  )
  object TokenResponse {
    def fromSession(implicit session: Session) = {
      for {
        accessToken <- session.get("accessToken")
        expiresIn <- session.get("expiresIn").map(_.toLong)
        if expiresIn > DateTime.now.getMillis
      } yield {
        TokenResponse(
          accessToken = OAuth2Token(accessToken),
          expiresIn = new DateTime(expiresIn)
        )
      }
    }
  }
  def getToken(code: String): Future[Try[TokenResponse]] = {
    implicit object TokenReads extends Reads[TokenResponse] {
      def reads(json: JsValue) = JsSuccess(TokenResponse(
        accessToken = OAuth2Token((json \ "access_token").as[String]),
        expiresIn = {
          val now = DateTime.now
          val TTL = (json \ "expires_in").as[Long]
          new DateTime(now.getMillis + TTL * 1000)
        }
      ))
    }
    WS.url(TokenURI).post(accessTokenBody(code)).map { wsResponse =>
      val json = wsResponse.json
      Logger("oauth2.getToken").debug(s"Received: $json")
      if (wsResponse.status == 200 && (json \ "error").asOpt[String].isEmpty) {
        val tokenResp = json.as[TokenResponse]
        Logger("oauth2.getToken").debug(s"Received token: $tokenResp")
        Success(tokenResp)
      }
      else {
        Logger("oauth2.getToken").debug(s"Can't received token: ${wsResponse.body}")
        Failure(OAuthErrorResult(BadRequest("Token can't be retrieved")))
      }
    }
  }

  case class Profile(
    id: String,
    email: String,
    verified_email: Boolean,
    name: String,
    given_name: String,
    family_name: String,
    link: String,
    picture: Option[String],
    gender: String,
    birthday: Option[String],
    locale: Option[String]
  ) {
    def toUser: Future[User] = {
      User.createOrMerge(Author(
        id = id,
        email = email,
        verifiedEmail = verified_email,
        name = name,
        givenName = given_name,
        familyName = family_name,
        link = link,
        picture = picture,
        gender = gender,
        birthday = birthday,
        locale = locale
      ))
    }
  }
  implicit val profileFormat = Json.format[Profile]
  def getProfile(token: TokenResponse): Future[Try[Profile]] = getProfile(token.accessToken)
  def getProfile(token: OAuth2Token): Future[Try[Profile]] = {
    WS.url(s"https://www.googleapis.com/oauth2/v1/userinfo?access_token=${token.value}").get().map { wsResponse =>
        val json = wsResponse.json
        if (wsResponse.status == 200) {
          Logger("oauth2.getProfile").debug(s"Received profile: $json")
          Success(json.as[Profile])
        }
        else {
          Logger("oauth2.getProfile").debug(s"Can't receive profile: $json")
          Failure(OAuthErrorJson(json))
        }
      }
  }

}
