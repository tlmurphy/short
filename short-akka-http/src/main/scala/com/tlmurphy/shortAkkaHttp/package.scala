package com.tlmurphy

import com.tlmurphy.shortAkkaHttp.UrlRegistry.ShortUrl

package object shortAkkaHttp {
  // Response message models
  type Response = Either[ResponseFailure, ResponseSuccess]
  case class ResponseSuccess(message: String, url: Option[ShortUrl] = None)
  case class ResponseFailure(message: String, url: Option[ShortUrl] = None)
}
