package eu.planlos.pcfeedback.mail.config;

import eu.planlos.pcfeedback.mail.MailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Configuration
public class MailConfig {

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
	public MailSender mailSender() {

		// Initialize mail sender, if mail sending is active
		if(mailSendingActive) {
			Properties props = new Properties();
			props.put("mail.transport.protocol", mailTransportProtocol);
			props.put("mail.smtp.auth", mailSmtpAuth);
			props.put("mail.smtp.starttls.enable", mailSmtpStarttlsEnable);
			props.put("mail.debug", mailDebug);
			return new MailSender(true, mailHost, mailPort, mailUsername, mailPassword, props);
		}

		// Else inactive mail sender
		return new MailSender(false);
	}

	@Bean
	public MimeMessage mimeMessage(JavaMailSender javaMailSender) throws MessagingException {
		
		MimeMessage mail = javaMailSender.createMimeMessage();
		MimeMessageHelper mailHelper = new MimeMessageHelper(mail, true);
		
		mailHelper.setFrom(mailUsername);
		mailHelper.setTo(mailUsername);
		mailHelper.setReplyTo(mailUsername);

		mail.setContent("", "text/html; charset=utf-8");
		
		return mail;
	}
}