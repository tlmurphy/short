package com.tlmurphy.shortHttp4s

import cats.effect.{IO, Resource}
import cats.syntax.all.*
import org.http4s.*
import org.http4s.implicits.*
import munit.CatsEffectSuite
import com.tlmurphy.shortHttp4s.Models.{ShortUrl, Repo}
import munit.catseffect.IOFixture

class UrlServiceSpec extends CatsEffectSuite:

  private val abcUrl = ShortUrl(uri"https://test.com", "abc")
  private val defUrl = ShortUrl(uri"https://anothertest.com", "def")
  private val repo = Map(
    "abc" -> abcUrl,
    "def" -> defUrl
  )

  private val repoFixture = ResourceTestLocalFixture(
    "repo-fixture",
    Resource.make(IO.ref(repo))(_ => IO.unit)
  )

  override def munitFixtures: List[IOFixture[_]] = List(repoFixture)

  test("add") {
    val url = ShortUrl(uri"https://cool", "trevor")
    UrlService
      .add(url, repoFixture())
      .assertEquals(repo + ("trevor" -> url))
  }

  test("remove") {
    UrlService
      .remove("abc", repoFixture())
      .assertEquals(repo - "abc")
  }

  test("add multiple then remove") {
    val url = ShortUrl(uri"https://cool", "trevor")
    val url2 = ShortUrl(uri"https://ok", "murphy")
    for {
      _ <- List(
        UrlService.add(url, repoFixture()),
        UrlService.add(url2, repoFixture())
      ).parSequence
      r <- UrlService.remove(url.shortened, repoFixture())
    } yield {
      assertEquals(r, repo + ("murphy" -> url2))
    }
  }

  test("get returns some url") {
    UrlService
      .get("def", repoFixture())
      .assertEquals(Some(defUrl))
  }

  test("get returns None for non-existend url") {
    UrlService
      .get("bad", repoFixture())
      .assertEquals(None)
  }

  test("getAll") {
    UrlService
      .getAll(repoFixture())
      .assertEquals(repo)
  }
