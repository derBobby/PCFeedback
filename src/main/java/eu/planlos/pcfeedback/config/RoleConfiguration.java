package eu.planlos.pcfeedback.config;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import eu.planlos.pcfeedback.constants.ApplicationProfileHelper;
import eu.planlos.pcfeedback.exceptions.KeycloakRoleNotSetException;

@Component
public class RoleConfiguration implements EnvironmentAware {
	
	private static final Logger LOG = LoggerFactory.getLogger(RoleConfiguration.class);
	
	private Environment environment;
	
	@Value("${eu.planlos.pcfeedback.keycloak.role.admin}")
	private String ROLE_KEYCLOAK_ADMIN;
	
	public static String ROLE_ADMIN;


	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}
	
	@PostConstruct
	private void init() throws KeycloakRoleNotSetException {
		
		List<String> profiles = Arrays.asList(environment.getActiveProfiles());

		if (profiles.contains(ApplicationProfileHelper.KEYCLOAK_PROFILE)) {
		
			if(ROLE_KEYCLOAK_ADMIN == null || ROLE_KEYCLOAK_ADMIN.equals("")) {
				throw new KeycloakRoleNotSetException("Parameter for admin role not set");
			}
			ROLE_ADMIN = ROLE_KEYCLOAK_ADMIN;
		} else {
			ROLE_ADMIN = "ROLE_ADMIN";
		}
		
		LOG.debug("Admin role is set to: {}", ROLE_ADMIN);
	}
}
