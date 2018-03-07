package io.sdkman


class DbCleanup extends VersionsRepo with EmailConnector {

  def start() = {

    send(Seq("http://wiremock:8080/candidates/scala/2.9.0"), smtpToEmail)

    println("Finished working...")
  }
}

object DbCleanup extends DbCleanup with App {
  start()
}
