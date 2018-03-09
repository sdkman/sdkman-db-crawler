package io.sdkman

import org.junit.runner.RunWith
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, Matchers, OptionValues, WordSpec}
import support.{MongoSupport, TestNetworking}

@RunWith(classOf[JUnitRunner])
class VersionsRepoSpec extends WordSpec
  with Matchers
  with BeforeAndAfter
  with ScalaFutures
  with OptionValues
  with MongoSupport
  with TestNetworking {

  before {
    dropAllCollections()
  }

  "versions repository" should {

    "find all versions ordered by candidate, version, platform" in new VersionRepoUnderTest {
      val java8u111 = Version("java", "8u111", "LINUX_64", "http://dl/8u111-b14/jdk-8u111-linux-x64.tar.gz")
      val java8u121 = Version("java", "8u121", "LINUX_64", "http://dl/8u121-b14/jdk-8u121-linux-x64.tar.gz")
      val java8u131 = Version("java", "8u131", "LINUX_64", "http://dl/8u131-b14/jdk-8u131-linux-x64.tar.gz")
      val scala212_lnx = Version("scala", "2.12.1", "LINUX_64", "http://dl/2_12_1/scala-2.12.1-linux-x64.zip")
      val scala212_osx = Version("scala", "2.12.1", "MAC_OSX", "http://dl/2_12_1/scala-2.12.1-osx-x64.zip")
      val scala212_win = Version("scala", "2.12.1", "WINDOWS_64", "http://dl/2_12_1/scala-2.12.1-windows-x64.zip")

      val candidateVersions = Seq(java8u131, scala212_win, java8u111, scala212_lnx, java8u121, scala212_osx)

      candidateVersions.foreach(insertVersion)

      whenReady(findAllVersions()) { versions =>
        versions.size shouldBe 6
        versions(0) shouldBe java8u111
        versions(1) shouldBe java8u121
        versions(2) shouldBe java8u131
        versions(3) shouldBe scala212_lnx
        versions(4) shouldBe scala212_osx
        versions(5) shouldBe scala212_win
      }
    }
  }

  sealed trait VersionRepoUnderTest extends VersionsRepo with MongoConnection with Configuration
}
