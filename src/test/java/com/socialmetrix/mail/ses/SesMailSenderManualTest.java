package com.socialmetrix.mail.ses;

import static com.socialmetrix.mail.MailBuilder.mail;
import static com.socialmetrix.mail.ses.SesMailSenderBuilder.ses;

import com.socialmetrix.mail.*;
import com.socialmetrix.mail.exceptions.MailSenderException;

public class SesMailSenderManualTest {

	private static final String SecretKey = "???";
	private static final String AccessKey = "???";

	public static void main(String[] args) {
		new SesMailSenderManualTest().send();
	}

	private void send() {
		MailSender mailSender = ses()//
			.credentials(AccessKey, SecretKey)
			.build();

		Mail mail = mail() //
			.from("Some Name", "some@name.com")
			.to("Another Person", "another@person.com")
			.replyTo("Someone <some@mail.com>")
			.bcc("Secret Destination", "secret@destination.com")
			.subject("My Subject")
			.html("<ul>" + //
					"<li>Element 1</li>"
					+ "<li>Element 2</li>"
					+ "<li>Element 3</li>"
					+ "</ul>"
					+ "<p>Paragraph <a href=\"google.com\">link</a></p>")
			.text("You can't see html.")
			.build();

		try {
			mailSender.send(mail);
			System.out.println("Mail sent: " + mail);
		} catch (MailSenderException e) {
			System.err.println("Mail not sent: " + mail);
			e.printStackTrace();
		}
	}

}
