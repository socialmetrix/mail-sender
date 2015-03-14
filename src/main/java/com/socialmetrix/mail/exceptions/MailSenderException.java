package com.socialmetrix.mail.exceptions;

import com.socialmetrix.mail.Mail;

public class MailSenderException extends RuntimeException {

	private static final long serialVersionUID = -3502652458444169649L;

	private final Mail mail;

	public MailSenderException(Mail mail) {
		super();
		this.mail = mail;
	}

	public MailSenderException(Throwable throwable, Mail mail) {
		super(throwable);
		this.mail = mail;
	}

	public Mail getMail() {
		return this.mail;
	}

}
