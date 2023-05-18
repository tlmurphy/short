package com.tlmurphy.shortHttp4s

import org.http4s.Uri
import cats.effect.{IO, Ref}

object Models:
  type Repo = Map[String, ShortUrl]
  case class ShortUrl(shortUrl: String, originalUrl: String) {
    override def toString(): String = s"($shortUrl) -> ($originalUrl)"
  }
