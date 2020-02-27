package eu.planlos.pcfeedback.constants;

public class ApplicationPathHelper {

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
	 * ACTUATOR AREA
	 */
	public static final String URL_AREA_ACTUATOR 				= URL_DELIMETER + "actuator";
	
	
	
	/*
	 * PUBLIC
	 */
	public static final String URL_HOME 						= URL_AREA_PUBLIC;
	public static final String URL_PRICEGAME 					= URL_AREA_PUBLIC + "gewinnspielhinweise";
	public static final String URL_FEEDBACK_START 				= URL_AREA_PUBLIC + "feedbackstart";
	public static final String URL_FEEDBACK_RESTART 			= URL_AREA_PUBLIC + "feedbackrestart";
	public static final String URL_FEEDBACK_FREETEXT_SUBMIT 	= URL_AREA_PUBLIC + "feedbacksubmit";
	public static final String URL_FEEDBACK_QUESTION		 	= URL_AREA_PUBLIC + "feedbackquestion";
	public static final String URL_FEEDBACK_SUBMIT				= URL_AREA_PUBLIC + "feedbackfreetext";
	public static final String URL_FEEDBACK_END		 			= URL_AREA_PUBLIC + "feedbackend";
	public static final String RES_HOME 						= "home";
	public static final String RES_PRICEGAME 					= "pricegame";
	public static final String RES_FEEDBACK_START 				= "feedbackstart";
	public static final String RES_FEEDBACK_QUESTION 			= "feedbackquestion";
	public static final String RES_FEEDBACK_FREETEXT			= "feedbackfreetext";
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
	public static final String URL_ADMIN_RESET					= URL_AREA_ADMIN + "resetdb";
	public static final String URL_ADMIN_SHOWFEEDBACK			= URL_AREA_ADMIN + "showresults";
	public static final String URL_ADMIN_SHOWUSERAGENTS			= URL_AREA_ADMIN + "showuseragents";
	public static final String URL_ADMIN_EDITPARTICIPANT		= URL_AREA_ADMIN + "editparticipant" + URL_DELIMETER;
	public static final String URL_ADMIN_DELETEPARTICIPANT		= URL_AREA_ADMIN + "deleteparticipant" + URL_DELIMETER;
	public static final String URL_ADMIN_EDITUITEXT				= URL_AREA_ADMIN + "edituitext" + URL_DELIMETER;
	public static final String URL_ADMIN_CSVPARTICIPANTS		= URL_AREA_ADMIN + "csvparticipants" + URL_DELIMETER;
	public static final String URL_ADMIN_CSVFEEDBACK			= URL_AREA_ADMIN + "csvfeedback" + URL_DELIMETER;
	public static final String URL_ADMIN_CSVFEEDBACK_M			= URL_AREA_ADMIN + "csvfeedbackm" + URL_DELIMETER;
	public static final String URL_ADMIN_CSVFEEDBACK_W			= URL_AREA_ADMIN + "csvfeedbackw" + URL_DELIMETER;

	public static final String RES_ADMIN_SHOWFEEDBACK			= "admin/showresults";
	public static final String RES_ADMIN_SHOWUSERAGENTS			= "admin/showuseragents";
	public static final String RES_ADMIN_EDITPARTICIPANT		= "admin/editparticipant";
	public static final String RES_ADMIN_EDITPARTICIPANTDONE	= "admin/editparticipantdone";
	public static final String RES_ADMIN_EDITUITEXT				= "admin/edituitexts";
	
}
