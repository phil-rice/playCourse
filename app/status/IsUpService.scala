package status

import controllers.BuildResponseForPlay
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}
import play.api.mvc.{AnyContent, Request, Results}

import scala.concurrent.ExecutionContext

case class IsUpRequest(url: String)

object IsUpRequest {
  implicit object BuildRequestForStatusRequest extends BuildRequestFrom[IsUpRequest] {
    override def apply(ws: WSClient)(t: IsUpRequest): WSRequest = ws.url(t.url)
  }
}

case class IsUpResult(url: String, up: Boolean)

object IsUpResult {

    implicit object BuildResponseForIsUpResult extends BuildFromResponse[IsUpRequest, IsUpResult] {
      override def apply(req: IsUpRequest)(res: WSResponse): IsUpResult =
        res.status match {
          case 200 => IsUpResult(req.url, true)
          case _ => IsUpResult(req.url, false)
        }
    }

  implicit object BuildResponseForPlayForIsUpResult extends BuildResponseForPlay[IsUpResult] with Results {
    override def apply(t: IsUpResult)(implicit request: Request[AnyContent]) =
      if (t.up) Ok("Google is Up") else BadGateway("Google is Down")
  }

}

class IsUpService(ws: WSClient)(implicit ex: ExecutionContext) extends HttpService[IsUpRequest, IsUpResult](ws)
