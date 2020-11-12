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
	public static final String URL_IMPRESSUM 					= "https://www.swdec.de/impressum/";
	public static final String URL_DATENSCHUTZ 					= "https://www.swdec.de/datenschutz/";
	
	
	
	/*
	 * ACTUATOR AREA
	 */
	public static final String URL_AREA_ACTUATOR 				= URL_DELIMETER + "actuator";
	
	
	
	/*
	 * PUBLIC
	 */
	public static final String URL_HOME 						= URL_AREA_PUBLIC;
	public static final String URL_PROJECTHOME 					= URL_AREA_PUBLIC + "project" + URL_DELIMETER;
	public static final String URL_FEEDBACK_START 				= URL_AREA_PUBLIC + "feedbackstart";
	public static final String URL_FEEDBACK_FREETEXT_SUBMIT 	= URL_AREA_PUBLIC + "feedbacksubmit";
	public static final String URL_FEEDBACK_QUESTION		 	= URL_AREA_PUBLIC + "feedbackquestion";
	public static final String URL_FEEDBACK_SUBMIT				= URL_AREA_PUBLIC + "feedbackfreetext";
	public static final String URL_FEEDBACK_END		 			= URL_AREA_PUBLIC + "feedbackend";
	public static final String URL_PRICEGAME 					= URL_AREA_PUBLIC + "gewinnspielhinweise" + URL_DELIMETER;
	public static final String RES_HOME 						= "home";
	public static final String RES_PROJECTHOME 					= "projecthome";
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
	public static final String URL_ADMIN_PROJECTS				= URL_AREA_ADMIN + "projects";
	public static final String URL_ADMIN_PROJECTRUN				= URL_AREA_ADMIN + "projectrun" + URL_DELIMETER;
	public static final String URL_ADMIN_PROJECTRESET			= URL_AREA_ADMIN + "projectreset" + URL_DELIMETER;
	public static final String URL_ADMIN_PROJECTDELETE			= URL_AREA_ADMIN + "projectdelete" + URL_DELIMETER;
	public static final String URL_ADMIN_PROJECTDETAILS			= URL_AREA_ADMIN + "projectdetails" + URL_DELIMETER;
	public static final String URL_ADMIN_SHOWFEEDBACK			= URL_AREA_ADMIN + "showresults" + URL_DELIMETER;
	public static final String URL_ADMIN_SHOWUSERAGENTS			= URL_AREA_ADMIN + "showuseragents" + URL_DELIMETER;
	public static final String URL_ADMIN_EDITPARTICIPANT		= URL_AREA_ADMIN + "editparticipant" + URL_DELIMETER;
	public static final String URL_ADMIN_DELETEPARTICIPANT		= URL_AREA_ADMIN + "deleteparticipant" + URL_DELIMETER;
	public static final String URL_ADMIN_EDITUITEXT				= URL_AREA_ADMIN + "edituitext" + URL_DELIMETER;
	//CSV Buttons
	public static final String URL_ADMIN_CSV					= URL_AREA_ADMIN + "csv" + URL_DELIMETER;
	public static final String URL_ADMIN_CSVPARTICIPANTS		= URL_ADMIN_CSV + "participants" + URL_DELIMETER;
	public static final String URL_ADMIN_CSVFEEDBACK			= URL_ADMIN_CSV + "feedback" + URL_DELIMETER;
	public static final String URL_ADMIN_CSVFEEDBACK_M			= URL_ADMIN_CSV + "feedbackm" + URL_DELIMETER;
	public static final String URL_ADMIN_CSVFEEDBACK_W			= URL_ADMIN_CSV + "feedbackw" + URL_DELIMETER;
	public static final String URL_ADMIN_CSVFEEDBACK_FREETEXT	= URL_ADMIN_CSV + "feedbackfreetext" + URL_DELIMETER;

	public static final String RES_ADMIN_PROJECTS				= "admin/projects";
	public static final String RES_ADMIN_PROJECTDETAILS			= "admin/projectdetails";
	public static final String RES_ADMIN_SHOWFEEDBACK			= "admin/showresults";
	public static final String RES_ADMIN_SHOWUSERAGENTS			= "admin/showuseragents";
	public static final String RES_ADMIN_EDITPARTICIPANT		= "admin/editparticipant";
	public static final String RES_ADMIN_EDITPARTICIPANTDONE	= "admin/editparticipantdone";
	public static final String RES_ADMIN_EDITUITEXT				= "admin/edituitexts";
	
}
