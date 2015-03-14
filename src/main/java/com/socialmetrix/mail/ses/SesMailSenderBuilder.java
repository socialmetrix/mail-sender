package com.socialmetrix.mail.ses;

import com.socialmetrix.mail.*;

public class SesMailSenderBuilder {

	private int mailsPerSecond;
	private String accessKey;
	private String secretKey;

	public static SesMailSenderBuilder ses() {
		return new SesMailSenderBuilder();
	}

	public SesMailSenderBuilder() {
		this.mailsPerSecond = 3;
	}

	public SesMailSenderBuilder mailsPerSecond(int mailsPerSecond) {
		this.mailsPerSecond = mailsPerSecond;
		return this;
	}

	public SesMailSenderBuilder credentials(String accessKey, String secretKey) {
		this.accessKey = accessKey;
		this.secretKey = secretKey;
		return this;
	}

	public MailSender build() {
		return rateLimited(sesMailSender(this.accessKey, this.secretKey));
	}

	private SesMailSender sesMailSender(String accessKey, String secretKey) {
		return new SesMailSender(accessKey, secretKey);
	}

	private RateLimitedMailSender rateLimited(MailSender mailSender) {
		return new RateLimitedMailSender(mailsPerSecond, mailSender);
	}

}
