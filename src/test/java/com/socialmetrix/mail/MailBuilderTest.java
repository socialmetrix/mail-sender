package com.socialmetrix.mail;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.UnsupportedEncodingException;
import java.util.*;

import javax.mail.internet.*;

import org.junit.*;

public class MailBuilderTest {

	private HttpTemplateRenderer templateRenderer;
	private MailBuilder builder;

	@Before
	public void setUp() {
		this.templateRenderer = mock(HttpTemplateRenderer.class);
		when(this.templateRenderer.renderUrlTemplate(anyString(), anyObject())).thenReturn("my rendered body");
		this.builder = new MailBuilder(this.templateRenderer);
	}

	@Test
	public void canCreateANewEmail() {
		Mail mail = this.builder //
			.from("My Name", "my@address.com")
			.replyTo("Other", "other@notme.com")
			.onlyTo("Your Address", "your@mail.com")
			.onlyBcc("Internal Support", "us@support.com.xx")
			.subject("Something important")
			.html("<p>Hi, you.</p>")
			.text("You can't read html.")
			.build();

		assertEquals("my@address.com", mail.getFrom().getAddress());
		assertEquals("My Name", mail.getFrom().getPersonal());
		assertEquals("other@notme.com", mail.getReplyto().getAddress());
		assertEquals("Other", mail.getReplyto().getPersonal());

		assertEquals(1, mail.getTo().size());
		assertEquals("your@mail.com", mail.getTo().get(0).getAddress());
		assertEquals("Your Address", mail.getTo().get(0).getPersonal());

		assertEquals(1, mail.getBcc().size());
		assertEquals("us@support.com.xx", mail.getBcc().get(0).getAddress());
		assertEquals("Internal Support", mail.getBcc().get(0).getPersonal());

		assertEquals("Something important", mail.getSubject());
		assertEquals("<p>Hi, you.</p>", mail.getHtml());
		assertEquals("You can't read html.", mail.getText());
	}

	@Test
	public void canCreateAMailWithNonAsciiChars() throws AddressException {
		Mail mail = this.builder //
			.from("Martín Paragüas", "my@address.com")
			.replyTo("Some \" crazy 'char=:s", "other@notme.com")
			.onlyTo("Some ñato", "your@mail.com")
			.onlyBcc("Internal Support", "us@support.com.xx")
			.subject("Something important")
			.html("<p>Hi, you.</p>")
			.text("You can't read html.")
			.build();

		mail.getReplyto().validate();

		assertEquals("=?UTF-8?Q?Mart=C3=ADn_Parag=C3=BCas?= <my@address.com>", mail.getFrom().toString());
		assertEquals("\"Some \\\" crazy 'char=:s\" <other@notme.com>", mail.getReplyto().toString());
		assertEquals("Some \" crazy 'char=:s", mail.getReplyto().getPersonal());
		assertEquals("=?UTF-8?Q?Some_=C3=B1ato?= <your@mail.com>", mail.getTo().get(0).toString());
		assertEquals("Internal Support <us@support.com.xx>", mail.getBcc().get(0).toString());
	}

	@Test
	public void doesNOTEncodesManualAddressFormating() throws AddressException {
		Mail mail = this.builder //
			.from("Martín Paragüas <my@address.com>")
			.replyTo("\"Some crazy 'char=:s\" <other@notme.com>")
			.onlyTo("Some ñato <your@mail.com>")
			.onlyBcc("Internal Support <us@support.com.xx>")
			.subject("Something important")
			.html("<p>Hi, you.</p>")
			.text("You can't read html.")
			.build();

		mail.getReplyto().validate();

		assertEquals("\"Martín Paragüas\" <my@address.com>", mail.getFrom().toString());
		assertEquals("\"Some crazy 'char=:s\" <other@notme.com>", mail.getReplyto().toString());
		assertEquals("Some crazy 'char=:s", mail.getReplyto().getPersonal());
		assertEquals("\"Some ñato\" <your@mail.com>", mail.getTo().get(0).toString());
		assertEquals("Internal Support <us@support.com.xx>", mail.getBcc().get(0).toString());
	}

	@Test
	public void canSendAMail() throws AddressException {
		MailSender mailSender = mock(MailSender.class);

		this.builder.from("from@from.com").onlyTo("to@to.com").html("content").send(mailSender);

		Mail expectedMail = new Mail( //
			new InternetAddress("from@from.com"),
			null,
			Arrays.asList(new InternetAddress("to@to.com")),
			new ArrayList<InternetAddress>(),
			null,
			"content",
			null);
		verify(mailSender).send(expectedMail);
		verifyNoMoreInteractions(mailSender);
	}

	@Test
	public void canUseHttpTemplates() throws UnsupportedEncodingException {
		Mail mail = this.builder //
			.from("My Name", "my@address.com")
			.replyTo("Other", "other@notme.com")
			.onlyTo("Your Address", "your@mail.com")
			.onlyBcc("Internal Support", "us@support.com.xx")
			.subject("Something important")
			.htmlFromTemplate("my template", this)
			.text("You can't read html.")
			.build();

		verify(this.templateRenderer).renderUrlTemplate("my template", this);
		verifyNoMoreInteractions(this.templateRenderer);

		Mail expectedMail = new Mail( //
			new InternetAddress("my@address.com", "My Name"),
			new InternetAddress("other@notme.com", "Other"),
			Arrays.asList(new InternetAddress("your@mail.com", "Your Address")),
			Arrays.asList(new InternetAddress("us@support.com.xx", "Internal Support")),
			"Something important",
			"my rendered body",
			"You can't read html.");

		assertEquals(expectedMail, mail);
	}
}
