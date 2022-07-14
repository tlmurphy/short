package com.tlmurphy.short

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout

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

  def getUrls: Future[Urls] =
    urlRegistry.ask(GetUrls)
  def getUrl(name: String): Future[GetUrlResponse] =
    urlRegistry.ask(GetUrl(name, _))
  def createUrl(url: Url): Future[ActionPerformed] =
    urlRegistry.ask(CreateUrl(url, _))
  def deleteUrl(name: String): Future[ActionPerformed] =
    urlRegistry.ask(DeleteUrl(name, _))

  val urlRoutes: Route =
    pathPrefix("urls") {
      concat(
        pathEnd {
          concat(
            get {
              complete(getUrls)
            },
            post {
              entity(as[Url]) { url =>
                onSuccess(createUrl(url)) { performed =>
                  complete((StatusCodes.Created, performed))
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
    }
}
