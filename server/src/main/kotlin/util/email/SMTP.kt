package util.email

import jakarta.mail.Authenticator
import jakarta.mail.Message
import jakarta.mail.Multipart
import jakarta.mail.PasswordAuthentication
import jakarta.mail.Session
import jakarta.mail.Transport
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeBodyPart
import jakarta.mail.internet.MimeMessage
import jakarta.mail.internet.MimeMultipart

import java.util.Properties

class SMTP {
    companion object {
        fun sendEmail(recipient: String, emailTemplate: EmailTemplate) {
            val smtpConfig = Properties()
            smtpConfig["mail.smtp.auth"] = true
            smtpConfig["mail.smtp.starttls.enable"] = true
            smtpConfig["mail.smtp.host"] = "sandbox.smtp.mailtrap.io"
            smtpConfig["mail.smtp.port"] = 2525
            smtpConfig["mail.smtp.ssl.trust"] = "sandbox.smtp.mailtrap.io"

            val session = Session.getInstance(smtpConfig, object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(
                        requireNotNull(System.getenv("MAILTRAP_UNAME")),
                        requireNotNull(System.getenv("MAILTRAP_PW"))
                    )
                }
            })

            val message: Message = MimeMessage(session)
            message.setFrom(InternetAddress("test@gmail.com"))
            message.setRecipients(
                Message.RecipientType.TO, InternetAddress.parse(recipient)
            )
            message.subject = emailTemplate.subject

            val msg = emailTemplate.compose()

            val mimeBodyPart = MimeBodyPart()
            mimeBodyPart.setContent(msg, "text/html; charset=utf-8")

            val multipart: Multipart = MimeMultipart()
            multipart.addBodyPart(mimeBodyPart)

            message.setContent(multipart)

            Transport.send(message)
        }
    }
}