package com.tlmurphy.short

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route

import scala.concurrent.Future
import com.tlmurphy.short.UrlRegistry._
import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout

//#import-json-formats
//#user-routes-class
class UserRoutes(userRegistry: ActorRef[UrlRegistry.Command])(implicit
    val system: ActorSystem[_]
) {

  //#user-routes-class
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import JsonFormats._
  //#import-json-formats

  // If ask takes more time than this to complete the request is failed
  private implicit val timeout = Timeout.create(
    system.settings.config.getDuration("my-app.routes.ask-timeout")
  )

  def getUsers(): Future[Urls] =
    userRegistry.ask(GetUrls)
  def getUser(name: String): Future[GetUrlResponse] =
    userRegistry.ask(GetUrl(name, _))
  def createUser(user: Url): Future[ActionPerformed] =
    userRegistry.ask(CreateUrl(user, _))
  def deleteUser(name: String): Future[ActionPerformed] =
    userRegistry.ask(DeleteUrl(name, _))

  //#all-routes
  //#users-get-post
  //#users-get-delete
  val userRoutes: Route =
    pathPrefix("users") {
      concat(
        //#users-get-delete
        pathEnd {
          concat(
            get {
              complete(getUsers())
            },
            post {
              entity(as[Url]) { user =>
                onSuccess(createUser(user)) { performed =>
                  complete((StatusCodes.Created, performed))
                }
              }
            }
          )
        },
        //#users-get-delete
        //#users-get-post
        path(Segment) { name =>
          concat(
            get {
              //#retrieve-user-info
              rejectEmptyResponse {
                onSuccess(getUser(name)) { response =>
                  complete(response.maybeUrl)
                }
              }
              //#retrieve-user-info
            },
            delete {
              //#users-delete-logic
              onSuccess(deleteUser(name)) { performed =>
                complete((StatusCodes.OK, performed))
              }
              //#users-delete-logic
            }
          )
        }
      )
      //#users-get-delete
    }
  //#all-routes
}
