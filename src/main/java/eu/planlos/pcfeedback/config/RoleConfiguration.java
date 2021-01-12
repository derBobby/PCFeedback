package eu.planlos.pcfeedback.config;

// keine Ahnung wofür diesee Klasse gedacht ist

public class RoleConfiguration {

	private String adminRole;
	
	public RoleConfiguration(String role) {
		this.adminRole = role;
	}
	
	public String getAdminRole() {
		return this.adminRole;
	}
}
