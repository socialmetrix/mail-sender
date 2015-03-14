package com.socialmetrix.mail;

import com.google.common.util.concurrent.RateLimiter;

public class RateLimitedMailSender implements MailSender {

	private RateLimiter rateLimiter;
	private MailSender mailSender;

	public RateLimitedMailSender(double mailPerSecond, MailSender mailSender) {
		this.rateLimiter = RateLimiter.create(mailPerSecond);
		this.mailSender = mailSender;
	}

	@Override
	public void send(Mail mail) {
		this.rateLimiter.acquire();
		this.mailSender.send(mail);
	}

}
