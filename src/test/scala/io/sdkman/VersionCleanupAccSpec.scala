package io.sdkman

import javax.mail.Message

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock._
import org.jvnet.mock_javamail.Mailbox
import org.scalatest.concurrent.{Eventually, IntegrationPatience}
import org.scalatest.{BeforeAndAfter, Matchers, OptionValues, WordSpec}
import support.Mongo
import support.Mongo.insertVersions

class VersionCleanupAccSpec extends WordSpec
  with Matchers
  with BeforeAndAfter
  with Eventually
  with IntegrationPatience
  with OptionValues {

  WireMock.configureFor("localhost", 8080)

  before {
    Mongo.dropAllCollections()
    WireMock.reset()
  }

  "application" should {
    "notify of all versions with defunct urls by email" in {

      val fromEmail = "from@localhost.com"
      val toEmail = "to@localhost.com"
      val subject = "Invalid URLs"

      val validUrl = "http://localhost:8080/candidates/scala/2.12.4"
      val invalidUrl = "http://localhost:8080/candidates/scala/2.9.0"

      val validVersion = Version("scala", "2.12.4", "UNIVERSAL", validUrl)
      val defunctVersion = Version("scala", "2.9.0", "UNIVERSAL", invalidUrl)

      insertVersions(validVersion, defunctVersion)

      stubFor(head(urlEqualTo("/candidates/scala/2.12.4"))
        .willReturn(aResponse()
          .withStatus(200)))
      stubFor(head(urlEqualTo("/candidates/scala/2.9.0"))
        .willReturn(aResponse()
          .withStatus(404)))

      DbCleanup.main(Array[String]())

      eventually {
        messageCountFor(toEmail) shouldBe 1
        val message = receivedMessage(toEmail)
        message.getFrom.toList.headOption.value.toString shouldBe fromEmail
        message.getSubject shouldBe subject
        message.getContent.asInstanceOf[String] should include(invalidUrl)
        message.getContent.asInstanceOf[String] shouldNot include(validUrl)
      }
    }
  }

  def messageCountFor(ownerEmail: String): Int = Mailbox.get(ownerEmail).size()

  def receivedMessage(ownerEmail: String): Message = Mailbox.get(ownerEmail).get(0)

}
