package com.tlmurphy.shortHttp4s

import cats.effect.*
import org.http4s.*
import org.http4s.dsl.io.*
import com.tlmurphy.shortHttp4s.Models.Repo
import io.circe.generic.auto.*
import org.http4s.circe.*
import io.circe.syntax.*
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import com.tlmurphy.shortHttp4s.Models.ShortUrl

case class PostBody(url: String)
implicit val postBodyDecoder: EntityDecoder[cats.effect.IO, PostBody] =
  jsonOf[IO, PostBody]

object Routes:
  def routes(repo: Ref[IO, Repo]): HttpRoutes[IO] =
    HttpRoutes.of[IO] {
      case GET -> Root / "urls" =>
        val listResp: IO[Map[String, List[ShortUrl]]] =
          UrlService.getAll(repo).map(u => Map("urls" -> u.values.toList))
        Ok(listResp)

      case req @ POST -> Root / "urls" =>
        for
          shortUrl <- UrlService.generateShortUrl
          body <- req.as[PostBody]
          resp <- Ok(UrlService.add(ShortUrl(shortUrl, body.url), repo))
        yield resp
    }
