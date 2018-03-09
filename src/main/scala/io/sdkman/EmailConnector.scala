package io.sdkman

import javax.mail.internet.InternetAddress

import com.typesafe.scalalogging.LazyLogging
import courier.{Envelope, Mailer, Text}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

trait EmailConnector {

  self: LazyLogging with Configuration =>

  lazy val mailer = Mailer(smtpHost, smtpPort)
    .auth(true)
    .as(smtpUser, smtpPassword)
    .startTtls(true)()

  def send(urls: Seq[String], email: String): Unit = {
    mailer(Envelope.from(new InternetAddress(smtpFromEmail))
      .to(new InternetAddress(email))
      .subject(s"Invalid URLs")
      .content(Text(compose(urls)))).onComplete {
      case Success(x) =>
        logger.info(s"Notification sent: $smtpToEmail: $urls")
      case Failure(e) =>
        logger.error(s"Failed to send notification: $smtpToEmail: ${e.getMessage}")
    }
  }

  private def compose(urls: Seq[String]) =
    s"""
      |The following URLs are invalid and marked for deletion:
      |
      |$urls
    """.stripMargin
}

