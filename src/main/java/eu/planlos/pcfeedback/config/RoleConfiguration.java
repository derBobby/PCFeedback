package eu.planlos.pcfeedback.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoleConfiguration {

	private static final Logger LOG = LoggerFactory.getLogger(RoleConfiguration.class);
	
	private String adminRole;
	
	public RoleConfiguration(String role) {
		LOG.debug("Created RoleConfiguration with adminRole={}", role);
		this.adminRole = role;
	}
	
	public String getAdminRole() {
		LOG.debug("Giving adminRole={}", adminRole);
		return this.adminRole;
	}
}
