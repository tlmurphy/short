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
import org.http4s.headers.Location

case class PostBody(url: String)
implicit val postBodyDecoder: EntityDecoder[cats.effect.IO, PostBody] =
  jsonOf[IO, PostBody]

case class GetAllResponse(urls: List[ShortUrl])
case class GetResponse(message: String, url: Option[ShortUrl] = None)
case class CreateResponse(message: String, url: ShortUrl)
case class DeleteResponse(message: String)

object Routes:
  def routes(repo: Ref[IO, Repo]): HttpRoutes[IO] =
    HttpRoutes.of[IO] {
      case GET -> Root / "urls" =>
        val listResp: IO[GetAllResponse] =
          UrlService.getAll(repo).map(u => GetAllResponse(u.values.toList))
        Ok(listResp)

      case GET -> Root / "urls" / url =>
        UrlService.get(url, repo).flatMap {
          case Some(u) => Ok(GetResponse(s"$u successfully retrieved", Some(u)))
          case None    => NotFound(GetResponse(s"$url does not exist"))
        }

      case req @ POST -> Root / "urls" =>
        for
          genShort <- UrlService.generateShortUrl
          body <- req.as[PostBody]
          shortUrl = ShortUrl(genShort, body.url)
          _ <- UrlService.add(shortUrl, repo)
          resp <- Created(
            CreateResponse(s"$shortUrl successfully created", shortUrl)
          )
        yield resp

      case DELETE -> Root / "urls" / url =>
        UrlService
          .remove(url, repo)
          .flatMap(_ =>
            Ok(DeleteResponse(s"Short URL $url successfully deleted."))
          )

      case GET -> Root / url =>
        UrlService
          .get(url, repo)
          .flatMap {
            case Some(u) =>
              PermanentRedirect(
                url,
                Location(
                  Uri.unsafeFromString(u.originalUrl)
                ) // Should probably handle bad urls somewhere
              )
            case None => NotFound()
          }
    }
