package eu.planlos.pcfeedback.auth.keycloak.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import eu.planlos.pcfeedback.config.RoleConfiguration;

@Profile("KC")
@Configuration
public class RoleConfigurationKeycloak {
	
	private static final Logger LOG = LoggerFactory.getLogger(RoleConfigurationKeycloak.class);
	
	@Value("${eu.planlos.pcfeedback.auth.keycloak.role.admin}")
	private String adminRole;

	@Bean(name = "roleConfiguration")
	public RoleConfiguration roleConfiguration() {
		LOG.debug("Creating RoleConfiguration for Keycloak auth");
		return new RoleConfiguration(adminRole);
	}
}
