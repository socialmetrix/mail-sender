package com.socialmetrix.mail;

import static com.socialmetrix.mail.MailBuilder.mail;
import static org.junit.Assert.assertTrue;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import static org.mockito.Mockito.*
;

@RunWith(MockitoJUnitRunner.class)
public class RateLimitedMailSenderTest {

	private static final int MailsPerSecond = 50;
	private static final int MailsToSend = 100;

	@Mock
	private MailSender mailSender;
	private MailSender rateLimitedSender;

	@Before
	public void setUp() {
		this.rateLimitedSender = new RateLimitedMailSender(MailsPerSecond, mailSender);
	}

	@Test
	public void totalTimeRespectsRateLimit() {
		long start = System.currentTimeMillis();

		Mail mail = mail().from("me@me.com").build();
		for (int i = 1; i <= MailsToSend; i++) {
			rateLimitedSender.send(mail);
		}
		
		long totalMillis = System.currentTimeMillis() - start;

		verify(mailSender, times(MailsToSend)).send(mail);
		int expectedTime = (MailsToSend * 1000) / MailsPerSecond;
		int delta = 50;
		int minExpected = expectedTime - delta;
		int maxExpected = expectedTime + delta;
		assertTrue(minExpected < totalMillis && totalMillis < maxExpected);
	}

}
