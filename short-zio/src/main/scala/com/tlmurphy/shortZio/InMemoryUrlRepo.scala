package com.tlmurphy.shortZio

import zio.*
import Models.*

final case class InMemoryUrlRepo(repo: Ref[Repo]) extends UrlRepo:
  def add(url: ShortUrl): Task[Repo] = ???
  def remove(url: String): Task[Repo] = ???
  def get(urlKey: String): Task[Option[ShortUrl]] = ???
  def getAll: Task[Repo] = ???

object InMemoryUrlRepo {
  def layer: ZLayer[Any, Nothing, InMemoryUrlRepo] =
    ZLayer.fromZIO(
      Ref.make(Map.empty[String, ShortUrl]).map(new InMemoryUrlRepo(_))
    )
}
