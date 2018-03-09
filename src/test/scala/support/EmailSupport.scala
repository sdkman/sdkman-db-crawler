package support

import java.util.Properties
import javax.mail._

import scala.util.Random

trait EmailSupport {

  val GreenmailHost: String

  val GreenmailPort: Int

  val props = new Properties()
  props.setProperty("mail.store.protocol", "imap")
  props.setProperty("mail.debug", "true")

  lazy val session = Session.getDefaultInstance(props, null)

  private def openStore(email: String) = {
    val store = session.getStore("imap")
    store.connect(GreenmailHost, GreenmailPort, email, "")
    store
  }

  private def closeStore(store: Store) = store.close()

  def withStore(email: String)(fun: Store => Unit): Unit = {
    val store = openStore(email)
    fun(store)
    closeStore(store)
  }

  def readMessages(store: Store): List[Message] = {
    val inbox = store.getFolder("Inbox")
    inbox.open(Folder.READ_ONLY)
    inbox.getMessages.toList
  }

  def randomEmail(): String = s"$alphanumericString@localhost.com"

  private def alphanumericString = Random.alphanumeric.take(32).mkString
}