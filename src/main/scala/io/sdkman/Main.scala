package io.sdkman

import java.lang.Thread.sleep

import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps
import scalaj.http.Http

class Main extends VersionsRepo with EmailConnector with MongoConnection with Configuration with LazyLogging {

  def run(): Unit = {

    send(
      Await.result(findAllVersions(), 10 seconds)
        .filter(hasOrphanedUrl).map(_.url),
      smtpToEmail)

    println("Finished working...")
  }

  private def hasOrphanedUrl(version: Version): Boolean = Http(version.url).method("HEAD").asString.code != 200
}

object Main extends Main with App {

  import monix.execution.Scheduler.{global => scheduler}

  scheduler.scheduleAtFixedRate(1 minute, 24 hours) {
    run()
  }

  while (true) sleep(1000)
}
