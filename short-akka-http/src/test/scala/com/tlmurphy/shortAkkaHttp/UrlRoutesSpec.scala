package com.tlmurphy.shortAkkaHttp

import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.tlmurphy.shortAkkaHttp.ResponseModels._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import com.tlmurphy.shortAkkaHttp.UrlRegistry.Url

class UrlRoutesSpec
    extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with ScalatestRouteTest {
  lazy val testKit: ActorTestKit = ActorTestKit()
  implicit def typedSystem: ActorSystem[Nothing] = testKit.system
  override def createActorSystem(): akka.actor.ActorSystem =
    testKit.system.classicSystem

  val urlRegistry: ActorRef[UrlRegistry.Command] = testKit.spawn(UrlRegistry())
  lazy val routes: Route = new UrlRoutes(urlRegistry).urlRoutes

  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import JsonFormats._

  "UrlRoutes" should {
    "be able to get urls" in {
      Get("/urls") ~> routes ~> check {
        status shouldEqual StatusCodes.OK
      }
    }

    "be able to add a url mapping" in {
      val urlEntity =
        Marshal(Url("https://google.com")).to[MessageEntity].futureValue

      Post("/urls").withEntity(urlEntity) ~> routes ~> check {
        status shouldEqual StatusCodes.Created
      }
    }

    /** The redirect test and delete tests are not good practice, as they rely
      * on the POST functionality to work. The better way would be to mock out
      * the registry somehow or to insert a ShortUrl directly into it so that
      * the tests don't rely on POST working.
      */
    // TODO: Remove POST dependency
    "be able to redirect given a short url" in {
      val urlEntity =
        Marshal(Url("https://google.com")).to[MessageEntity].futureValue

      Post("/urls").withEntity(urlEntity) ~> routes ~> check {
        val response = entityAs[ResponseSuccess]
        Get(s"/${response.url.get.shortUrl}") ~> routes ~> check {
          status shouldEqual StatusCodes.PermanentRedirect
        }
      }
    }

    // TODO: Remove POST dependency
    "be able to delete a url mapping" in {
      val urlEntity =
        Marshal(Url("https://google.com")).to[MessageEntity].futureValue

      Post("/urls").withEntity(urlEntity) ~> routes ~> check {
        val response = entityAs[ResponseSuccess]
        Delete(s"/urls/${response.url.get.shortUrl}") ~> routes ~> check {
          status shouldEqual StatusCodes.OK
        }
      }
    }

    "return a 404 when deleting a url mapping that does not exist" in {
      Delete(s"/urls/veryshort") ~> routes ~> check {
        status shouldEqual StatusCodes.NotFound
      }
    }

    "return a 404 when given a url that does that exist in the url mapping" in {
      Get("/veryshort") ~> routes ~> check {
        status shouldEqual StatusCodes.NotFound
      }
    }

    "return a 400 when attempting to create a url mapping that contains an invalid url" in {
      val urlEntity = Marshal(Url("badurl")).to[MessageEntity].futureValue

      Post("/urls").withEntity(urlEntity) ~> routes ~> check {
        status shouldEqual StatusCodes.BadRequest
      }
    }

    "return a 400 when attempting to create a url mapping that already exists" in {
      val urlEntity =
        Marshal(Url("https://google.com")).to[MessageEntity].futureValue

      Post("/urls").withEntity(urlEntity) ~> routes ~> check {
        Post("/urls").withEntity(urlEntity) ~> routes ~> check {
          status shouldEqual StatusCodes.BadRequest
        }
      }
    }
  }
}
