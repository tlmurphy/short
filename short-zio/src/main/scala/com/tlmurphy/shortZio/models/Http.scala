package com.tlmurphy.shortZio.models

import zio.json.*

case class GetAllResponse(urls: List[ShortUrl])
case class GetResponse(message: String, url: Option[ShortUrl] = None)
case class CreateResponse(message: String, url: ShortUrl)
case class DeleteResponse(message: String)
case class PostBody(url: String)

object GetAllResponse:
  given JsonDecoder[GetAllResponse] =
    DeriveJsonDecoder.gen[GetAllResponse]

object GetResponse:
  given JsonDecoder[GetResponse] =
    DeriveJsonDecoder.gen[GetResponse]

object CreateResponse:
  given JsonDecoder[CreateResponse] =
    DeriveJsonDecoder.gen[CreateResponse]

object DeleteResponse:
  given JsonDecoder[DeleteResponse] =
    DeriveJsonDecoder.gen[DeleteResponse]

object PostBody:
  given JsonDecoder[PostBody] =
    DeriveJsonDecoder.gen[PostBody]
