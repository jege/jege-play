import play.api._
import play.api.mvc._

object Global extends GlobalSettings {
  override def onStart(app: Application) {
    Logger.info("Application has started")
  }

  override def onStop(app: Application) {
    Logger.info("Application shutdown...")
  }

  override def onError(request: play.api.mvc.RequestHeader,ex: Throwable):Result = {
    Logger.error("ERRROORRRR", ex)
    Results.InternalServerError(ex.toString)
  }

}
