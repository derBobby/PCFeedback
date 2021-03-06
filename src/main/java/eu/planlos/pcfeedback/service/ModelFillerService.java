package eu.planlos.pcfeedback.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import eu.planlos.pcfeedback.constants.ApplicationPathHelper;
import eu.planlos.pcfeedback.constants.ApplicationProfileHelper;
import eu.planlos.pcfeedback.model.db.Participant;
import eu.planlos.pcfeedback.model.db.ParticipationResult;
import eu.planlos.pcfeedback.model.db.Project;
import eu.planlos.pcfeedback.model.db.RatingObject;
import eu.planlos.pcfeedback.model.db.RatingQuestion;
import eu.planlos.pcfeedback.model.db.UiText;
import eu.planlos.pcfeedback.util.ZonedDateTimeUtility;

@Service
public class ModelFillerService implements EnvironmentAware {

	private static final Logger LOG = LoggerFactory.getLogger(ModelFillerService.class);
	
	private Environment environment;

	public void fillGlobal(Model model) {
				
		LOG.debug("Preparing model for global area");
		model.addAttribute("URL_HOME", ApplicationPathHelper.URL_HOME);
		model.addAttribute("URL_PROJECTHOME", ApplicationPathHelper.URL_PROJECTHOME); //TODO where necessary?
		model.addAttribute("URL_IMPRESSUM", ApplicationPathHelper.URL_IMPRESSUM);
		model.addAttribute("URL_DATENSCHUTZ", ApplicationPathHelper.URL_DATENSCHUTZ);
		model.addAttribute("URL_PRICEGAME", ApplicationPathHelper.URL_PRICEGAME);
		
		LOG.debug("Preparing model for feedback start area");
		model.addAttribute("URL_FEEDBACK_START", ApplicationPathHelper.URL_FEEDBACK_START);
		model.addAttribute("URL_FEEDBACK_QUESTION_SUBMIT", ApplicationPathHelper.URL_FEEDBACK_QUESTION_SUBMIT);
		model.addAttribute("URL_FEEDBACK_RESULT_SUBMIT", ApplicationPathHelper.URL_FEEDBACK_RESULT_SUBMIT);
		
		LOG.debug("Preparing model for anonymous area");
		model.addAttribute("URL_LOGIN_FORM", ApplicationPathHelper.URL_LOGIN_FORM);
		model.addAttribute("URL_LOGIN", ApplicationPathHelper.URL_LOGIN);
		
		LOG.debug("Preparing model for administration area");
		model.addAttribute("URL_ADMIN_PROJECTDETAILS", ApplicationPathHelper.URL_ADMIN_PROJECTDETAILS);
		model.addAttribute("URL_ADMIN_PROJECTS", ApplicationPathHelper.URL_ADMIN_PROJECTS);
		model.addAttribute("URL_ADMIN_SHOWFEEDBACK", ApplicationPathHelper.URL_ADMIN_SHOWFEEDBACK); //TODO notwendig?
		model.addAttribute("URL_ADMIN_EDITUITEXT", ApplicationPathHelper.URL_ADMIN_EDITUITEXT); //TODO notwendig?
		model.addAttribute("URL_ADMIN_SHOWUSERAGENTS", ApplicationPathHelper.URL_ADMIN_SHOWUSERAGENTS); //TODO notwendig?
		
		model.addAttribute("TIMENOW", ZonedDateTimeUtility.nice(ZonedDateTimeUtility.nowCET()));		
		
		/*
		 * URLs for DEV profile
		 */
		List<String> profiles = Arrays.asList(environment.getActiveProfiles());

		if (profiles.contains(ApplicationProfileHelper.DEV_PROFILE)) {
			LOG.debug("Preparing model for DEV profile.");
			model.addAttribute("isDevProfile", true);
		}
		
		if (profiles.contains(ApplicationProfileHelper.REV_PROFILE)) {
			LOG.debug("Preparing model for REV profile.");
			model.addAttribute("isRevProfile", true);
		}
	}

	public void fillResults(Model model, Project project, List<Participant> randomParticipantList,
			List<Participant> participantList, List<RatingQuestion> rqListMale,
			List<RatingQuestion> rqListFemale, List<ParticipationResult> prList,
			Map<RatingObject, BigDecimal> maleResultMap, Map<RatingObject, BigDecimal> femaleResultMap, Map<RatingObject, BigDecimal> overallResultMap
			) {
		
		model.addAttribute("project", project);
		
		model.addAttribute("randomParticipantList", randomParticipantList);
		model.addAttribute("participantList", participantList);
		model.addAttribute("rqListMale", rqListMale);
		model.addAttribute("rqListFemale", rqListFemale);
		model.addAttribute("prList", prList);
		
		model.addAttribute("maleResultMap", maleResultMap);
		model.addAttribute("femaleResultMap", femaleResultMap);
		model.addAttribute("overallResultMap", overallResultMap);
		
		model.addAttribute("URL_ADMIN_CSVPARTICIPANTS", ApplicationPathHelper.URL_ADMIN_CSVPARTICIPANTS);
		model.addAttribute("URL_ADMIN_CSVFEEDBACK", ApplicationPathHelper.URL_ADMIN_CSVFEEDBACK);
		model.addAttribute("URL_ADMIN_CSVFEEDBACK_FREETEXT", ApplicationPathHelper.URL_ADMIN_CSVFEEDBACK_FREETEXT);
		
		model.addAttribute("URL_ADMIN_EDITPARTICIPANT", ApplicationPathHelper.URL_ADMIN_EDITPARTICIPANT);
		//TODO use with modal
		model.addAttribute("URL_ADMIN_DELETEPARTICIPANT", ApplicationPathHelper.URL_ADMIN_DELETEPARTICIPANT);
	}

	public void fillError(Model model, int statusCode, String errorTitle, String errorMessage, Exception errorException, String errorTrace, boolean printTrace) {
		
		LOG.debug("Preparing model for error area");

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

	public void fillUiText(Model model, UiText uiText) {

		model.addAttribute(uiText.getUiTextKey().toString(), uiText.getText());
	}

	/**
	 * Fills Model for Project Details page
	 * @param model Model to fill
	 * @param urlSubmit URL which shall form be submitted to
	 * @param buttonText Text to display on Button
	 */
	public void fillProjectDetails(Model model, Project project) {
		model.addAttribute("project", project);
		model.addAttribute("URL_ADMIN_PROJECTDETAILS", ApplicationPathHelper.URL_ADMIN_PROJECTDETAILS);
	}
}
