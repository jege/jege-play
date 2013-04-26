package controllers

import play.api.mvc._

object Application extends ExtController with Authentication {

  def main(url: String) = Action {
    Ok(views.html.templates.main())
  }

  def index = Authenticated { implicit user => implicit request =>
    Ok(views.html.index())
  }

  def notFound(url: String) = Action {
    NotFound
  }
}
