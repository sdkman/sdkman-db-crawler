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
    logger.info("Starting sdkman-db-crawler...")

    val orphans = result(findAllVersions().map(vs => vs.filter(invalidUrl)), 20 minutes)
    send(orphans, smtpToEmail)

    Thread.sleep(10000L)
    logger.info("Stopping sdkman-db-crawler...")
    if(orphans.isEmpty) System.exit(0) else {
      logger.info("The following urls are defunct:")
      orphans.foreach(v => logger.info(s"${v.candidate} ${v.version} (${v.platform}): ${v.url}"))
      System.exit(1)
    }
  }

  private def invalidUrl(v: Version): Boolean = !resourceAvailable(v.url)
}

object Main extends Main with App {
  run()
}
