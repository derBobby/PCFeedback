package eu.planlos.pcfeedback.mail;

import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

public class MailSender extends JavaMailSenderImpl {
	
	private boolean active;
		
	/**
	 * By default sending of mails is inactive
	 * If 
	 */
	public MailSender(boolean active) {
		this.active = active;
	}

	public MailSender(boolean active, String mailHost, int mailPort, String mailUsername, String mailPassword, Properties props) {
		this.active = active;
		setHost(mailHost);
		setPort(mailPort);
		setUsername(mailUsername);
		setPassword(mailPassword);
		setJavaMailProperties(props);
	}

	public boolean isActive() {
		return this.active;
	}
}
