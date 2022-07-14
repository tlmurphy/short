package com.tlmurphy.short

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{RejectionHandler, Route, ValidationRejection}
import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._

import scala.concurrent.Future
import scala.util.Try
import java.net.URL
import com.tlmurphy.short.UrlRegistry._

class UrlRoutes(urlRegistry: ActorRef[UrlRegistry.Command])(implicit
    val system: ActorSystem[_]
) {
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import JsonFormats._

  private implicit val timeout: Timeout = Timeout.create(
    system.settings.config.getDuration("my-app.routes.ask-timeout")
  )

  def getUrls: Future[Urls] =
    urlRegistry.ask(GetUrls)
  def getUrl(name: String): Future[GetUrlResponse] =
    urlRegistry.ask(GetUrl(name, _))
  def createUrl(url: Url): Future[ActionPerformed] =
    urlRegistry.ask(CreateUrl(url, _))
  def deleteUrl(name: String): Future[ActionPerformed] =
    urlRegistry.ask(DeleteUrl(name, _))
  def resolveShortUrl(name: String): Future[ResolveShortUrlResponse] =
    urlRegistry.ask(ResolveShortUrl(name, _))

  def validateUrl(url: String): Boolean = Try(new URL(url).toURI).isSuccess

  def rejectionHandler: RejectionHandler =
    RejectionHandler
      .newBuilder()
      .handle { case ValidationRejection(message, _) =>
        complete(StatusCodes.BadRequest, message)
      }
      .result()

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
                handleRejections(rejectionHandler) {
                  post {
                    entity(as[Url]) { url =>
                      validate(validateUrl(url.originalUrl), "uh oh :)") {
                        onSuccess(createUrl(url)) { performed =>
                          complete(StatusCodes.Created, performed)
                        }
                      }
                    }
                  }
                }
              )
            },
            path(Segment) { name =>
              concat(
                get {
                  rejectEmptyResponse {
                    onSuccess(getUrl(name)) { response =>
                      complete(response.maybeUrl)
                    }
                  }
                },
                delete {
                  onSuccess(deleteUrl(name)) { performed =>
                    complete((StatusCodes.OK, performed))
                  }
                }
              )
            }
          )
        },
        path(Remaining) { url =>
          onSuccess(resolveShortUrl(url)) { res =>
            res.maybeShortUrl match {
              case Some(url) => redirect(url, StatusCodes.PermanentRedirect)
              case None =>
                complete(
                  StatusCodes.NotFound,
                  "The requested resource could not be found."
                )
            }
          }
        }
      )
    }
  }
}
