package service.health.check.updater;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmailUtil {

	// constants
    public static final String SMTP_HOST_SERVER = "mail";
	public static final String FROM_EMAIL = "test@testdomain.com";
	public static final String FROM_NAME = "test";

	public static void sendEmail(String toEmail, String subject, String body)
			throws UnsupportedEncodingException, MessagingException {
		Properties props = System.getProperties();

		props.put("mail.smtp.host", SMTP_HOST_SERVER);

		Session session = Session.getInstance(props, null);

		log.info("Updater - Sending email to '{}'; '{}'; '{}'", toEmail, subject, body);
		EmailUtil.sendEmail(session, toEmail, subject, body);
	}

	private static void sendEmail(Session session, String toEmail, String subject, String body)
			throws MessagingException, UnsupportedEncodingException {
		MimeMessage msg = new MimeMessage(session);
		//set message headers
		msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
		msg.addHeader("format", "flowed");
		msg.addHeader("Content-Transfer-Encoding", "8bit");

		msg.setFrom(new InternetAddress(FROM_EMAIL, FROM_NAME));

		msg.setReplyTo(InternetAddress.parse(FROM_EMAIL, false));

		msg.setSubject(subject, "UTF-8");

		msg.setText(body, "UTF-8");

		msg.setSentDate(new Date());

		msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
		Transport.send(msg);
	}
}

