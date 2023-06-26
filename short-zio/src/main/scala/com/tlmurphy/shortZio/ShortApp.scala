package com.tlmurphy.shortZio

import zhttp.http.*
import zio.*
import zio.json.*
import models.*

object ShortApp:
  def apply(): Http[UrlRepo, Throwable, Request, Response] =
    Http.collectZIO[Request] {
      case Method.GET -> !! / "urls" =>
        UrlRepo.getAll
          .map(repo => Response.json(GetAllResponse(repo.values.toList).toJson))

      case Method.GET -> !! / "urls" / url =>
        UrlRepo
          .get(url)
          .map {
            case Some(u) =>
              Response.json(
                GetResponse(s"$u successfully retrieved", u).toJson
              )
            case None => Response.status(Status.NotFound)
          }

      case req @ Method.POST -> !! / "urls" =>
        for {
          body <- req.body.asString.map(_.fromJson[PostBody])
          req <- body match {
            case Left(e) =>
              ZIO
                .debug(s"Failed to parse the input: $e")
                .as(Response.text(e).setStatus(Status.BadRequest))
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
                .setStatus(Status.Created)
          }
        } yield req

      case Method.DELETE -> !! / "urls" / url =>
        UrlRepo
          .remove(url)
          .map(_ =>
            Response.json(
              DeleteResponse(s"Short URL $url successfully deleted.").toJson
            )
          )

      case Method.GET -> !! / shortUrl =>
        UrlRepo.get(shortUrl).map {
          case Some(u) => Response.redirect(u.originalUrl, true)
          case None    => Response.status(Status.NotFound)
        }
    }
