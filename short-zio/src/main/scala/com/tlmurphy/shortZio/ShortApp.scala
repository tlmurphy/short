package com.tlmurphy.shortZio

import zhttp.http.*

object ShortApp {
  def apply(): Http[Any, Nothing, Request, Response] =
    Http.collect[Request] { case Method.GET -> !! / "hello" =>
      Response.text("Hello there!")
    }
}
