package eu.planlos.pcfeedback.mail;

import org.springframework.mail.javamail.JavaMailSenderImpl;

public class MailSender extends JavaMailSenderImpl {
	
	private boolean active;
		
	/**
	 * By default sending of mails is inactive
	 * If 
	 */
	public MailSender() {
		active = false;
	}
	
	public boolean isActive() {
		return this.active;
	}
	
	public void setActive() {
		this.active = true;
	}
}
