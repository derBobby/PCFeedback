package eu.planlos.pcfeedback.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthConfiguration {

	private static final Logger LOG = LoggerFactory.getLogger(AuthConfiguration.class);
	
	private String adminRole;
	private String logoutUrl;
	
	public AuthConfiguration(String adminRole, String logoutUrl) {
		LOG.debug("Created AuthConfiguration with adminRole={}, logoutUrl={}", adminRole, logoutUrl);
		this.adminRole = adminRole;
		this.logoutUrl = logoutUrl;
	}

	public String getAdminRole() {
		LOG.debug("Giving adminRole={}", adminRole);
		return this.adminRole;
	}
	
	public String getLogoutUrl() {
		LOG.debug("Giving logoutUrl={}", logoutUrl);
		return this.logoutUrl;
	}
}
