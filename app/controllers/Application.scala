package controllers

import javax.inject._

import controllers.BuildResponseForPlay._
import play.api.libs.ws.WSClient
import play.api.mvc._
import status._

import scala.concurrent.ExecutionContext

@Singleton
class HomeController @Inject()(wsClient: WSClient)(implicit ec: ExecutionContext) extends Controller with Arrow{


  val isItUpService = new IsUpService(wsClient)
  val statusService = new StatusService(isItUpService)

  def index = Action { implicit request =>
    val x: Request[AnyContent] = request
    Ok("""{"json":"example"}""").as("application/json")
  }

  def isGoogleUp = Action.async { implicit request =>
    IsUpRequest("http://www.google.com") ~> isItUpService ~>  toResponseForPlay[IsUpResult]
  }

  def status = Action.async { implicit request =>
     StatusRequest(List("http://www.google.com", "http://www.slack.com")) ~> statusService ~> toResponseForPlay[StatusResponse]
  }

  def ping = Action { implicit request =>
    Ok("pong").as("text/plain")
  }
}
