package eu.planlos.pcfeedback.config;

public class RoleConfiguration {

	private String adminRole;
	
	public RoleConfiguration(String role) {
		this.adminRole = role;
	}
	
	public String getAdminRole() {
		return this.adminRole;
	}
}
