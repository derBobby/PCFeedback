package eu.planlos.pcfeedback.mail.config;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import eu.planlos.pcfeedback.constants.ApplicationProfileHelper;
import eu.planlos.pcfeedback.mail.MailSender;

@Configuration
public class MailConfig implements EnvironmentAware {

	private static final Logger LOG = LoggerFactory.getLogger(MailConfig.class);
	
	private Environment environment;
	
	@Value("${eu.planlos.pcfeedback.mail.active:false}")
	private boolean mailSendingActive;
	
	@Value("${spring.mail.properties.mail.transport.protocol:@null}")
	private String mailTransportProtocol;
	
	@Value("${spring.mail.properties.mail.smtp.auth:@null}")
	private String mailSmtpAuth;
	
	@Value("${spring.mail.properties.mail.smtp.starttls.enable:@null}")
	private String mailSmtpStarttlsEnable;
	
	@Value("${spring.mail.properties.mail.debug:@null}")
	private String mailDebug;
	
	@Value("${spring.mail.host:#null}")
	private String mailHost;
	
	@Value("${spring.mail.port:0}")
	private int mailPort;
	
	@Value("${spring.mail.username:#null}")
	private String mailUsername;
	
	@Value("${spring.mail.password:#null}")
	private String mailPassword;
	
	@Bean
	public MailSender getJavaMailSender() {
		
		MailSender mailSender = new MailSender();
		
		if(mailSendingActive) {
			mailSender.setActive();
			
			mailSender.setHost(mailHost);
			mailSender.setPort(mailPort);

			mailSender.setUsername(mailUsername);
			mailSender.setPassword(mailPassword);

			Properties props = mailSender.getJavaMailProperties();
			props.put("mail.transport.protocol", mailTransportProtocol);
			props.put("mail.smtp.auth", mailSmtpAuth);
			props.put("mail.smtp.starttls.enable", mailSmtpStarttlsEnable);
			props.put("mail.debug", mailDebug);
			
			printCredentials();
		}

		return mailSender;
	}

	@Bean
	public MimeMessage emailTemplate(JavaMailSender javaMailSender) throws MessagingException {
		
		MimeMessage mail = javaMailSender.createMimeMessage();
		MimeMessageHelper mailHelper = new MimeMessageHelper(mail, true);
		
		mailHelper.setFrom(mailUsername);
		mailHelper.setTo(mailUsername);
		mailHelper.setReplyTo(mailUsername);

		mail.setContent("", "text/html; charset=utf-8");
		
		return mail;
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}
	
	/**
	 * Prints credentials given that according profile DEV or REV is running
	 */
	private void printCredentials() {
		
		List<String> profiles = Arrays.asList(environment.getActiveProfiles());

		if (profiles.contains(ApplicationProfileHelper.DEV_PROFILE)
				|| profiles.contains(ApplicationProfileHelper.REV_PROFILE)) {
			LOG.debug("Setup email account as user='{}' password='{}' socket='{}:{}'", mailUsername, mailPassword, mailHost, mailPort);
		}
	}
}