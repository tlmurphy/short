package services

import cats.effect.{IO, Ref}
import models.Models.{ShortUrl, Repo}
import cats.effect.std.Random
import cats.syntax.all._
import javax.inject._

@Singleton
class UrlService {
  def add(url: ShortUrl, ref: Ref[IO, Repo]): IO[Repo] =
    ref.updateAndGet(r => r + (url.shortUrl -> url))

  def remove(url: String, ref: Ref[IO, Repo]): IO[Repo] =
    ref.updateAndGet(r => r - url)

  def get(urlKey: String, ref: Ref[IO, Repo]): IO[Option[ShortUrl]] =
    ref.get.map(r => r.get(urlKey))

  def getAll(ref: Ref[IO, Repo]): IO[Repo] =
    ref.get

  def generateShortUrl: IO[String] =
    Random
      .scalaUtilRandom[IO]
      .flatMap(x =>
        List.fill(7)(x.nextAlphaNumeric).parSequence.map(_.mkString)
      )
}
