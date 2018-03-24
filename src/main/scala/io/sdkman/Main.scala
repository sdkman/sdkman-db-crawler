package io.sdkman

import com.typesafe.scalalogging.LazyLogging
import ratpack.handling.{Context, Handler}
import ratpack.server.RatpackServer

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

    send(result(findAllVersions(), 10 seconds).filter(hasOrphanedUrl), smtpToEmail)

    logger.info("Completed scheduled email job...")
  }
}

object Main extends Main with App {

  logger.info("Starting sdkman-db-cleanup...")

  import monix.execution.Scheduler.{global => scheduler}

  logger.info("Starting up scheduler...")
  scheduler.scheduleAtFixedRate(10 seconds, 24 hours) {
    logger.info("Running scheduled email job...")
    run()
  }

  logger.info("Starting up http server...")
  RatpackServer.start(server =>
    server.handlers(chain =>
      chain.get("alive", new HealthCheckHandler)))
}

class HealthCheckHandler extends Handler {
  override def handle(ctx: Context): Unit = ctx.render("OK")
}
