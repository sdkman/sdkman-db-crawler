package io.sdkman

import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.Await.result
import scala.concurrent.duration._
import scala.language.postfixOps

class Main extends VersionsRepo
  with UrlValidation
  with EmailConnector
  with MongoConnection
  with Configuration
  with LazyLogging {

  def run(): Unit = {
    logger.info("Starting sdkman-db-cleanup...")

    send(result(findAllVersions(), 10 seconds).filter { v =>
      if (v.version.endsWith("oracle"))
        hasOrphanedUrl(v.url, Some(Cookie("oraclelicense", "accept-securebackup-cookie")))
      else hasOrphanedUrl(v.url)
    }, smtpToEmail)

    logger.info("Stopping sdkman-db-cleanup...")
    System.exit(0)
  }
}

object Main extends Main with App {
  run()
}