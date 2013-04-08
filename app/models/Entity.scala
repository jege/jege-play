package models

// Scala
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

// Play
import play.api.Play.current

// Play Json imports
import play.api.libs.json._

// ReactiveMongo
import reactivemongo.api._
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.{BSONObjectID, BSONDocument}

// ReactiveMongo plugin
import play.modules.reactivemongo._

trait Entity {
  val db: DB = ReactiveMongoPlugin.db
  def collection: BSONCollection

  val MONGO_ID = "_id"
  val ID = "id"
  val VERSION = "version"
  val LAST_UPDATE = "lastUpdate"

  def findById(id: String): Future[Option[BSONDocument]] = collection.find(BSONDocument(MONGO_ID -> BSONObjectID(id))).cursor.toList.map(_.headOption)
  def findById(id: BSONObjectID): Future[Option[BSONDocument]] = collection.find(BSONDocument(MONGO_ID -> id)).cursor.toList.map(_.headOption)
}
