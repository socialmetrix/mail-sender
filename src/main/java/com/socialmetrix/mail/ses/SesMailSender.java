package com.socialmetrix.mail.ses;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.MimeMessage;

import org.apache.commons.mail.*;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.*;
import com.socialmetrix.mail.*;
import com.socialmetrix.mail.exceptions.MailSenderException;

/**
 * Sends mails using amazon API. Listeners can be added to be notified when a
 * mail was send successfully.
 */
public class SesMailSender implements MailSender {

	private AmazonSimpleEmailServiceClient client;

	public SesMailSender(String accessKey, String secretKey) {
		this.client = new AmazonSimpleEmailServiceClient(new BasicAWSCredentials(accessKey, secretKey));
	}

	@Override
	public void send(Mail mail) {
		try {
			SendRawEmailRequest request = buildEmailRequest(buildMimeMessage(mail));
			this.client.sendRawEmail(request);
		} catch (AmazonServiceException e) {
			throw new MailSenderException(e, mail);
		} catch (IOException e) {
			throw new MailSenderException(e, mail);
		} catch (MessagingException e) {
			throw new MailSenderException(e, mail);
		}
	}

	private static SendRawEmailRequest buildEmailRequest(MimeMessage mimeMessage) throws IOException, MessagingException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		mimeMessage.writeTo(out);

		RawMessage rawMessage = new RawMessage();
		rawMessage.withData(ByteBuffer.wrap(out.toByteArray()));

		return new SendRawEmailRequest().withRawMessage(rawMessage);
	}

	private static MimeMessage buildMimeMessage(Mail mail) {
		HtmlEmail apacheEmail = new HtmlEmail();
		apacheEmail.setMailSession(Session.getInstance(new Properties()));
		apacheEmail.setCharset("UTF-8");

		try {
			apacheEmail.setFrom(mail.getFrom().getAddress(), mail.getFrom().getPersonal());
			apacheEmail.setSubject(mail.getSubject());

			if (mail.getHtml() != null) {
				apacheEmail.setHtmlMsg(mail.getHtml());
			}
			if (mail.getText() != null) {
				apacheEmail.setTextMsg(mail.getText());
			}
			if (!mail.getTo().isEmpty()) {
				apacheEmail.setTo(mail.getTo());
			}
			if (mail.getReplyto() != null) {
				apacheEmail.addReplyTo(mail.getReplyto().getAddress(), mail.getReplyto().getPersonal());
			}
			if (!mail.getBcc().isEmpty()) {
				apacheEmail.setBcc(mail.getBcc());
			}
			apacheEmail.buildMimeMessage();
		} catch (EmailException e) {
			throw new MailSenderException(e, mail);
		}

		return apacheEmail.getMimeMessage();
	}

}
