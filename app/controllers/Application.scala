package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {

  def main(any: String) = Action {
    Ok(views.html.templates.main())
  }

  def index = Action {
    Ok(views.html.index())
  }

  def notFound(any: String) = Action {
    NotFound
  }
  
}