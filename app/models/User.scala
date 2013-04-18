package models

import play.modules.reactivemongo.json.collection.JSONCollection

object User extends Model {
  override val collection = db[JSONCollection]("users")

  val USERNAME = "username"
  val PASSWORD = "password"
  val EMAIL = "email"
}
