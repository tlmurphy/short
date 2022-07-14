package com.tlmurphy.short

import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.{Route, ValidationRejection}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class UrlRoutesSpec
    extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with ScalatestRouteTest {
  lazy val testKit: ActorTestKit = ActorTestKit()
  implicit def typedSystem: ActorSystem[Nothing] = testKit.system
  override def createActorSystem(): akka.actor.ActorSystem =
    testKit.system.classicSystem

  val userRegistry: ActorRef[UrlRegistry.Command] = testKit.spawn(UrlRegistry())
  lazy val routes: Route = new UrlRoutes(userRegistry).urlRoutes

  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import JsonFormats._

  "UrlRoutes" should {
    "return no urls if none are present (GET /urls)" in {
      val request = HttpRequest(uri = "/urls")

      request ~> routes ~> check {
        status shouldEqual StatusCodes.OK
        contentType shouldEqual ContentTypes.`application/json`
        entityAs[String] shouldEqual """{"urls":[]}"""
      }
    }

    "be able to add urls (POST /urls)" in {
      val url = Url("veryshort", "https://google.com")
      val urlEntity =
        Marshal(url)
          .to[MessageEntity]
          .futureValue

      val request = Post("/urls").withEntity(urlEntity)

      request ~> routes ~> check {
        status shouldEqual StatusCodes.Created
        contentType shouldEqual ContentTypes.`application/json`
        entityAs[
          String
        ] shouldEqual """{"description":"Short URL veryshort created."}"""
      }
    }

    "be able to redirect given a short URL" in {
      val request = Get("/veryshort")

      request ~> routes ~> check {
        status shouldEqual StatusCodes.PermanentRedirect
      }
    }

    "be able to remove urls (DELETE /urls)" in {
      val request = Delete(uri = "/urls/veryshort")

      request ~> routes ~> check {
        status shouldEqual StatusCodes.OK
        contentType shouldEqual ContentTypes.`application/json`
        entityAs[
          String
        ] shouldEqual """{"description":"Short URL veryshort deleted."}"""
      }
    }

    "return a 404 when given a URL that does that exist in the URL mapping" in {
      val request = Get("/veryshort")

      request ~> routes ~> check {
        status shouldEqual StatusCodes.NotFound
      }
    }

    "return a 400 when attempting to create a url mapping that contains a non-url" in {
      val url = Url("veryshort", "oh")
      val urlEntity =
        Marshal(url)
          .to[MessageEntity]
          .futureValue

      val request = Post("/urls").withEntity(urlEntity)
      request -> routes -> check {
        rejection shouldEqual ValidationRejection("uh oh :)")
        status shouldEqual StatusCodes.BadRequest
      }
    }
  }
}
