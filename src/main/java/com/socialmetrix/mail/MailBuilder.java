package com.socialmetrix.mail;

import java.io.UnsupportedEncodingException;
import java.util.*;

import javax.mail.internet.*;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.socialmetrix.mail.exceptions.*;

public class MailBuilder {

	private final HttpTemplateRenderer httpTemplateRenderer;

	private InternetAddress from;
	private InternetAddress replyTo;
	private List<InternetAddress> to = new ArrayList<InternetAddress>();
	private List<InternetAddress> bcc = new ArrayList<InternetAddress>();
	private String subject;
	private String html;
	private String text;

	public static MailBuilder mail() {
		return new MailBuilder(new HttpTemplateRenderer());
	}

	public MailBuilder(HttpTemplateRenderer httpTemplateRenderer) {
		this.httpTemplateRenderer = httpTemplateRenderer;
	}

	// ======== FROM ========

	public MailBuilder from(String address) {
		this.from = buildAddress(address);
		return this;
	}

	public MailBuilder from(String name, String address) {
		this.from = buildAddress(name, address);
		return this;
	}

	// ======== FROM ========

	public MailBuilder replyTo(String address) {
		this.replyTo = buildAddress(address);
		return this;
	}

	public MailBuilder replyTo(String name, String address) {
		this.replyTo = buildAddress(name, address);
		return this;
	}

	// ======== TO ========

	/**
	 * Replace mail "to" addresses with just the {@code address} parameter.
	 */
	public MailBuilder onlyTo(String address) {
		this.to = Arrays.asList(buildAddress(address));
		return this;
	}

	/**
	 * Replace mail "to" addresses with the {@code name} and {@code address}
	 * parameters.
	 */
	public MailBuilder onlyTo(String name, String address) {
		this.to = Arrays.asList(buildAddress(name, address));
		return this;
	}

	public MailBuilder onlyTo(List<String> addresses) {
		this.to = new ArrayList<InternetAddress>(Collections2.transform(addresses, new Function<String, InternetAddress>() {
			@Override
			public InternetAddress apply(String address) {
				return buildAddress(address);
			}
		}));
		return this;
	}

	/**
	 * Adds and address to the current destination list.
	 */
	public MailBuilder to(String address) {
		this.to.add(buildAddress(address));
		return this;
	}

	/**
	 * Adds and address with name to the current destination list.
	 */
	public MailBuilder to(String name, String address) {
		this.to.add(buildAddress(name, address));
		return this;
	}

	// ======== BCC ========

	public MailBuilder onlyBcc(String address) {
		this.bcc = Arrays.asList(buildAddress(address));
		return this;
	}

	public MailBuilder onlyBcc(String name, String address) {
		this.bcc = Arrays.asList(buildAddress(name, address));
		return this;
	}

	public MailBuilder bcc(String address) {
		this.bcc.add(buildAddress(address));
		return this;
	}

	public MailBuilder bcc(String name, String address) {
		this.bcc.add(buildAddress(name, address));
		return this;
	}

	// ======== SUBJECT ========

	public MailBuilder subject(String subject) {
		this.subject = subject;
		return this;
	}

	// ======== HTML ========

	public MailBuilder html(String html) {
		this.html = html;
		return this;
	}

	public MailBuilder htmlFromTemplate(String url, Object data) {
		this.html = this.httpTemplateRenderer.renderUrlTemplate(url, data);
		return this;
	}

	// ======== TEXT ========

	public MailBuilder text(String text) {
		this.text = text;
		return this;
	}

	// ======== BUILD ========

	public Mail build() {
		return new Mail(this.from, this.replyTo, this.to, this.bcc, this.subject, this.html, this.text);
	}

	// ======== SEND ========
	/**
	 * Sends the built email with the provided mail sender.
	 * 
	 * @throws MailSenderException
	 *             If the email was not sent.
	 */
	public void send(MailSender mailSender) {
		mailSender.send(this.build());
	}

	private static InternetAddress buildAddress(String address) {
		try {
			InternetAddress internetAddress = new InternetAddress(address);
			internetAddress.validate();
			return internetAddress;
		} catch (AddressException e) {
			throw new MailBuilderException(e);
		}
	}

	private static InternetAddress buildAddress(String personal, String address) {
		try {
			InternetAddress internetAddress = new InternetAddress(address, personal, "UTF-8");
			internetAddress.validate();
			return internetAddress;
		} catch (UnsupportedEncodingException e) {
			throw new MailBuilderException(e);
		} catch (AddressException e) {
			throw new MailBuilderException(e);
		}
	}

}
