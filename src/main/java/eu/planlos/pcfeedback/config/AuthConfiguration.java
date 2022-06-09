package eu.planlos.pcfeedback.config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
public class  AuthConfiguration {

	private String adminRole;
	private String logoutUrl;
	
	public AuthConfiguration(String adminRole, String logoutUrl) {
		log.debug("Created AuthConfiguration with adminRole={}, logoutUrl={}", adminRole, logoutUrl);
		this.adminRole = adminRole;
		this.logoutUrl = logoutUrl;
	}

	public String getAdminRole() {
		log.debug("Giving adminRole={}", adminRole);
		return this.adminRole;
	}
	
	public String getLogoutUrl() {
		log.debug("Giving logoutUrl={}", logoutUrl);
		return this.logoutUrl;
	}
}
