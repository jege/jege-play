package models

import org.joda.time._
import play.api.Play.current
import play.api._
import play.api.libs.json._

// ReactiveMongo
import reactivemongo.api._
import reactivemongo.bson._
import reactivemongo.core.commands.LastError
import play.modules.reactivemongo.json.collection.JSONCollection

// ReactiveMongo plugin
import play.modules.reactivemongo._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
case class User(
  id: BSONObjectID,
  author: Author,
  settings: Settings,
  createdAt: DateTime,
  lastActivityAt : DateTime
)

object User extends Model{
  lazy val collection = db("users")
  lazy val collectionJson = db[JSONCollection]("users")

  def indexes = Seq(
    reactivemongo.api.indexes.Index(
      key = List("author.id" -> reactivemongo.api.indexes.IndexType.Ascending),
      unique = true
    )
  )

  def create(author: Author) = {
    User(
      id = BSONObjectID.generate,
      author = author,
      settings = Settings(),
      createdAt = DateTime.now,
      lastActivityAt = DateTime.now
    )
  }

  def merge(oldUser: User, newUser: User): Future[User] = {
    val mergedUser = newUser.copy(
      settings = oldUser.settings,
      createdAt = oldUser.createdAt,
      lastActivityAt = oldUser.lastActivityAt
    )
    Future { mergedUser }  // FIXME
  }

  def createOrMerge(author: Author): Future[User] = {
    implicit val handler = UserBsonHandler
    val newUser = create(author)
    findByAuthorId(author.id).flatMap {
      case Some(oldUser) => merge(oldUser, newUser)
      case None =>
        collection.insert(newUser)
                  .map{ _ => newUser }
                  .recoverWith {
                    case l: LastError if l.code == Some(11000) =>
                      findByAuthorId(author.id).flatMap{ oldUser => merge(oldUser.get, newUser) }
                  }
    }
  }

  def findById(id: String): Future[Option[User]] = {
    val query = BSONDocument("_id" -> BSONObjectID(id))
    implicit val handler = UserBsonHandler
    collection.find( query ).cursor[User].headOption
  }

  def findByAuthorId(id: String): Future[Option[User]] = {
    val query = BSONDocument("author.id" -> BSONString(id))
    implicit val handler = UserBsonHandler
    collection.find( query ).cursor[User].headOption
  }

  def findByName(name: String): Future[Option[User]] = {
    val query = BSONDocument("author.name" -> BSONString(name))
    implicit val handler = UserBsonHandler
    collection.find( query ).cursor[User].headOption
  }

}

object UserBsonHandler extends BSONDocumentReader[User] with BSONDocumentWriter[User] {
  def read(document: BSONDocument): User = {
    User(
      id = document.getAs[BSONObjectID]("_id").get,
      author = AuthorBsonHandler.read(document.getAs[BSONDocument]("author").get),
      settings = document.getAs[BSONDocument]("settings").map(SettingsBsonHandler.read(_)).getOrElse(Settings()),
      createdAt = new DateTime(document.getAs[BSONDateTime]("createdAt").get.value),
      lastActivityAt = new DateTime(document.getAs[BSONDateTime]("lastActivityAt").get.value)
    )
  }
  def write(o: User): BSONDocument = {
    BSONDocument(
      "_id" -> o.id,
      "author" -> AuthorBsonHandler.write(o.author),
      "settings" -> SettingsBsonHandler.write(o.settings),
      "createdAt" -> BSONDateTime(o.createdAt.getMillis),
      "lastActivityAt" -> BSONDateTime(o.lastActivityAt.getMillis)
    )
  }
}

object UserJsonWrite extends Writes[User] {
  def writes(o: User): JsValue = Json.obj(
    "id" -> o.id.stringify,
    "author" -> AuthorJsonFormat.writes(o.author),
    "createdAt" -> Json.toJson(o.createdAt),
    "lastActivityAt" -> Json.toJson(o.lastActivityAt)
  )
}
