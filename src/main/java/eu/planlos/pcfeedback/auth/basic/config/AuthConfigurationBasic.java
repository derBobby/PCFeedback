package eu.planlos.pcfeedback.auth.basic.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import eu.planlos.pcfeedback.config.AuthConfiguration;

@Configuration
public class AuthConfigurationBasic {
	
	private static final Logger LOG = LoggerFactory.getLogger(AuthConfigurationBasic.class);
	private static final String ROLE_ADMIN = "ADMIN";
	private static final String URL_LOGOUT = "/admin/logout";
	
	@Bean(name = "authConfiguration")
	public AuthConfiguration authConfiguration() {
		LOG.debug("Creating AuthConfiguration for basic auth");
		return new AuthConfiguration(ROLE_ADMIN, URL_LOGOUT);
	}
}
