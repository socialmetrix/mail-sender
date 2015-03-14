package com.socialmetrix.mail;

import java.util.List;

import javax.mail.internet.InternetAddress;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class Mail {

	private final InternetAddress from;
	private final InternetAddress replyTo;
	private final List<InternetAddress> to;
	private final List<InternetAddress> bcc;
	private final String subject;
	private final String html;
	private final String text;

	public Mail(InternetAddress from, InternetAddress replyTo, List<InternetAddress> to, List<InternetAddress> bcc, String subject, String html, String text) {
		this.from = from;
		this.replyTo = replyTo;
		this.to = to;
		this.bcc = bcc;
		this.subject = subject;
		this.html = html;
		this.text = text;
	}

	public InternetAddress getFrom() {
		return this.from;
	}

	public InternetAddress getReplyto() {
		return this.replyTo;
	}

	public List<InternetAddress> getTo() {
		return this.to;
	}

	public List<InternetAddress> getBcc() {
		return this.bcc;
	}

	public String getSubject() {
		return this.subject;
	}

	public String getHtml() {
		return this.html;
	}

	public String getText() {
		return this.text;
	}

	@Override
	public String toString() {
		return MoreObjects
			.toStringHelper(this)
			.add("From", this.from)
			.add("ReplyTo", this.replyTo)
			.add("To", this.to)
			.add("Bcc", this.bcc)
			.add("Subject", this.subject)
			.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(//
			this.from,
			this.replyTo,
			this.to,
			this.bcc,
			this.subject,
			this.html,
			this.text);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}

		Mail other = (Mail) obj;
		return Objects.equal(this.from, other.from) &&
				Objects.equal(this.replyTo, other.replyTo) &&
				Objects.equal(this.to, other.to) &&
				Objects.equal(this.bcc, other.bcc) &&
				Objects.equal(this.subject, other.subject) &&
				Objects.equal(this.html, other.html) &&
				Objects.equal(this.text, other.text);
	}

}
