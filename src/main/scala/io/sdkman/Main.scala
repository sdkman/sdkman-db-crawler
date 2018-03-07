package io.sdkman

import scala.concurrent.Await
import scala.concurrent.duration._
import scalaj.http.Http
import languageFeature.postfixOps

class Main extends VersionsRepo with EmailConnector {

  def start() = {

    send(
      Await.result(findAllVersions(), 10 seconds)
        .filter(hasOrphanedUrl).map(_.url),
      smtpToEmail)

    println("Finished working...")
  }

  private def hasOrphanedUrl(version: Version): Boolean = Http(version.url).method("HEAD").asString.code != 200
}

object Main extends Main with App {
  start()
}
