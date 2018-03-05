package support

import java.util.Properties
import javax.mail._

import scala.util.Random

trait EmailSupport {

  val Host = "localhost"

  val Port = 3143

  val props = new Properties()
  props.setProperty("mail.store.protocol", "imap")
  props.setProperty("mail.debug", "true")

  lazy val session = Session.getDefaultInstance(props, null)

  lazy val store = session.getStore("imap")

  def readMessages(email: String): List[Message] = {
    store.connect(Host, Port, email, "")

    val inbox = store.getFolder("Inbox")
    inbox.open(Folder.READ_ONLY)

    inbox.getMessages.toList
  }

  def randomEmail(): String = s"$alphanumericString@localhost.com"

  private def alphanumericString = Random.alphanumeric.take(32).mkString
}