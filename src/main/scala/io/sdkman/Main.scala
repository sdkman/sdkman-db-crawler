package io.sdkman

import java.lang.Thread.sleep

import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

class Main extends VersionsRepo
  with UrlValidation
  with EmailConnector
  with MongoConnection
  with Configuration
  with LazyLogging {

  def run(): Unit = {

    send(Await.result(findAllVersions(), 10 seconds).filter(hasOrphanedUrl).map(_.url), smtpToEmail)

    logger.info("Completed scheduled email job...")
  }
}

object Main extends Main with App {

  logger.info("Starting sdkman-db-cleanup...")

  import monix.execution.Scheduler.{global => scheduler}

  logger.info("Starting up scheduler...")
  scheduler.scheduleAtFixedRate(1 minute, 24 hours) {
    logger.info("Running scheduled email job...")
    run()
  }

  logger.info("Successfully started sdkman-db-cleanup...")
  while (true) sleep(1000)
}
