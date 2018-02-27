package io.sdkman

import java.net.InetAddress
import javax.mail.Address

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock._
import org.scalatest.concurrent.{Eventually, IntegrationPatience}
import org.scalatest.{BeforeAndAfter, Matchers, OptionValues, WordSpec}
import support.Mongo.insertVersions
import support.{MailSupport, Mongo}

class VersionCleanupAccSpec extends WordSpec with Matchers with BeforeAndAfter with Eventually with IntegrationPatience with MailSupport with OptionValues {

  override val email = "user@localhost.com"

  override val user = "user"

  override val password = "password"

  override lazy val greenMail = DbCleanup.greenMail

  override lazy val greenMailUser = DbCleanup.greenMailUser

  WireMock.configureFor("localhost", 8080)

  before {
    Mongo.dropAllCollections()
    WireMock.reset()
  }

  "application" should {
    "notify of all versions with defunct urls by email" in {

      val fromEmail = "dbcleanup@example.org"
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
        val message = receiveMessage().value
        message.getFrom.toList.headOption.value.toString shouldBe fromEmail
        message.getSubject shouldBe subject
        message.getContent.asInstanceOf[String] should include(invalidUrl)
        message.getContent.asInstanceOf[String] shouldNot include(validUrl)
      }
    }
  }

  def extractEmail(addresses: Array[Address]): String = new String(addresses.toList.head.asInstanceOf[InetAddress].getAddress)

}
