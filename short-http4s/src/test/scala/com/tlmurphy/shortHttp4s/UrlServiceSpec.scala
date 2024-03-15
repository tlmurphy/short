package com.tlmurphy.shortHttp4s

import cats.effect.{IO, Resource}
import cats.syntax.all.*
import munit.CatsEffectSuite
import com.tlmurphy.shortHttp4s.Models.{ShortUrl, Repo}
import munit.catseffect.IOFixture

class UrlServiceSpec extends CatsEffectSuite:

  private val abcUrl = ShortUrl("abc", "https://test.com")
  private val defUrl = ShortUrl("def", "https://anothertest.com")
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
    val url = ShortUrl("trevor", "https://cool")
    UrlService
      .add(url, repoFixture())
      .assertEquals(repo + ("trevor" -> url))
  }

  test("remove") {
    UrlService
      .remove("abc", repoFixture())
      .assertEquals(repo - "abc")
  }

  test("remove doesn't exist") {
    UrlService
      .remove("hello", repoFixture())
      .assertEquals(repo)
  }

  test("add multiple then remove") {
    val url = ShortUrl("trevor", "https://cool")
    val url2 = ShortUrl("murphy", "https://ok")
    for
      _ <- List(
        UrlService.add(url, repoFixture()),
        UrlService.add(url2, repoFixture())
      ).parSequence
      r <- UrlService.remove(url.shortUrl, repoFixture())
    yield assertEquals(r, repo + ("murphy" -> url2))
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

  test("generateShortUrl generates a 7 length alphanumeric string") {
    UrlService.generateShortUrl
      .map(s => s.length == 7 && s.forall(_.isLetterOrDigit))
      .assertEquals(true)
  }

  test("generateShortUrl generates unique strings") {
    List(
      UrlService.generateShortUrl,
      UrlService.generateShortUrl,
      UrlService.generateShortUrl
    ).parSequence
      .map(urlList => urlList.size == urlList.distinct.size)
      .assertEquals(true)
  }
