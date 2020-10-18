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

    val orphans = result(findAllVersions().map(vs => vs.filter(validUrl)), 20 minutes)
    send(orphans, smtpToEmail)

    Thread.sleep(10000L)
    logger.info("Stopping sdkman-db-cleanup...")
    if(orphans.isEmpty) System.exit(0) else System.exit(1)
  }

  private def validUrl(v: Version): Boolean = !resourceAvailable(v.url)
}

object Main extends Main with App {
  run()
}
