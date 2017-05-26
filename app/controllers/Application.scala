package controllers

import javax.inject._

import play.api._
import play.api.libs.ws.WSClient
import play.api.mvc._
import status.IsUpResult.BuildResponseForPlayForIsUpResult.BadGateway
import status._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class HomeController @Inject()(wsClient: WSClient)(implicit ec: ExecutionContext) extends Controller {

  val isItUpService = new IsUpService(wsClient)
  val statusService = new StatusService(isItUpService)

  def index = Action { implicit request =>
    val x: Request[AnyContent] = request
    Ok("""{"json":"example"}""").as("application/json")
  }

  def isGoogleUp = Action.async { implicit request =>
    isItUpService(IsUpRequest("http://www.google.com")).map { t =>
      if (t.up)
        Ok("Google is Up")
      else
        BadGateway("Google is Down")
    }
  }

  def status = Action.async { implicit request =>
    statusService(StatusRequest(List("http:www.google.com"))).map(BuildResponseForPlay.apply[StatusResponse])
  }

  def ping = Action { implicit request =>
    Ok("pong").as("text/plain")
  }
}
