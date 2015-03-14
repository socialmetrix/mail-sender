package com.socialmetrix.mail;

import com.socialmetrix.mail.exceptions.MailSenderException;

public interface MailSender {

	/**
	 * Send the email message.
	 * 
	 * @throws MailSenderException
	 *             If the email was not sent.
	 */
	void send(Mail mail);

}
