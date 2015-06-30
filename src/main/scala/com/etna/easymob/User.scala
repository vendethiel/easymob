package com.etna.easymob

case class User(id: Int, username: String)

object User {
  var instance: Option[User] = None
}