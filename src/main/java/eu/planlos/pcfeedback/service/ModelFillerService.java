package eu.planlos.pcfeedback.service;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import eu.planlos.pcfeedback.constants.ApplicationPath;
import eu.planlos.pcfeedback.constants.ApplicationProfile;
import eu.planlos.pcfeedback.model.Participant;
import eu.planlos.pcfeedback.model.RatingQuestion;

@Service
public class ModelFillerService implements EnvironmentAware {

	private static final Logger logger = LoggerFactory.getLogger(ModelFillerService.class);
	
	@Value("${eu.planlos.pcfeedback.need-mail}")
	private boolean needMail;
	
	@Value("${eu.planlos.pcfeedback.need-mobile}")
	private boolean needMobile;

	@Value("${eu.planlos.pcfeedback.question-count}")
	public int neededQuestionCount;
	
	@Autowired
	private Environment environment;
	public void fillGlobal(Model model) {

		logger.debug("Adding configs from application.properties");
		model.addAttribute("needMail", needMail);
		model.addAttribute("needMobile", needMobile);
		
		logger.debug("Preparing model for global area");
		model.addAttribute("URL_HOME", ApplicationPath.URL_HOME);
		model.addAttribute("URL_IMPRESSUM", ApplicationPath.URL_IMPRESSUM);
		model.addAttribute("URL_DATENSCHUTZ", ApplicationPath.URL_DATENSCHUTZ);

		logger.debug("Preparing model for home area");
		model.addAttribute("NEEDED_QUESTION_COUNT", neededQuestionCount);
		
		logger.debug("Preparing model for feedback start area");
		model.addAttribute("URL_FEEDBACK_START", ApplicationPath.URL_FEEDBACK_START);
		model.addAttribute("URL_FEEDBACK_SUBMIT", ApplicationPath.URL_FEEDBACK_SUBMIT);
		
		logger.debug("Preparing model for anonymous area");
		model.addAttribute("URL_LOGIN_FORM", ApplicationPath.URL_LOGIN_FORM);
		model.addAttribute("URL_LOGIN", ApplicationPath.URL_LOGIN);

		logger.debug("Preparing model for administration area");
		model.addAttribute("URL_ADMIN_SHOWFEEDBACK", ApplicationPath.URL_ADMIN_SHOWFEEDBACK);
		model.addAttribute("URL_ADMIN_SHOWUSERAGENTS", ApplicationPath.URL_ADMIN_SHOWUSERAGENTS);
		model.addAttribute("URL_LOGOUT", ApplicationPath.URL_LOGOUT);

		
		
		/*
		 * URLs for DEV profile
		 */
		List<String> profiles = Arrays.asList(environment.getActiveProfiles());
		boolean isDevProfile = profiles.contains(ApplicationProfile.DEV_PROFILE);

		if (isDevProfile) {
			logger.debug("Preparing model for DEV profile.");
			model.addAttribute("isDevProfile", true);
		}
	}

	public void fillExport(Model model, List<Participant> randomParticipantList, List<Participant> participantList, List<RatingQuestion> rqListMale, List<RatingQuestion> rqListFemale) {
		model.addAttribute("randomParticipantList", randomParticipantList);
		model.addAttribute("participantList", participantList);
		model.addAttribute("rqListMale", rqListMale);
		model.addAttribute("rqListFemale", rqListFemale);
	}

	public void fillError(Model model, int statusCode, String errorTitle, String errorMessage, Exception errorException, String errorTrace, boolean printTrace) {
		
		logger.debug("Preparing model for error area");

		model.addAttribute("statusCode", statusCode);
		model.addAttribute("errorTitle", errorTitle);
		
		if(printTrace) {
			model.addAttribute("errorMessage", errorMessage);
			model.addAttribute("errorException", errorException);
			model.addAttribute("errorTrace", errorTrace);
		}		
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}
}
