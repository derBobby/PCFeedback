package eu.planlos.pcfeedback.controller.feedback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import eu.planlos.pcfeedback.constants.ApplicationPathHelper;
import eu.planlos.pcfeedback.constants.SessionAttributeHelper;
import eu.planlos.pcfeedback.exceptions.InvalidFeedbackException;
import eu.planlos.pcfeedback.exceptions.NoFeedbackException;
import eu.planlos.pcfeedback.exceptions.ParticipantAlreadyExistingException;
import eu.planlos.pcfeedback.exceptions.RatingQuestionsNotExistentException;
import eu.planlos.pcfeedback.model.FeedbackContainer;
import eu.planlos.pcfeedback.model.Gender;
import eu.planlos.pcfeedback.model.UiTextKey;
import eu.planlos.pcfeedback.model.db.Participant;
import eu.planlos.pcfeedback.model.db.ParticipationResult;
import eu.planlos.pcfeedback.model.db.Project;
import eu.planlos.pcfeedback.model.db.RatingQuestion;
import eu.planlos.pcfeedback.service.FeedbackValidationService;
import eu.planlos.pcfeedback.service.FreeTextService;
import eu.planlos.pcfeedback.service.ModelFillerService;
import eu.planlos.pcfeedback.service.ParticipantService;
import eu.planlos.pcfeedback.service.ParticipationResultService;
import eu.planlos.pcfeedback.service.RatingQuestionService;
import eu.planlos.pcfeedback.service.UserAgentService;

@Controller
@SessionAttributes(names = {SessionAttributeHelper.PARTICIPANT, SessionAttributeHelper.PROJECT, SessionAttributeHelper.FEEDBACK})
public class FeedbackController {

	private static final Logger LOG = LoggerFactory.getLogger(FeedbackController.class);
	
	private static final String ERROR_TEMPLATE = "feedback_error";
	
	private static final int FREETEXTMAXLENGTH = 2000;
	
	@Autowired
	private ModelFillerService mfs;
	
	@Autowired
	private RatingQuestionService ratingQuestionService;
	
	@Autowired
	private ParticipantService participantService;

	@Autowired
	private UserAgentService userAgentService;

	@Autowired
	private FreeTextService freeTextService;
	
	@Autowired
	private FeedbackValidationService validationService;
	
	@Autowired
	private ParticipationResultService participationResultService;

	@Autowired
	private RatingQuestionService rqs;
	
	/**
	 * User is redirected to this controller after successfully writing participant info to session.
	 * 
	 * @param model
	 * @param project
	 * @param participant
	 * @param session
	 * @return template to load
	 */
	@RequestMapping(path = ApplicationPathHelper.URL_FEEDBACK_QUESTION)
	public String feedback(Model model,
			@ModelAttribute(SessionAttributeHelper.PROJECT) Project project,
			@ModelAttribute(SessionAttributeHelper.PARTICIPANT) Participant participant) {
		
		
		//TODO ParticipantFilter?
		if(participant == null) {
			LOG.debug("User tried to access feedback without entering participant details");
			return "redirect:" + ApplicationPathHelper.URL_FEEDBACK_START;
		}
		
		LOG.debug("Participant: {}", participant.toString());
		
		Gender gender = participant.getGender();
		List<RatingQuestion> ratingQuestionList = new ArrayList<>();		
		try {
			ratingQuestionService.addRatingQuestionsForProjectAndGenderToList(ratingQuestionList, project, gender);
			
		} catch (RatingQuestionsNotExistentException e) {
			LOG.error("Fataler Fehler: Konnte Liste von RatingQuestion nicht laden.");
			LOG.error("project={}, gender={}", project.getProjectName(), gender.name());
			e.printStackTrace();
			e.getCause();
			//TODO What happens here?
		} 
				
		model.addAttribute("ratingQuestionList", ratingQuestionList);

		mfs.fillUiText(model, project, UiTextKey.MSG_FEEDBACK_QUESTION);
		mfs.fillGlobal(model);
		return ApplicationPathHelper.RES_FEEDBACK_QUESTION;
	}
	
	/**
	 * 
	 * @param model
	 * @param session
	 * @param fbc
	 * @param project
	 * @param participant
	 * @return
	 */
	@RequestMapping(path = ApplicationPathHelper.URL_FEEDBACK_SUBMIT, method = RequestMethod.POST)
	public String feedbackSubmit(Model model,
			HttpSession session,
			@ModelAttribute FeedbackContainer fbc,
			@ModelAttribute(SessionAttributeHelper.PROJECT) Project project,
			@ModelAttribute(SessionAttributeHelper.PARTICIPANT) Participant participant) {

		String resource = ApplicationPathHelper.RES_FEEDBACK_FREETEXT;
		
		Map<Long, Integer> feedbackMap = fbc.getFeedbackMap();
		
		try {
			validationService.isValidFeedback(project, feedbackMap);
			LOG.debug("Adding feedback to session");
			session.setAttribute(SessionAttributeHelper.FEEDBACK, fbc);
			
		} catch (NoFeedbackException | InvalidFeedbackException e) {

			try {
				
				List<RatingQuestion> ratingQuestionList = new ArrayList<>();
				ratingQuestionList.addAll(ratingQuestionService.reloadForInvalidFeedback(project, participant.getGender(), feedbackMap));
				model.addAttribute("ratingQuestionList", ratingQuestionList);
			
				model.addAttribute("feedbackError", e.getMessage());
				model.addAttribute("chosenList", feedbackMap);
				
				mfs.fillUiText(model, project, UiTextKey.MSG_FEEDBACK_QUESTION);
				
				resource = ApplicationPathHelper.RES_FEEDBACK_QUESTION;
				
			} catch (RatingQuestionsNotExistentException f) {
				f.printStackTrace();
				resource = ERROR_TEMPLATE;
			}
			
		}
		model.addAttribute("freeTextMaxLength", FREETEXTMAXLENGTH);
		mfs.fillUiText(model, project, UiTextKey.MSG_FEEDBACK_FREETEXT);
		mfs.fillGlobal(model);		
		return resource;
	}
	
	/**
	 * Method which saves all results. Takes participant and feedback from session
	 * @param model
	 * @param userAgentText
	 * @param freeText
	 * @param project
	 * @param participant
	 * @param fbContainer
	 * @return
	 */
	@RequestMapping(path = ApplicationPathHelper.URL_FEEDBACK_FREETEXT_SUBMIT, method = RequestMethod.POST)
	public String freeTextSubmit(Model model,
			@RequestHeader("User-Agent") String userAgentText,
			@RequestParam String freeText,
			@ModelAttribute(SessionAttributeHelper.PROJECT) Project project,
			@ModelAttribute(SessionAttributeHelper.PARTICIPANT) Participant participant,
			@ModelAttribute(SessionAttributeHelper.FEEDBACK) FeedbackContainer fbContainer) {

		Map<Long, Integer> feedbackMap = fbContainer.getFeedbackMap();
				
		String resource = "redirect:" + ApplicationPathHelper.URL_FEEDBACK_END;
		
		try {
			
			finalValidation(project, participant, feedbackMap);
			
			//Save participant first, might not complete
			participantService.save(participant);
			ratingQuestionService.saveFeedback(feedbackMap);
			freeTextService.createAndSaveFreeText(project, freeText, participant.getGender());			
			
			//Save the result for later plausibilisation/correction
			ParticipationResult pr = new ParticipationResult(project, participant, feedbackMap);
			participationResultService.saveParticipationResult(pr);
			
			//Save user agent for later analysis
			userAgentService.saveUserAgent(project, userAgentText, participant.getGender());
			
		} catch (ParticipantAlreadyExistingException e) {
			LOG.error("This should not happen, because session is destroyed on submitting feedback");
			resource = ERROR_TEMPLATE;
			
		} catch (InvalidFeedbackException e) {
			LOG.error("Project is not in all objects the same: project, participant, etc.");
			resource = ERROR_TEMPLATE;
			
		} finally {
			if(resource.equals(ERROR_TEMPLATE)) {
				mfs.fillGlobal(model);
			}
		}
		
		return resource;
	}

	private void finalValidation(Project project, Participant participant, Map<Long, Integer> feedbackMap) throws InvalidFeedbackException {

		long project1 = project.getIdProject();
		long project2 = participant.getProject().getIdProject();
		
		if(project1 != project2) {
			throw new InvalidFeedbackException("Projekt in folgenden Objekten nicht identisch: Projekt, Teilnehmer.");
		}

		long idRatingQuestion = feedbackMap.keySet().iterator().next();
		RatingQuestion ratingQuestion = rqs.findByIdRatingQuestion(idRatingQuestion);
		
		long project3 = ratingQuestion.getProject().getIdProject();
		
		if(project1 != project3) {
			throw new InvalidFeedbackException("Projekt in folgenden Objekten nicht identisch: Projekt, Frage.");
		}		
	}
}
