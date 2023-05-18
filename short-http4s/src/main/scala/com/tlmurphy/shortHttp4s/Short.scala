package com.tlmurphy.shortHttp4s

import cats.effect.{ExitCode, IO, IOApp, Ref, Resource}
import org.http4s.ember.server.EmberServerBuilder
import com.comcast.ip4s.*
import org.http4s.implicits.*
import org.http4s.server.middleware.Logger
import org.http4s.HttpApp
import com.tlmurphy.shortHttp4s.Models.Repo
import com.tlmurphy.shortHttp4s.Models.ShortUrl

object Short extends IOApp.Simple:
  override def run: IO[Unit] =
    for
      repo <- IO.ref(Map.empty[String, ShortUrl])
      httpApp = Logger.httpApp(true, true)(Routes.routes(repo).orNotFound)
      _ <- EmberServerBuilder
        .default[IO]
        .withHost(ipv4"0.0.0.0")
        .withPort(port"8081")
        .withHttpApp(httpApp)
        .build
        .useForever
    yield ()
