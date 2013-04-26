package models

import play.api._

// ReactiveMongo
import reactivemongo.api._
import reactivemongo.bson._
import reactivemongo.core.commands.LastError
import play.modules.reactivemongo.json.collection.JSONCollection

// ReactiveMongo plugin
import play.modules.reactivemongo._

case class Settings(
)

object SettingsBsonHandler extends BSONDocumentReader[Settings] with BSONDocumentWriter[Settings] {
  def read(document: BSONDocument): Settings = {
    Settings(
    )
  }
  def write(o: Settings): BSONDocument = {
    BSONDocument(
    )
  }
}
