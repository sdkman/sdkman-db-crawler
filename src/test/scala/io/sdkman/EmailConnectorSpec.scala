package io.sdkman

import org.junit.runner.RunWith
import org.scalatest.concurrent.Eventually
import org.scalatest.junit.JUnitRunner
import org.scalatest.{Matchers, OptionValues, WordSpec}
import support.EmailSupport

@RunWith(classOf[JUnitRunner])
class EmailConnectorSpec extends WordSpec with Matchers with Eventually with OptionValues with EmailSupport {
  "email connector" should {
    "send an email" in new EmailConnector {
      val fromEmail = "from@localhost.com"
      override lazy val smtpToEmail = randomEmail()
      val toEmail = smtpToEmail
      val subject = "Invalid URLs"
      val content = "content text"

      send(Seq(content), toEmail)

      eventually {
        val messages = readMessages(toEmail)
        messages.size shouldBe 1
        val message = messages.head
        message.getFrom.toList.headOption.value.toString shouldBe fromEmail
        message.getSubject shouldBe subject
        val content = message.getContent.asInstanceOf[String]
        content should include("The following URLs are invalid and marked for deletion")
        content should include(content)
      }
    }
  }
}
