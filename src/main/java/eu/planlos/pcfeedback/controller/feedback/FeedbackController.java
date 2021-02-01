package eu.planlos.pcfeedback.controller.feedback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
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
import eu.planlos.pcfeedback.model.db.UiText;
import eu.planlos.pcfeedback.service.FeedbackValidationService;
import eu.planlos.pcfeedback.service.MailService;
import eu.planlos.pcfeedback.service.ModelFillerService;
import eu.planlos.pcfeedback.service.ParticipantService;
import eu.planlos.pcfeedback.service.ParticipationResultService;
import eu.planlos.pcfeedback.service.RatingQuestionService;
import eu.planlos.pcfeedback.service.UiTextService;

@Controller
@SessionAttributes(names = {SessionAttributeHelper.PARTICIPANT, SessionAttributeHelper.PROJECT, SessionAttributeHelper.FEEDBACK})
public class FeedbackController {

	private static final Logger LOG = LoggerFactory.getLogger(FeedbackController.class);
	
	private static final String ERROR_TEMPLATE = "feedback_error";
	
	private static final int FREETEXTMAXLENGTH = 2000;

	private ModelFillerService mfs;
	private RatingQuestionService ratingQuestionService;
	private ParticipantService participantService;
	private FeedbackValidationService validationService;
	private ParticipationResultService participationResultService;
	private MailService mailService;
	private UiTextService uts;
	
	@Autowired
	public FeedbackController(ModelFillerService mfs, RatingQuestionService	ratingQuestionService,
			ParticipantService participantService, FeedbackValidationService validationService,
			ParticipationResultService participationResultService, MailService mailService,
			UiTextService uts) {
		this.mfs = mfs;
		this.ratingQuestionService = ratingQuestionService;
		this.participantService = participantService;
		this.validationService = validationService;
		this.participationResultService = participationResultService;
		this.mailService = mailService;
		this.uts = uts;
	}
	
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
			LOG.error("FATAL - COULD NOT LOAD LIST OF MATCHING RatingQuestion. This should never happen.");
			LOG.error("FATAL - project={}, gender={}", project.getProjectName(), gender.name());
		} 
				
		model.addAttribute("ratingQuestionList", ratingQuestionList);

		UiText uiText = uts.getUiText(project, UiTextKey.MSG_FEEDBACK_QUESTION);
		
		mfs.fillUiText(model, uiText);
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
	 * @throws IOException 
	 */
	@RequestMapping(path = ApplicationPathHelper.URL_FEEDBACK_QUESTION_SUBMIT, method = RequestMethod.POST)
	public String questionSubmit(Model model,
			ServletResponse response,
			HttpSession session,
			@ModelAttribute FeedbackContainer fbc,
			@ModelAttribute(SessionAttributeHelper.PROJECT) Project project,
			@ModelAttribute(SessionAttributeHelper.PARTICIPANT) Participant participant) throws IOException {

		String resource = ApplicationPathHelper.RES_FEEDBACK_FREETEXT;
		
		Map<Long, Integer> feedbackMap = fbc.getFeedbackMap();
		
		try {
			validationService.isValidFeedback(project, feedbackMap);
			LOG.debug("Adding feedback to session");
			session.setAttribute(SessionAttributeHelper.FEEDBACK, fbc);
						
			if(! project.getAskFreetext()) {
				HttpServletResponse res = (HttpServletResponse) response;
				res.sendRedirect(ApplicationPathHelper.URL_FEEDBACK_RESULT_SUBMIT);
				return null;
			}
					
		} catch (NoFeedbackException | InvalidFeedbackException e) {

			try {
				
				List<RatingQuestion> ratingQuestionList = new ArrayList<>();
				ratingQuestionList.addAll(ratingQuestionService.reloadForInvalidFeedback(project, participant.getGender(), feedbackMap));
				model.addAttribute("ratingQuestionList", ratingQuestionList);
			
				model.addAttribute("feedbackError", e.getMessage());
				model.addAttribute("chosenList", feedbackMap);
				
				UiText uiText = uts.getUiText(project, UiTextKey.MSG_FEEDBACK_QUESTION);
				mfs.fillUiText(model, uiText);
				
				resource = ApplicationPathHelper.RES_FEEDBACK_QUESTION;
				
			} catch (RatingQuestionsNotExistentException | InvalidFeedbackException f) {
				LOG.debug("{}", f.toString());
				resource = ERROR_TEMPLATE;
			}
			
		}
		model.addAttribute("freeTextMaxLength", FREETEXTMAXLENGTH);
		UiText uiText = uts.getUiText(project, UiTextKey.MSG_FEEDBACK_FREETEXT);
		mfs.fillUiText(model, uiText);
		mfs.fillGlobal(model);		
		return resource;
	}
	
	/**
	 * Method which saves all results. Takes participant and feedback from session
	 * @param model
	 * @param userAgent
	 * @param project
	 * @param participant
	 * @param fbContainer
	 * @param freeText
	 * @return
	 */
	@RequestMapping(path = ApplicationPathHelper.URL_FEEDBACK_RESULT_SUBMIT, method = RequestMethod.POST)
	public String resultSubmit(Model model,
			@RequestHeader("User-Agent") String userAgent,
			@ModelAttribute(SessionAttributeHelper.PROJECT) Project project,
			@ModelAttribute(SessionAttributeHelper.PARTICIPANT) Participant participant,
			@ModelAttribute(SessionAttributeHelper.FEEDBACK) FeedbackContainer fbContainer,
			@RequestParam String freeText) {
		
		participant.setUserAgent(userAgent);
		
		return processEndResult(model, freeText, project, participant, fbContainer);
	}
	
	/**
	 * Method which saves all results. Takes participant and feedback from session
	 * @param model
	 * @param userAgent
	 * @param project
	 * @param participant
	 * @param fbContainer
	 * @return
	 */
	@RequestMapping(path = ApplicationPathHelper.URL_FEEDBACK_RESULT_SUBMIT, method = RequestMethod.GET)
	public String resultSubmit(Model model,
			@RequestHeader("User-Agent") String userAgent,
			@ModelAttribute(SessionAttributeHelper.PROJECT) Project project,
			@ModelAttribute(SessionAttributeHelper.PARTICIPANT) Participant participant,
			@ModelAttribute(SessionAttributeHelper.FEEDBACK) FeedbackContainer fbContainer) {
		
		participant.setUserAgent(userAgent);
		
		return processEndResult(model, null, project, participant, fbContainer);
	}

	private String processEndResult(Model model, String freeText, Project project,
			Participant participant, FeedbackContainer fbContainer) {
		Map<Long, Integer> feedbackMap = fbContainer.getFeedbackMap();
				
		String resource = "redirect:" + ApplicationPathHelper.URL_FEEDBACK_END;
		
		try {
			
			finalValidation(project, participant, feedbackMap);
			
			//Save participant first, might not complete
			participantService.save(participant);
			ratingQuestionService.saveFeedback(feedbackMap);
			
			//Save the result for later plausibilisation/correction
			ParticipationResult participationResult = new ParticipationResult(project, participant, feedbackMap, freeText);
			participationResultService.saveParticipationResult(participationResult);
			
			//Send notification mail
			String recepient = project.getNotificationMail();
			if(recepient != null && ! recepient.equals("")) {
				LOG.debug("Sending notification mail");
				mailService.notifyParticipation(project);
			} else {
				LOG.debug("Not sending notification mail");
			}
			
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

		int mapEntryCount = feedbackMap.size();
		
		if(mapEntryCount < project.getRatingQuestionCount()) {
			throw new InvalidFeedbackException("Im Feedback ist nicht die erwartete Anzahl Paare enthalten.");
		}
		
		long project1 = project.getIdProject();
		long project2 = participant.getProject().getIdProject();
		
		if(project1 != project2) {
			throw new InvalidFeedbackException("Projekt in folgenden Objekten nicht identisch: Projekt, Teilnehmer.");
		}

		if(mapEntryCount == 0) {
			LOG.debug("No rating questions have been submitted");
			return;
		}
		
		long idRatingQuestion = feedbackMap.keySet().iterator().next();
		RatingQuestion ratingQuestion = ratingQuestionService.findByIdRatingQuestion(idRatingQuestion);
		
		long project3 = ratingQuestion.getProject().getIdProject();
		
		if(project1 != project3) {
			throw new InvalidFeedbackException("Projekt in folgenden Objekten nicht identisch: Projekt, Frage.");
		}		
	}
}
