package com.tlmurphy.shortHttp4s

import cats.effect.{IO, Ref}
import com.tlmurphy.shortHttp4s.Models.{ShortUrl, Repo}

object UrlService:
  def add(url: ShortUrl, repo: Ref[IO, Repo]): IO[Repo] =
    repo.updateAndGet(r => r + (url.shortened -> url))

  def remove(url: String, repo: Ref[IO, Repo]): IO[Repo] =
    repo.updateAndGet(r => r - url)

  def get(urlKey: String, repo: Ref[IO, Repo]): IO[Option[ShortUrl]] =
    repo.get.map(r => r.get(urlKey))

  def getAll(repo: Ref[IO, Repo]): IO[Repo] =
    repo.get
