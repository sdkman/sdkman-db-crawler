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

  def send(versions: Seq[Version], email: String): Unit = {
    versions.headOption.foreach { _ =>
      mailer(Envelope.from(new InternetAddress(smtpFromEmail))
        .to(email.split(",").map(new InternetAddress(_)): _*)
        .subject(s"Invalid URLs")
        .content(Text(compose(versions)))).onComplete {
        case Success(x) =>
          logger.info(s"Notification sent to: $smtpToEmail")
        case Failure(e) =>
          logger.error(s"Failed to send notification: $smtpToEmail: $e")
      }
    }
  }

  private def compose(urls: Seq[Version]) =
    "The following URLs are invalid and should be removed:\n" + urls.map(v => s"* ${v.candidate}:${v.version} - ${v.url} (${v.platform})").mkString("\n")
}

