package status

import controllers.BuildResponseForPlay
import play.api.libs.ws.WSClient
import play.api.mvc.{AnyContent, Request, Result, Results}

import scala.collection.immutable
import scala.concurrent.{ExecutionContext, Future}

case class StatusRequest(urls: List[String])

object StatusRequest {
  def makeIsItUpRequests(req: StatusRequest) = req.urls.map(IsUpRequest(_))
}

case class StatusResponse(ups: Seq[IsUpResult])

object StatusResponse {
  def toStatusResponse = { ups: Seq[IsUpResult] => StatusResponse(ups) }

  implicit object BuildResponseForPlayForStatusResponse extends BuildResponseForPlay[StatusResponse] with Results {
    override def apply(res: StatusResponse)(implicit request: Request[AnyContent]): Result = {
      val result = res.ups.map { case IsUpResult(url, up) => url + ": " + up }.mkString("\n")
      (res.ups.foldLeft(true)(_ && _.up) match {
        case true => Ok(result)
        case false => BadGateway(result)
      }).as("text/plain")
    }
  }

}

class OriginalStatusService(isUpService: IsUpService)(implicit executionContext: ExecutionContext) extends (StatusRequest => Future[StatusResponse]) {

  override def apply(req: StatusRequest): Future[StatusResponse] = {
    val futureOfResults: Seq[Future[IsUpResult]] =
      req.urls.
        map(IsUpRequest(_)).
        map(isUpService)
    //the type of futureOfResults is neither use nor ornament

    Future.sequence(futureOfResults).map(StatusResponse(_))
  }
}


class StatusService(isUpService: IsUpService)(implicit executionContext: ExecutionContext) extends (StatusRequest => Future[StatusResponse]) with Arrow {

  import StatusRequest._
  import StatusResponse._

  def apply(req: StatusRequest) = req ~> makeIsItUpRequests ~> isUpService ~> toStatusResponse
}