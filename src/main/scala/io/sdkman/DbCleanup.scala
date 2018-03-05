package io.sdkman

trait DbCleanup extends VersionsRepo with EmailConnector {

  def start() = {

    send(Seq("http://localhost:8080/candidates/scala/2.9.0"), smtpToEmail)

    println("Done cleaning up.")
  }
}

object DbCleanup extends DbCleanup {
  start()
}
