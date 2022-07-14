package com.tlmurphy.short

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

  implicit val responseFailureJsonFormat: RootJsonFormat[ResponseFailure] =
    jsonFormat1(ResponseFailure)
}
