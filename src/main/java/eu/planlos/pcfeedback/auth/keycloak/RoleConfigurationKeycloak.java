package eu.planlos.pcfeedback.auth.keycloak;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import eu.planlos.pcfeedback.config.RoleConfiguration;

@Profile("KEYCLOAK")
@Configuration
public class RoleConfigurationKeycloak {
	
	private static final Logger LOG = LoggerFactory.getLogger(RoleConfigurationKeycloak.class);
	
	@Value("${eu.planlos.pcfeedback.auth.keycloak.role.admin}")
	private String adminRole;
	

	@Bean
	public RoleConfiguration roleConfiguration() {
		LOG.debug("Creating admin role text for Keycloak login");
		return new RoleConfiguration(adminRole);
	}
}
