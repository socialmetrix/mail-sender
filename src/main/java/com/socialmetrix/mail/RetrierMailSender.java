package com.socialmetrix.mail;

import com.socialmetrix.mail.exceptions.MailSenderException;

/**
 * Calls the original mail sender, retrying after each
 * {@code MailSenderException} with an exponential back-off algorithm.
 */
public class RetrierMailSender implements MailSender {

	private FailureListener listener;
	private MailSender mailSender;
	private int retries;
	private int minWait;
	private int maxWait;

	/**
	 * @param retries
	 *            Number of retires per send.
	 * @param minWait
	 *            Minimum time (in milliseconds) to wait after a send error.
	 * @param maxWait
	 *            Maximum time (in milliseconds) to wait after a send error.
	 */
	public RetrierMailSender(int retries, int minWait, int maxWait, MailSender mailSender) {
		this.retries = retries;
		this.maxWait = maxWait;
		this.mailSender = mailSender;
	}

	@Override
	public void send(Mail mail) {
		int currentTry = 0;

		while (currentTry < this.retries) {
			try {
				currentTry++;
				this.mailSender.send(mail);
				return;
			} catch (MailSenderException e) {
				this.notify(mail, e);

				long backoffDuration = getSleepDuration(currentTry, this.minWait, this.maxWait);
				try {
					Thread.sleep(backoffDuration);
				} catch (InterruptedException e1) {
					throw new RuntimeException(e1);
				}
			}
		}

		throw new MailSenderException(mail);
	}

	private static long getSleepDuration(int currentTry, long minSleepMillis, long maxSleepMillis) {
		long currentSleepMillis = (long) (minSleepMillis * Math.pow(2, currentTry));
		return Math.min(currentSleepMillis, maxSleepMillis);
	}

	private void notify(Mail mail, MailSenderException e) {
		if (this.listener != null) {
			this.listener.sendFailure(mail, e);
		}
	}

	public void setListener(FailureListener listener) {
		this.listener = listener;
	}

	public interface FailureListener {
		void sendFailure(Mail mail, MailSenderException exception);
	}

}
