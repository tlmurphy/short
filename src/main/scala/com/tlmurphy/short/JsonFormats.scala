package com.tlmurphy.short

import com.tlmurphy.short.UrlRegistry.ActionPerformed
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

object JsonFormats {
  import DefaultJsonProtocol._

  implicit val urlJsonFormat: RootJsonFormat[Url] = jsonFormat2(Url)
  implicit val urlsJsonFormat: RootJsonFormat[Urls] = jsonFormat1(Urls)

  implicit val actionPerformedJsonFormat: RootJsonFormat[ActionPerformed] =
    jsonFormat1(ActionPerformed)
}
