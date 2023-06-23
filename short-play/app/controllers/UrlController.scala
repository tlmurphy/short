package controllers

import javax.inject._
import play.api.mvc._
import services.UrlService
import models.Models._
import cats.effect.unsafe.implicits.global
import play.api.libs.json.Json.toJson
import cats.effect.{IO, Ref}

@Singleton
class UrlController @Inject() (
    val controllerComponents: ControllerComponents,
    urlService: UrlService
) extends BaseController {

  private val ref: Ref[IO, Map[String, ShortUrl]] =
    Ref.unsafe(Map.empty[String, ShortUrl])

  def create() = Action.async { request =>
    val body = request.body.asJson.get
    val url = body.as[PostBody]
    (for {
      short <- urlService.generateShortUrl
      shortUrl = ShortUrl(short, url.url)
      res <- urlService
        .add(shortUrl, ref)
        .map(_ =>
          Created(
            toJson(
              CreateResponse(s"$shortUrl successfully created", shortUrl)
            )
          )
        )
    } yield res).unsafeToFuture()
  }

  def getAll = Action.async {
    urlService
      .getAll(ref)
      .map(res => Ok(toJson(GetAllResponse(res.values.toList))))
      .unsafeToFuture()
  }

  def get(shortUrl: String) = Action.async {
    urlService
      .get(shortUrl, ref)
      .map({
        case Some(u) =>
          Ok(toJson(GetResponse(s"$u successfully retrieved", Some(u))))
        case None =>
          NotFound(toJson(GetResponse(s"$shortUrl does not exist")))
      })
      .unsafeToFuture()
  }

  def delete(shortUrl: String) = Action.async {
    urlService
      .remove(shortUrl, ref)
      .map(_ =>
        Ok(
          toJson(
            DeleteResponse(s"Short URL $shortUrl successfully deleted.")
          )
        )
      )
      .unsafeToFuture()
  }

  def resolve(shortUrl: String) = Action.async {
    urlService
      .get(shortUrl, ref)
      .map({
        case Some(u) => Redirect(u.originalUrl)
        case None    => NotFound
      })
      .unsafeToFuture()
  }
}
