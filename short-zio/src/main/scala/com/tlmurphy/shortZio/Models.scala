package com.tlmurphy.shortZio

object Models:
  type Repo = Map[String, ShortUrl]
  case class ShortUrl(shortUrl: String, originalUrl: String) {
    override def toString(): String = s"($shortUrl) -> ($originalUrl)"
  }
