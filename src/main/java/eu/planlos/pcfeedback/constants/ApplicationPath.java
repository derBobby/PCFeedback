package eu.planlos.pcfeedback.constants;

public class ApplicationPath {

	
	/*
	 * GLOBAL
	 */
	public static final String URL_HOME 						= "/";
	public static final String URL_IMPRESSUM 					= "redirect: https://teennight.de/Impressum";
	public static final String URL_DATENSCHUTZ 					= "redirect: https://teennight.de/Datenschutz";

	public static final String RES_HOME 						= "home";
	
	
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
	 * LOGIN
	 */
	public static final String URL_LOGIN_FORM					= "/loginform";
	public static final String URL_LOGIN						= "/login";
	public static final String URL_LOGOUT						= "/logout";

	public static final String RES_LOGIN_FORM					= "loginform";
	
	
	/*
	 * ADMINISTRATION
	 */
	public static final String URL_ADMIN						= "/admin";
	public static final String URL_ADMIN_CONFIG					= "/admin/config"; //TODO
	public static final String URL_ADMIN_RATINGOBJECTS			= "/admin/ratingobjects";
	public static final String URL_ADMIN_EXPORT					= "/admin/export";


	/*
	 * ERROR
	 */
	public static final String URL_ERROR_403 					= "/403";
	public static final String URL_ERROR_DEFAULT 				= "/error";

	public static final String RES_ERROR_403 					= "error_403";
	public static final String RES_ERROR_UNKNOWN				= "error_unknown";
}
