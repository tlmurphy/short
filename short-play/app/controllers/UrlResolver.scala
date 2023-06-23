package controllers

import javax.inject._
import play.api.mvc._

@Singleton
class UrlController @Inject() (val controllerComponents: ControllerComponents)
    extends BaseController {

  def create() = Action {
    Ok("create")
  }

  def getAll = Action {
    Ok("getAll")
  }

  def get(shortUrl: String) = Action {
    Ok(s"get: $shortUrl")
  }

  def delete(shortUrl: String) = Action {
    Ok(s"delete $shortUrl")
  }

  def resolve(shorUrl: String) = Action {
    Ok(s"Short URL: $shorUrl")
  }
}
