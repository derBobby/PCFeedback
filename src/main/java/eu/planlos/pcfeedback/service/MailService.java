package eu.planlos.pcfeedback.service;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import eu.planlos.pcfeedback.constants.ApplicationProfiles;
import eu.planlos.pcfeedback.mail.MailSender;
import eu.planlos.pcfeedback.model.db.Project;

@Slf4j
@Service
public class MailService implements EnvironmentAware {
	
	private static final String MIME_TYPE = "text/html; charset=utf-8";
	
	private MailSender mailSender;
	private MimeMessage preconfiguredMessage;

	public MailService(MailSender mailSender, MimeMessage preconfiguredMessage) {
		this.mailSender=mailSender;
		this.preconfiguredMessage=preconfiguredMessage;
	}
	
	private Environment environment;
	
	@Async
	public void notifyParticipation(Project project) {
		
		if(! mailSender.isActive()) {
			logNotSending();
			return;
		}
		logSending();
		
		try {
			MimeMessage message = new MimeMessage(preconfiguredMessage);
			message.setSubject(buildSubject("PCFeedback - Update Benachrichtigung"));
			message.setContent(String.format("Neue Teilnahme an Umfrage '%s'", project.getProjectName()), MIME_TYPE);
						
			mailSender.send(message);
			logMailOK();
			
		} catch (MessagingException e) {
			logMailError(e.getMessage());
		}
	}
	
	@PostConstruct
	@Async
	void notifyStart() {
				
		if(! mailSender.isActive()) {
			logNotSending();
			return;
		}
		logSending();
		
		try {
			MimeMessage message = new MimeMessage(preconfiguredMessage);
			message.setSubject(buildSubject("PCFeedback - Server gestartet"));
			message.setContent(String.format(""), MIME_TYPE);
		
			mailSender.send(message);
			logMailOK();
			
		} catch (MessagingException e) {
			e.printStackTrace();
			logMailError(e.getMessage());
		}
	}

	private String buildSubject(String subject) {

		List<String> profiles = Arrays.asList(environment.getActiveProfiles());

		if (profiles.contains(ApplicationProfiles.DEV_PROFILE)) {
			return String.format("DEV - %s", subject);
		}
		if (profiles.contains(ApplicationProfiles.REV_PROFILE)) {
			return String.format("REV - %s", subject);
		}

		return "";
	}

	private void logMailError(String message) {
		log.error("Notification email could not been sent: {}", message);
	}
	
	private void logMailOK() {
		log.debug("Notification email has been sent.");
	}

	private void logNotSending() {
		log.debug("Not sending notification email");
	}
	
	private void logSending() {
		log.debug("Sending notification email");
	}
	
	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}
}
