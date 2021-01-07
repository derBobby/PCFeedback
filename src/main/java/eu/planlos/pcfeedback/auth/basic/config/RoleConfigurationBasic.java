package eu.planlos.pcfeedback.auth.basic.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import eu.planlos.pcfeedback.config.RoleConfiguration;

@Profile("!KEYCLOAK")
@Configuration
public class RoleConfigurationBasic {
	
	private static final Logger LOG = LoggerFactory.getLogger(RoleConfigurationBasic.class);
		
	public static final String ROLE_ADMIN = "ROLE_ADMIN";
	
	@Bean(name = "roleConfiguration")
	public RoleConfiguration roleConfiguration() {
		LOG.debug("Creating RoleConfiguration for basic auth");
		return new RoleConfiguration(ROLE_ADMIN);
	}
}
