package io.sdkman

object DbCleanup extends App with VersionsRepo with EmailSupport {

  send(Seq("http://localhost:8080/candidates/scala/2.9.0"), "to@localhost.com")

  println("Done cleaning up.")
}
