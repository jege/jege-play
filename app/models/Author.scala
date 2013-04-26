package models

import play.api.Play.current
import play.api._
import play.api.libs.json._

// ReactiveMongo
import reactivemongo.api._
import reactivemongo.core.commands.LastError
import reactivemongo.bson._
import play.modules.reactivemongo.json.collection.JSONCollection

// ReactiveMongo plugin
import play.modules.reactivemongo._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._

case class Author(
  id: String,
  email: String,
  verifiedEmail: Boolean,
  name: String,
  givenName: String,
  familyName: String,
  link: String,
  picture: Option[String],
  gender: String,
  birthday: Option[String],
  locale: Option[String]
)

object AuthorBsonHandler extends BSONDocumentReader[Author] with BSONDocumentWriter[Author] {
  def read(document: BSONDocument): Author = {
    Author(
      id = document.getAs[BSONString]("id").get.value,
      email = document.getAs[BSONString]("email").get.value,
      verifiedEmail = document.getAs[BSONBoolean]("verifiedEmail").get.value,
      name = document.getAs[BSONString]("name").get.value,
      givenName = document.getAs[BSONString]("givenName").get.value,
      familyName = document.getAs[BSONString]("familyName").get.value,
      link = document.getAs[BSONString]("link").get.value,
      picture = document.getAs[BSONString]("picture").map(_.value),
      gender = document.getAs[BSONString]("gender").get.value,
      birthday = document.getAs[BSONString]("birthday").map(_.value),
      locale = document.getAs[BSONString]("locale").map(_.value)
    )
  }
  def write(o: Author): BSONDocument = {
    BSONDocument(
      "id" -> BSONString(o.id),
      "email" -> BSONString(o.email),
      "verifiedEmail" -> BSONBoolean(o.verifiedEmail),
      "name" -> BSONString(o.name),
      "givenName" -> BSONString(o.givenName),
      "familyName" -> BSONString(o.familyName),
      "link" -> BSONString(o.link),
      "picture" -> o.picture,
      "gender" -> BSONString(o.gender),
      "birthday" -> o.birthday,
      "locale" -> o.locale
    )
  }
}

object AuthorJsonFormat extends Format[Author] {
  def reads(json: JsValue) = JsSuccess(Author(
    id = (json \ "id").as[String],
    email = (json \ "email").as[String],
    verifiedEmail = (json \ "verifiedEmail").as[Boolean],
    name = (json \ "name").as[String],
    givenName = (json \ "givenName").as[String],
    familyName = (json \ "familyName").as[String],
    link = (json \ "link").as[String],
    picture = (json \ "picture").asOpt[String],
    gender = (json \ "gender").as[String],
    birthday = (json \ "birthday").asOpt[String],
    locale = (json \ "locale").asOpt[String]
  ))
  def writes(o: Author): JsValue = Json.obj(
    "id" -> o.id,
    "email" -> o.email,
    "verifiedEmail" -> o.verifiedEmail,
    "name" -> o.name,
    "givenName" -> o.givenName,
    "familyName" -> o.familyName,
    "link" -> o.link,
    "picture" -> o.picture,
    "gender" -> o.gender,
    "birthday" -> o.birthday,
    "locale" -> o.locale
  )
}
