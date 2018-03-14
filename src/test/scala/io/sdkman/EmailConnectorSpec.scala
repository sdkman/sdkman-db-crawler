package io.sdkman

import com.typesafe.scalalogging.LazyLogging
import org.junit.runner.RunWith
import org.scalatest.concurrent.{Eventually, IntegrationPatience}
import org.scalatest.junit.JUnitRunner
import org.scalatest.{Matchers, OptionValues, WordSpec}
import support.{EmailSupport, TestNetworking}

@RunWith(classOf[JUnitRunner])
class EmailConnectorSpec extends WordSpec
  with Matchers
  with Eventually
  with IntegrationPatience
  with OptionValues
  with EmailSupport
  with TestNetworking {

  "email connector" should {

    "send email if invalid url(s) are found" in new TestEmailConnector {
      val fromEmail = "from@localhost.com"
      val toEmail = randomEmail()
      val subject = "Invalid URLs"
      val url = "url text"

      send(Seq(url), toEmail)

      withStore(toEmail) { store =>
        eventually {
          val messages = readMessages(store)
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

    "not send email if no versions are found" in new TestEmailConnector {
      val fromEmail = "from@localhost.com"
      val toEmail = randomEmail()
      val subject = "Invalid URLs"
      val url = "url text"

      //doing two calls will not bump up the message count to 2
      send(Seq(), toEmail)
      send(Seq(url), toEmail)

      withStore(toEmail) { store =>
        eventually {
          val messages = readMessages(store)
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

  sealed trait TestEmailConnector extends EmailConnector with LazyLogging with Configuration

}
