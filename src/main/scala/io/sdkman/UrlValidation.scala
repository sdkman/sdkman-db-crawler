package io.sdkman

import scala.util.Try
import scalaj.http.Http
import scalaj.http.HttpOptions.followRedirects

trait UrlValidation {
  def hasOrphanedUrl(version: Version): Boolean =
    Try {
      Http(version.url)
        .method("HEAD")
        .option(followRedirects(true))
        .asString
        .code
    }.fold(e => true, code => code == 404)
}
