package com.tlmurphy.shortZio

import zio.*

trait ShortUrlGenerator:
  def generateShortUrl: UIO[String]

object ShortUrlGenerator:
  private def nextAlphaNumeric: UIO[Char] = {
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    Random.nextIntBounded(chars.length()).map(chars.charAt(_))
  }

  private def combineIntoString(charEffectList: List[UIO[Char]]): UIO[String] =
    ZIO.mergeAll(charEffectList)("")((x, y) => x + y)

  def generateShortUrl: UIO[String] =
    combineIntoString(List.fill(7)(nextAlphaNumeric))
