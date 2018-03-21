package io.sdkman

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, head, stubFor, urlEqualTo}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, Matchers, WordSpec}
import support.TestNetworking

@RunWith(classOf[JUnitRunner])
class UrlValidationSpec extends WordSpec with Matchers with BeforeAndAfter with TestNetworking {

  WireMock.configureFor(WiremockHost, WiremockPort)

  before {
    WireMock.reset()
  }


  "url validation" should {

    "determine that a resource is not orphaned" in new UrlValidation {
      val validUri = "/candidates/scala/2.12.4"

      stubFor(head(urlEqualTo(validUri))
        .willReturn(aResponse().withStatus(200)))

      withClue("valid url orphaned") {
        hasOrphanedUrl(version(urlWith(validUri))) shouldBe false
      }
    }

    "determine that a resource redirecting to a valid uri is not orphaned" in new UrlValidation {
      val redirectUri = "/redirect/scala/2.12.5"
      val finalUri = "/finalurl/scala/2.12.5"

      stubFor(head(urlEqualTo(redirectUri))
        .willReturn(aResponse().withStatus(302).withHeader("Location", urlWith(finalUri))))

      stubFor(head(urlEqualTo(finalUri))
        .willReturn(aResponse().withStatus(200)))

      withClue("redirect to a valid uri orphaned") {
        hasOrphanedUrl(version(urlWith(redirectUri))) shouldBe false
      }
    }

    "determine that a resource redirects to a uri not found" in new UrlValidation {
      val redirectUri = "/redirect/scala/2.12.5"
      val finalUri = "/invalid/url/scala/2.12.5"

      stubFor(head(urlEqualTo(redirectUri))
        .willReturn(aResponse().withStatus(302).withHeader("Location", urlWith(finalUri))))

      stubFor(head(urlEqualTo(finalUri))
        .willReturn(aResponse().withStatus(404)))

      withClue("redirect to invalid uri not orphaned") {
        hasOrphanedUrl(version(urlWith(redirectUri))) shouldBe true
      }
    }

    "determine that a resource redirects to an unknown host" in new UrlValidation {
      val redirectUri = "/redirect/scala/2.12.5"
      val unknownHostUrl = "http://unknown5f7c5b58a4e4e777654ad16bf641144c:9090"

      stubFor(head(urlEqualTo(redirectUri))
        .willReturn(aResponse().withStatus(302).withHeader("Location", unknownHostUrl)))

      withClue("redirect to unknown host not orphaned") {
        hasOrphanedUrl(version(urlWith(redirectUri))) shouldBe true
      }
    }

    "determine that a resource with invalid uri is orphaned" in new UrlValidation {
      val invalidUri = "/candidates/scala/9.9.9"

      withClue("invalid uri not orphaned") {
        hasOrphanedUrl(version(urlWith(invalidUri))) shouldBe true
      }
    }

    "deterimine that a resource with unknown host is orphaned" in new UrlValidation {
      val unknownHostUrl = "http://unknown5f7c5b58a4e4e777654ad16bf641144c:9090"

      withClue("unknown host not orphaned") {
        hasOrphanedUrl(version(unknownHostUrl)) shouldBe true
      }
    }
  }

  def urlWith(uri: String) = s"http://$WiremockHost:$WiremockPort$uri"

  def version(url: String) = Version("candidate", "version", "platform", url)

}
