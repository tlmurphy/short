package com.tlmurphy.shortZio

import zio.json.*

object Models:
  type Repo = Map[String, ShortUrl]
  case class ShortUrl(shortUrl: String, originalUrl: String) {
    override def toString(): String = s"($shortUrl) -> ($originalUrl)"
  }
  given JsonEncoder[ShortUrl] =
    DeriveJsonEncoder.gen[ShortUrl]
  given JsonDecoder[ShortUrl] =
    DeriveJsonDecoder.gen[ShortUrl]
