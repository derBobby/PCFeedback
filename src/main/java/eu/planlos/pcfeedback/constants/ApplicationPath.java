package eu.planlos.pcfeedback.constants;

public class ApplicationPath {

	/*
	 * URL AREAS
	 */
	public static final String URL_DELIMETER 					= "/";
	public static final String URL_AREA_PUBLIC 					= URL_DELIMETER;
	public static final String URL_AREA_ADMIN 					= URL_DELIMETER + "admin" + URL_DELIMETER;
	
	
	
	/*
	 * EXTERNAL
	 */
	public static final String URL_IMPRESSUM 					= "https://teennight.de/Impressum";
	public static final String URL_DATENSCHUTZ 					= "https://teennight.de/Datenschutz";
	
	
	
	/*
	 * PUBLIC
	 */
	public static final String URL_HOME 						= URL_AREA_PUBLIC;
	public static final String URL_FEEDBACK_START 				= URL_AREA_PUBLIC + "feedbackstart";
	public static final String URL_FEEDBACK		 				= URL_AREA_PUBLIC + "feedback";
	public static final String URL_FEEDBACK_END		 			= URL_AREA_PUBLIC + "feedbackend";
	public static final String RES_HOME 						= "home";
	public static final String RES_FEEDBACK_START 				= "feedbackstart";
	public static final String RES_FEEDBACK 					= "feedback";
	public static final String RES_FEEDBACK_END 				= "feedbackend";
	
	// ERROR
	public static final String URL_ERROR						= URL_AREA_PUBLIC + "error";
	public static final String RES_ERROR						= "error";
	
	
	
	/*
	 * ANONYMOUS
	 */
	public static final String URL_LOGIN_FORM					= URL_AREA_PUBLIC + "loginform";
	public static final String URL_LOGIN						= URL_AREA_PUBLIC + "login";
	public static final String URL_LOGOUT						= URL_AREA_ADMIN + "logout";
	public static final String RES_LOGIN_FORM					= "loginform";
	


	/*
	 * ADMINISTRATION
	 */
	public static final String URL_ADMIN_CONFIG					= URL_AREA_ADMIN + "config"; //TODO
	public static final String URL_ADMIN_EXPORTFEEDBACK			= URL_AREA_ADMIN + "exportfeedback"; //TODO
	public static final String RES_ADMIN_EXPORT					= "admin/exportfeedback";
}
