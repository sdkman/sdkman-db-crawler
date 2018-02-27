package support

import javax.mail.internet.{InternetAddress, MimeMessage}
import javax.mail.{Message, Session}

import com.icegreen.greenmail.user.GreenMailUser
import com.icegreen.greenmail.util.GreenMail

trait MailSupport {

  val email: String
  val user: String
  val password: String

  def greenMail: GreenMail

  def greenMailUser: GreenMailUser

  def deliverMessage(from: String, subject: String, text: String) = {
    val message = new MimeMessage(null.asInstanceOf[Session])
    message.setFrom(new InternetAddress(from))
    message.setRecipients(Message.RecipientType.TO, email)
    message.setSubject(subject)
    message.setText(text)
    greenMailUser.deliver(message)
  }

  def receiveMessage(): Option[MimeMessage] = greenMail.getReceivedMessages.toList.headOption

}
