package com.tlmurphy.short

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._

import scala.concurrent.Future
import com.tlmurphy.short.UrlRegistry._

class UrlRoutes(urlRegistry: ActorRef[UrlRegistry.Command])(implicit
    val system: ActorSystem[_]
) {
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import JsonFormats._

  private implicit val timeout: Timeout = Timeout.create(
    system.settings.config.getDuration("my-app.routes.ask-timeout")
  )

  def getUrls: Future[ShortUrls] =
    urlRegistry.ask(GetShortUrls)
  def getUrl(name: String): Future[Either[GetFailure, GetSuccess]] =
    urlRegistry.ask(GetShortUrl(name, _))
  def createUrl(url: Url): Future[Either[CreateFailure, CreateSuccess]] =
    urlRegistry.ask(CreateShortUrl(url, _))
  def deleteUrl(name: String): Future[Either[DeleteFailure, DeleteSuccess]] =
    urlRegistry.ask(DeleteShortUrl(name, _))
  def resolveShortUrl(
      name: String
  ): Future[Either[ResolveShortUrlFailure, ResolveShortUrlSuccess]] =
    urlRegistry.ask(ResolveShortUrl(name, _))

  val urlRoutes: Route = {
    cors() {
      concat(
        pathPrefix("urls") {
          concat(
            pathEnd {
              concat(
                get {
                  complete(getUrls)
                },
                post {
                  entity(as[Url]) { originalUrl =>
                    onSuccess(createUrl(originalUrl)) {
                      case Right(success) =>
                        complete(StatusCodes.Created, success.shortUrl)
                      case Left(failure) =>
                        complete(
                          StatusCodes.BadRequest,
                          ResponseFailure(failure.description)
                        )
                    }
                  }
                }
              )
            },
            path(Segment) { name =>
              concat(
                get {
                  onSuccess(getUrl(name)) {
                    case Right(success) =>
                      complete(StatusCodes.OK, success.shortUrl)
                    case Left(failure) =>
                      complete(
                        StatusCodes.NotFound,
                        ResponseFailure(failure.description)
                      )
                  }
                },
                delete {
                  onSuccess(deleteUrl(name)) {
                    case Right(success) =>
                      complete(StatusCodes.OK, success.description)
                    case Left(failure) =>
                      complete(
                        StatusCodes.NotFound,
                        failure.description
                      )
                  }
                }
              )
            }
          )
        },
        path(Remaining) { url =>
          onSuccess(resolveShortUrl(url)) {
            case Right(success) =>
              redirect(success.shortUrl, StatusCodes.PermanentRedirect)
            case Left(failure) =>
              complete(
                StatusCodes.NotFound,
                ResponseFailure(failure.description)
              )
          }
        }
      )
    }
  }
}
