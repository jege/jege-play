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
import reactivemongo.core.commands.LastError
import play.modules.reactivemongo.json.collection.JSONCollection

// ReactiveMongo plugin
import play.modules.reactivemongo._

trait Model {
  val db: DB = ReactiveMongoPlugin.db
  def collectionJson: JSONCollection

  val MONGO_ID = "_id"
  val MONGO_OID = "$oid"
  val ID = "id"
  val VERSION = "version"
  val LAST_UPDATE = "lastUpdate"

  def allJson(): Future[JsArray] = collectionJson.find(Json.obj()).cursor[JsObject].toList.map(new JsArray(_))
  def findByIdJson(id: String): Future[Option[JsObject]] = collectionJson.find(Json.obj( MONGO_ID -> Json.obj(MONGO_OID -> id) )).cursor[JsObject].toList.map(_.headOption)

  def createJson(entity: JsValue): Future[LastError] = entity match {
    case obj: JsObject => collectionJson.insert(entity)
    case _ => throw new IllegalStateException("Entity.create required a JsObject")
  }
}
