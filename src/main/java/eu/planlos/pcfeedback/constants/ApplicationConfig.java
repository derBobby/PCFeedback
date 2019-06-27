package eu.planlos.pcfeedback.constants;

import org.springframework.beans.factory.annotation.Value;

public class ApplicationConfig {

	public static final String TIME_ZONE = "Europe/Berlin";

	@Value("${eu.planlos.pcfeedback.question-count}")
	public static int NEEDED_QUESTION_COUNT;
	
	@Value("${eu.planlos.pcfeedback.need-mail}")
	public static boolean NEED_MAIL_ADDRESS; 		

}
