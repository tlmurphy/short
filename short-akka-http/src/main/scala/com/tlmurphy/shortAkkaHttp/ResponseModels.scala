package com.tlmurphy.shortAkkaHttp

import com.tlmurphy.shortAkkaHttp.UrlRegistry.ShortUrl

object ResponseModels {
  case class ResponseSuccess(message: String, url: Option[ShortUrl] = None)
  case class ResponseFailure(message: String, url: Option[ShortUrl] = None)
  type Response = Either[ResponseFailure, ResponseSuccess]
}
