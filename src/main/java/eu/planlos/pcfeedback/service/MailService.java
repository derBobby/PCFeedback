package eu.planlos.pcfeedback.service;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import eu.planlos.pcfeedback.constants.ApplicationProfileHelper;
import eu.planlos.pcfeedback.model.db.Project;

@Service
public class MailService implements EnvironmentAware {
	
	private static final Logger LOG = LoggerFactory.getLogger(MailService.class);

	@Autowired
	private JavaMailSender javaMailSender;

	@Value("${eu.planlos.pcfeedback.mail.sender}")
	private String mailSender;

	@Value("${spring.mail.host}")
	private String springMailHost;
	
	@Value("${spring.mail.port}")
	private String springMailPort;
	
	@Value("${spring.mail.username}")
	private String springMailUsername;

	@Value("${spring.mail.password}")
	private String springMailPassword;

	private Environment environment;
	
	@Async
	public void notifyParticipation(Project project) {
		
		LOG.debug("Sending notification email");
		
		MimeMessageHelper mailHelper;
		MimeMessage mail = javaMailSender.createMimeMessage();
		
		try {
			
			
			
			mailHelper = new MimeMessageHelper(mail, true);
			mailHelper.setSubject(String.format("%sPCFeedback - Update Benachrichtigung", subjectPrefix()));
			mailHelper.setFrom(mailSender);
			mailHelper.setTo(project.getNotificationMail());
			
			String htmlBody = String.format("Neue Teilnahme an Umfrage '%s'", project.getProjectName());

			mail.setContent(htmlBody, "text/html; charset=utf-8");
			
			javaMailSender.send(mail);
			LOG.debug("Notification email has been sent.");
			
		} catch (MessagingException e) {
			LOG.error("Notification email could not been sent: {}", e.getMessage());
			e.printStackTrace();
		}
	}
		
	private String subjectPrefix() {
		
		List<String> profiles = Arrays.asList(environment.getActiveProfiles());

		if (profiles.contains(ApplicationProfileHelper.DEV_PROFILE)) {
			return "DEV - ";
		}
		if (profiles.contains(ApplicationProfileHelper.REV_PROFILE)) {
			return "REV - ";
		}
		
		return "";
	}

	@PostConstruct
	@Profile(value = {"DEV", "REV"})
	private void printCredentials() {
		LOG.debug("Setup email account as user='{}' password='{}' socket='{}:{}'", springMailUsername, springMailPassword, springMailHost, springMailPort);
	}
	
	@PostConstruct
	@Async
	private void notifyStart() {
		
		LOG.debug("Sending notification email");
		
		MimeMessageHelper mailHelper;
		MimeMessage mail = javaMailSender.createMimeMessage();
		
		try {
			
			mailHelper = new MimeMessageHelper(mail, true);
			mailHelper.setSubject(String.format("%sPCFeedback - Server gestartet", subjectPrefix()));
			mailHelper.setFrom(mailSender);
			mailHelper.setTo(mailSender);
			
			String htmlBody = String.format("");

			mail.setContent(htmlBody, "text/html; charset=utf-8");
			
			javaMailSender.send(mail);
			LOG.debug("Notification email has been sent.");
			
		} catch (MessagingException e) {
			LOG.error("Notification email could not been sent: {}", e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}
}
