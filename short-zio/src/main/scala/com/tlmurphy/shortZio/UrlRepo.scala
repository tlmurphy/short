package com.tlmurphy.shortZio

import zio.*
import Models.*

trait UrlRepo {
  def add(url: ShortUrl): Task[Repo]
  def remove(url: String): Task[Repo]
  def get(urlKey: String): Task[Option[ShortUrl]]
  def getAll: Task[Repo]
}

object UrlRepo {
  def add(url: ShortUrl): ZIO[UrlRepo, Throwable, Repo] =
    ZIO.serviceWithZIO[UrlRepo](_.add(url))

  def remove(url: String): ZIO[UrlRepo, Throwable, Repo] =
    ZIO.serviceWithZIO[UrlRepo](_.remove(url))

  def get(urlKey: String): ZIO[UrlRepo, Throwable, Option[ShortUrl]] =
    ZIO.serviceWithZIO[UrlRepo](_.get(urlKey))

  def getAll: ZIO[UrlRepo, Throwable, Repo] =
    ZIO.serviceWithZIO[UrlRepo](_.getAll)
}
