package io.sdkman

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock._
import org.junit.runner.RunWith
import org.scalatest.concurrent.{Eventually, IntegrationPatience}
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, Matchers, OptionValues, WordSpec}
import support.{EmailSupport, MongoSupport, TestNetworking}

@RunWith(classOf[JUnitRunner])
class VersionCleanupAccSpec extends WordSpec
  with Matchers
  with BeforeAndAfter
  with Eventually
  with IntegrationPatience
  with OptionValues
  with EmailSupport
  with MongoSupport
  with TestNetworking {

  WireMock.configureFor(WiremockHost, WiremockPort)

  val toEmail = "to@localhost.com"

  before {
    dropAllCollections()
    WireMock.reset()
  }

  "application" should {
    "notify of all versions with defunct urls by email" in new DbCleanup {

      val fromEmail = "from@localhost.com"
      val toEmail = randomEmail()

      override lazy val smtpToEmail = toEmail

      val subject = "Invalid URLs"

      val validUrl = s"http://$WiremockHost:$WiremockPort/candidates/scala/2.12.4"
      val invalidUrl = s"http://$WiremockHost:$WiremockPort/candidates/scala/2.9.0"

      val validVersion = Version("scala", "2.12.4", "UNIVERSAL", validUrl)
      val defunctVersion = Version("scala", "2.9.0", "UNIVERSAL", invalidUrl)

      insertVersions(validVersion, defunctVersion)

      stubFor(head(urlEqualTo("/candidates/scala/2.12.4"))
        .willReturn(aResponse()
          .withStatus(200)))
      stubFor(head(urlEqualTo("/candidates/scala/2.9.0"))
        .willReturn(aResponse()
          .withStatus(404)))

      start()

      eventually {
        val messages = readMessages(toEmail)
        messages.size shouldBe 1
        val message = messages.head
        message.getFrom.toList.headOption.value.toString shouldBe fromEmail
        message.getSubject shouldBe subject
        val content = message.getContent.asInstanceOf[String]
        content should include(invalidUrl)
        content shouldNot include(validUrl)
      }
    }
  }
}
