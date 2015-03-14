package com.socialmetrix.mail;

import static com.socialmetrix.mail.MailBuilder.mail;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.socialmetrix.mail.RetrierMailSender.FailureListener;
import com.socialmetrix.mail.exceptions.MailSenderException;

@RunWith(MockitoJUnitRunner.class)
public class RetrierMailSenderTest {

	@Mock
	private MailSender mailSender;
	@Mock
	private FailureListener failureListener;
	private Mail mail;
	private MailSenderException exception;
	private RetrierMailSender retrier;

	@Before
	public void setUp() {
		mail = mail().from("me@me.com").build();
		exception = new MailSenderException(mail);
		retrier = new RetrierMailSender(6, 1, 100, mailSender);
	}

	@Test
	public void canSendTheMailEvenIfTheListenerIsNotDefined() {
		doThrow(exception) //
			.doNothing()
			.when(mailSender)
			.send(mail);

		retrier.send(mail);

		verify(mailSender, times(2)).send(mail);
		verifyZeroInteractions(failureListener);
	}

	@Test
	public void ifAllAtemptsFailsCallTheListenerForEachOneAndFinallyThrowsException() {
		retrier.setListener(failureListener);
		doThrow(exception).when(mailSender).send(mail);

		try {
			retrier.send(mail);
		} catch (MailSenderException e) {
			assertEquals(mail, e.getMail());
			verify(mailSender, times(6)).send(mail);
			verify(failureListener, times(6)).sendFailure(mail, exception);
			return;
		}

		fail();
	}

	@Test
	public void ifSomeFailsCallsTheListener() {
		retrier.setListener(failureListener);
		doThrow(exception) //
			.doThrow(exception)
			.doThrow(exception)
			.doNothing()
			.when(mailSender)
			.send(mail);

		retrier.send(mail);

		verify(mailSender, times(4)).send(mail);
		verify(failureListener, times(3)).sendFailure(mail, exception);
	}

	@Test
	public void doesNotCallTheListenerIfNoErrorOcurred() {
		retrier.setListener(failureListener);

		retrier.send(mail);

		verify(mailSender).send(mail);
		verifyZeroInteractions(failureListener);
	}

}
