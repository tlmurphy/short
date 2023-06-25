package com.tlmurphy.shortZio

import zio.*
import models.{Repo, ShortUrl}

final case class InMemoryUrlRepo(repo: Ref[Repo]) extends UrlRepo:
  def add(url: ShortUrl): UIO[Repo] =
    repo.updateAndGet(_ + (url.shortUrl -> url))

  def remove(url: String): UIO[Repo] =
    repo.updateAndGet(_ - url)

  def get(urlKey: String): UIO[Option[ShortUrl]] =
    repo.get.map(_.get(urlKey))

  def getAll: UIO[Repo] =
    repo.get

object InMemoryUrlRepo {
  def layer: ZLayer[Any, Nothing, InMemoryUrlRepo] =
    ZLayer.fromZIO(
      Ref.make(Map.empty[String, ShortUrl]).map(new InMemoryUrlRepo(_))
    )
}
