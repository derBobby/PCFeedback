package eu.planlos.pcfeedback.auth.basic.config;

import eu.planlos.pcfeedback.config.AuthConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class AuthConfigurationBasic {
	
	private static final String ROLE_ADMIN = "ADMIN";
	private static final String URL_LOGOUT = "/admin/logout";
	
	@Bean(name = "authConfiguration")
	public AuthConfiguration authConfiguration() {
		log.debug("Creating AuthConfiguration for basic auth");
		return new AuthConfiguration(ROLE_ADMIN, URL_LOGOUT);
	}
}
