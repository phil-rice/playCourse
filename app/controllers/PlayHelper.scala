package controllers

import play.api.mvc.{AnyContent, Request, Result}
import play.mvc.Http.Response


trait BuildResponseForPlay[T] {
  def apply(t: T)(implicit request: Request[AnyContent]): Result
}

object BuildResponseForPlay {
  def apply[T](t: T)(implicit request: Request[AnyContent], buildResponseForPlay: BuildResponseForPlay[T]) =
    buildResponseForPlay(t)
}