package com.tlmurphy.shortZio

import zio.*
import zio.http.*
import zio.json.*
import zio.http.HttpAppMiddleware.cors
import zio.http.internal.middlewares.Cors.CorsConfig
import models.*

object ShortApp:
  def apply(): HttpApp[UrlRepo, Throwable] =
    Http.collectZIO[Request] {
      case Method.GET -> Root / "urls" =>
        UrlRepo.getAll
          .map(repo => Response.json(GetAllResponse(repo.values.toList).toJson))

      case Method.GET -> Root / "urls" / url =>
        UrlRepo
          .get(url)
          .map {
            case Some(u) =>
              Response.json(
                GetResponse(s"$u successfully retrieved", u).toJson
              )
            case None => Response.status(Status.NotFound)
          }

      case req @ Method.POST -> Root / "urls" =>
        for {
          body <- req.body.asString.map(_.fromJson[PostBody])
          req <- body match {
            case Left(e) =>
              ZIO
                .debug(s"Failed to parse the input: $e")
                .as(Response.text(e).withStatus(Status.BadRequest))
            case Right(b) =>
              for {
                short <- ShortUrlGenerator.generateShortUrl
                shortUrl = ShortUrl(short, b.url)
                addResult <- UrlRepo.add(shortUrl)
              } yield Response
                .json(
                  CreateResponse(
                    s"$shortUrl successfully created",
                    shortUrl
                  ).toJson
                )
                .withStatus(Status.Created)
          }
        } yield req

      case Method.DELETE -> Root / "urls" / url =>
        UrlRepo
          .remove(url)
          .map(_ =>
            Response.json(
              DeleteResponse(s"Short URL $url successfully deleted.").toJson
            )
          )

      case Method.GET -> Root / shortUrl =>
        UrlRepo
          .get(shortUrl)
          .map {
            case Some(u) =>
              Response.redirect(URL(Path.decode(u.originalUrl)), true)
            case None => Response.status(Status.NotFound)
          }
    } @@ cors(CorsConfig())
