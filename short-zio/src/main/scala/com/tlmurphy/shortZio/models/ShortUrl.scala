package com.tlmurphy.shortZio.models

import zio.json.*

case class ShortUrl(shortUrl: String, originalUrl: String):
  override def toString(): String = s"($shortUrl) -> ($originalUrl)"

object ShortUrl:
  given JsonEncoder[ShortUrl] =
    DeriveJsonEncoder.gen[ShortUrl]
  given JsonDecoder[ShortUrl] =
    DeriveJsonDecoder.gen[ShortUrl]
