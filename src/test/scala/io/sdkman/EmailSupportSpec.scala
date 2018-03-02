package io.sdkman

import javax.mail.Message

import org.jvnet.mock_javamail.Mailbox
import org.scalatest.concurrent.Eventually
import org.scalatest.{Matchers, OptionValues, WordSpec}

class EmailSupportSpec extends WordSpec with Matchers with Eventually with OptionValues {
  "email support" should {
    "send an email" in new EmailSupport {
      val fromEmail = "from@localhost.com"
      val toEmail = "to@localhost.com"
      val subject = "Invalid URLs"
      val content = "content text"

      send(Seq(content), toEmail)

      eventually {
        messageCountFor(toEmail) shouldBe 1
        val message = receivedMessage(toEmail)
        message.getFrom.toList.headOption.value.toString shouldBe fromEmail
        message.getSubject shouldBe subject
        val emailBody = message.getContent.asInstanceOf[String]
        emailBody should include("The following URLs are invalid and marked for deletion")
        emailBody should include(content)
      }
    }
  }

  def messageCountFor(ownerEmail: String): Int = Mailbox.get(ownerEmail).size()

  def receivedMessage(ownerEmail: String): Message = Mailbox.get(ownerEmail).get(0)
}
