package com.tlmurphy.shortAkkaHttp

import com.tlmurphy.shortAkkaHttp.ResponseModels._
import com.tlmurphy.shortAkkaHttp.UrlRegistry._
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

object JsonFormats {
  import DefaultJsonProtocol._

  implicit val urlJsonFormat: RootJsonFormat[Url] = jsonFormat1(Url)
  implicit val shortUrlJsonFormat: RootJsonFormat[ShortUrl] = jsonFormat2(
    ShortUrl
  )
  implicit val shortUrlsJsonFormat: RootJsonFormat[ShortUrls] = jsonFormat1(
    ShortUrls
  )

  implicit val responseJsonFormat: RootJsonFormat[ResponseSuccess] =
    jsonFormat2(
      ResponseSuccess
    )
  implicit val responseFailureJsonFormat: RootJsonFormat[ResponseFailure] =
    jsonFormat2(
      ResponseFailure
    )
}
