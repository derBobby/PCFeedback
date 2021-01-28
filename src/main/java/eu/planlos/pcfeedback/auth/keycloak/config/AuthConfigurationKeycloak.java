package eu.planlos.pcfeedback.auth.keycloak.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import eu.planlos.pcfeedback.config.AuthConfiguration;

@Profile("KC")
@Configuration
public class AuthConfigurationKeycloak {
	
	private static final Logger LOG = LoggerFactory.getLogger(AuthConfigurationKeycloak.class);
	
	private static final String URL_LOGOUT = "/sso/logout";

	@Value("${eu.planlos.pcfeedback.auth.keycloak.role.admin}")
	private String adminRole;
	
	@Bean(name = "authConfiguration")
	public AuthConfiguration authConfiguration() {
		LOG.debug("Creating AuthConfiguration for Keycloak auth");
		return new AuthConfiguration(adminRole, URL_LOGOUT);
	}
}
