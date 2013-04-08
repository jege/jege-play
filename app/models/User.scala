package models

object User extends Entity {
  override val collection = db("users")

  val USERNAME = "username"
  val PASSWORD = "password"
  val EMAIL = "email"
}
