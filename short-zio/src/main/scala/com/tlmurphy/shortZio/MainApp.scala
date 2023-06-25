package com.tlmurphy.shortZio

import zio.*
import zhttp.service.Server

object MainApp extends ZIOAppDefault:
  def run =
    Server
      .start(port = 8081, http = ShortApp())
      .provide(InMemoryUrlRepo.layer)
