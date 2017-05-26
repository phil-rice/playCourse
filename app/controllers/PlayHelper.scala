package controllers

import play.api.mvc.{AnyContent, Request, Result}


trait BuildResponseForPlay[T] {
  def apply(t: T)(implicit request: Request[AnyContent]): Result
}

object BuildResponseForPlay {
  def toResponseForPlay[T](t: T)(implicit request: Request[AnyContent], buildResponseForPlay: BuildResponseForPlay[T]) =
    buildResponseForPlay(t)
}