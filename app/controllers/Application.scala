package controllers

import play.api.mvc._

object Application extends ExtController {

  def main(url: String) = Action {
    Ok(views.html.templates.main())
  }

  def index = Action {
    Ok(views.html.index())
  }

  def notFound(url: String) = Action {
    NotFound
  }
  
}
