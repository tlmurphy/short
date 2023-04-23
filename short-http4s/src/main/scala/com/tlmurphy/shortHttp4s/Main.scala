package com.tlmurphy.shortHttp4s

import cats.effect.{ExitCode, IO, IOApp}

object Main extends IOApp.Simple:
  val run: IO[Nothing] = Shorthttp4sServer.run[IO]
