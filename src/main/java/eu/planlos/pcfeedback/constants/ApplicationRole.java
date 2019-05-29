package eu.planlos.pcfeedback.constants;

public class ApplicationRole {

	// Role is assigned as long as the user is not starting the feedback process or logged in as administrator  
	public static final String ROLE_ANONYMOUS 		= "ROLE_ANONYMOUS";
	
	// Role is assigned while the user is in the feedback process
	public static final String ROLE_PARTICIPANT		= "ROLE_PARTICIPANT";
	
	// Role is assigned after the user logs in as administrator until he logs out
	public static final String ROLE_ADMIN 			= "ROLE_ADMIN";
}
