package io.sdkman

import javax.mail.{Message, Session}
import javax.mail.internet.{InternetAddress, MimeMessage}

import com.icegreen.greenmail.util.{GreenMail, ServerSetupTest}

object DbCleanup extends App {

  lazy val greenMail = new GreenMail(ServerSetupTest.SMTP_IMAP)

  lazy  val greenMailUser = greenMail.setUser("user@localhost.com", "user", "password")

  def deliverMessage(from: String, subject: String, text: String) = {
    val message = new MimeMessage(null.asInstanceOf[Session])
    message.setFrom(new InternetAddress(from))
    message.setRecipients(Message.RecipientType.TO, greenMailUser.getEmail)
    message.setSubject(subject)
    message.setText(text)
    greenMailUser.deliver(message)
  }

  deliverMessage("dbcleanup@example.org", "Invalid URLs", "http://localhost:8080/candidates/scala/2.9.0")

  println("Done cleaning up.")
}
