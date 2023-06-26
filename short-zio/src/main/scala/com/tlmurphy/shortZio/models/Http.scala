package com.tlmurphy.shortZio.models

import zio.json.*

case class GetAllResponse(urls: List[ShortUrl])
case class GetResponse(message: String, url: ShortUrl)
case class CreateResponse(message: String, url: ShortUrl)
case class DeleteResponse(message: String)
case class PostBody(url: String)

object GetAllResponse:
  given JsonEncoder[GetAllResponse] =
    DeriveJsonEncoder.gen[GetAllResponse]

object GetResponse:
  given JsonEncoder[GetResponse] =
    DeriveJsonEncoder.gen[GetResponse]

object CreateResponse:
  given JsonEncoder[CreateResponse] =
    DeriveJsonEncoder.gen[CreateResponse]

object DeleteResponse:
  given JsonEncoder[DeleteResponse] =
    DeriveJsonEncoder.gen[DeleteResponse]

object PostBody:
  given JsonDecoder[PostBody] =
    DeriveJsonDecoder.gen[PostBody]
