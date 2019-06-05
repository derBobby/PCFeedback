package eu.planlos.pcfeedback.constants;

public class ApplicationPath {

	
	/*
	 * GLOBAL
	 */
	public static final String URL_HOME 						= "/";
	public static final String URL_IMPRESSUM 					= "https://teennight.de/Impressum";
	public static final String URL_DATENSCHUTZ 					= "https://teennight.de/Datenschutz";

	public static final String RES_HOME 						= "home";
	
	// ERROR
	public static final String URL_ERROR						= "/error";
	
	public static final String RES_ERROR_403 					= "error_403";
	public static final String RES_ERROR						= "error";
	
	
	
	/*
	 * PARTICIPATION
	 */
	public static final String URL_FEEDBACK_START 				= "/feedbackstart";
	public static final String URL_FEEDBACK		 				= "/feedback";
	public static final String URL_RESTART	 					= "/restart";
	
	public static final String RES_FEEDBACK_START 				= "feedbackstart";
	public static final String RES_FEEDBACK 					= "feedback";
	public static final String RES_FEEDBACK_END 				= "feedbackend";
	
	
	
	/*
	 * ANONYMOUS
	 */
	public static final String URL_LOGIN_FORM					= "/loginform";
	public static final String URL_LOGIN						= "/login";
	public static final String URL_LOGOUT						= "/logout";

	public static final String RES_LOGIN_FORM					= "loginform";
	


	/*
	 * ADMINISTRATION
	 */
	public static final String URL_ADMIN						= "/admin"; //TODO
	public static final String URL_ADMIN_CONFIG					= "/admin/config"; //TODO
	public static final String URL_ADMIN_RATINGOBJECTS			= "/admin/ratingobjects"; //TODO
	public static final String URL_ADMIN_EXPORTFEEDBACK					= "/admin/exportfeedback"; //TODO
	
	public static final String RES_ADMIN_EXPORT					= "admin/exportfeedback";
}
