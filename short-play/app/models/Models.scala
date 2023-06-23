package models
import play.api.libs.json._

object Models {
  type Repo = Map[String, ShortUrl]
  case class ShortUrl(shortUrl: String, originalUrl: String) {
    override def toString(): String = s"($shortUrl) -> ($originalUrl)"
  }
  case class GetAllResponse(urls: List[ShortUrl])
  case class GetResponse(message: String, url: Option[ShortUrl] = None)
  case class CreateResponse(message: String, url: ShortUrl)
  case class DeleteResponse(message: String)
  case class PostBody(url: String)

  implicit val shortUrlWrites = Json.writes[ShortUrl]
  implicit val getAllWrites = Json.writes[GetAllResponse]
  implicit val getWrites = Json.writes[GetResponse]
  implicit val createWrites = Json.writes[CreateResponse]
  implicit val deleteWrites = Json.writes[DeleteResponse]
  implicit val postBodyReads = Json.reads[PostBody]
}
