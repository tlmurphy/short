package com.tlmurphy.shorthttp4s

import cats.effect.{ExitCode, IO, IOApp}

object Main extends IOApp.Simple:
  val run = Shorthttp4sServer.run[IO]
