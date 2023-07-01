package com.tlmurphy.shortZio

import zio.*
import zio.http.*

object MainApp extends ZIOAppDefault:
  def run = Server
    .serve(ShortApp().withDefaultErrorResponse)
    .provide(Server.defaultWithPort(8081), InMemoryUrlRepo.layer)
