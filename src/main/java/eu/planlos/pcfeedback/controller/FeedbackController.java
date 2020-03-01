package eu.planlos.pcfeedback.controller;

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

import eu.planlos.pcfeedback.constants.ApplicationPathHelper;
import eu.planlos.pcfeedback.constants.SessionAttributeHelper;
import eu.planlos.pcfeedback.exceptions.InvalidFeedbackException;
import eu.planlos.pcfeedback.exceptions.NoFeedbackException;
import eu.planlos.pcfeedback.exceptions.ParticipantAlreadyExistingException;
import eu.planlos.pcfeedback.exceptions.RatingQuestionsNotExistentException;
import eu.planlos.pcfeedback.model.FeedbackContainer;
import eu.planlos.pcfeedback.model.Gender;
import eu.planlos.pcfeedback.model.Participant;
import eu.planlos.pcfeedback.model.ParticipationResult;
import eu.planlos.pcfeedback.model.RatingQuestion;
import eu.planlos.pcfeedback.model.UiTextKey;
import eu.planlos.pcfeedback.service.FeedbackValidationService;
import eu.planlos.pcfeedback.service.FreeTextService;
import eu.planlos.pcfeedback.service.ModelFillerService;
import eu.planlos.pcfeedback.service.ParticipantService;
import eu.planlos.pcfeedback.service.ParticipationResultService;
import eu.planlos.pcfeedback.service.RatingQuestionService;
import eu.planlos.pcfeedback.service.UserAgentService;

@Controller
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
	
	
	//TODO describe what it does
	/**
	 * User is redirected to this controller after successfully writing participant info to session 
	 * @param model
	 * @param session provides participant details
	 * @return template to load
	 */
	@RequestMapping(path = ApplicationPathHelper.URL_FEEDBACK_QUESTION)
	public String feedback(Model model, HttpSession session) {
		
		Participant participant = (Participant) session.getAttribute(SessionAttributeHelper.PARTICIPANT);
		
		if(participant == null) {
			LOG.debug("User tried to access feedback without entering participant details");
			return "redirect:" + ApplicationPathHelper.URL_FEEDBACK_START;
		}
		
		LOG.debug("Participant: {}", participant.toString());
		
		List<RatingQuestion> ratingQuestionList = new ArrayList<>();
		Gender gender = participant.getGender();
		
		try {
			ratingQuestionService.addRatingQuestionsForGenderToList(ratingQuestionList, gender);
			
		} catch (RatingQuestionsNotExistentException e) {
			//TODO Can this happen?
			e.printStackTrace();
		} 
				
		model.addAttribute("ratingQuestionList", ratingQuestionList);

		mfs.fillUiText(model, UiTextKey.MSG_FEEDBACK_QUESTION);
		mfs.fillGlobal(model);
		return ApplicationPathHelper.RES_FEEDBACK_QUESTION;
	}
	
	@RequestMapping(path = ApplicationPathHelper.URL_FEEDBACK_SUBMIT, method = RequestMethod.POST)
	public String feedbackSubmit(@ModelAttribute FeedbackContainer fbc, HttpSession session, Model model) {

		String resource = ApplicationPathHelper.RES_FEEDBACK_FREETEXT;
		
		Map<Long, Integer> feedbackMap = fbc.getFeedbackMap();
		Participant participant = (Participant) session.getAttribute(SessionAttributeHelper.PARTICIPANT);
		
		try {
			validationService.isValidFeedback(feedbackMap);
			LOG.debug("Adding feedback to session");
			session.setAttribute(SessionAttributeHelper.FEEDBACK, fbc);
			
		} catch (NoFeedbackException | InvalidFeedbackException e) {

			try {
				
				List<RatingQuestion> ratingQuestionList = new ArrayList<>();
				ratingQuestionList.addAll(ratingQuestionService.reloadForInvalidFeedback(participant.getGender(), feedbackMap));
				model.addAttribute("ratingQuestionList", ratingQuestionList);
			
				model.addAttribute("feedbackError", e.getMessage());
				model.addAttribute("chosenList", feedbackMap);
				
				mfs.fillUiText(model, UiTextKey.MSG_FEEDBACK_QUESTION);
				
				resource = ApplicationPathHelper.RES_FEEDBACK_QUESTION;
				
			} catch (RatingQuestionsNotExistentException f) {
				f.printStackTrace();
				resource = ERROR_TEMPLATE;
			}
			
		}
		model.addAttribute("freeTextMaxLength", FREETEXTMAXLENGTH);
		mfs.fillUiText(model, UiTextKey.MSG_FEEDBACK_FREETEXT);
		mfs.fillGlobal(model);		
		return resource;
	}
	
	/**
	 * Method which saves all results. Takes participant and feedback from session
	 * @param userAgentText Is automatically read from http header. Used to store Browser statistics 
	 * @param fbc Feedback container provided by form
	 * @param session stores participant
	 * @param model
	 * @return template to load
	 * @throws NoParticipantException
	 */
	@RequestMapping(path = ApplicationPathHelper.URL_FEEDBACK_FREETEXT_SUBMIT, method = RequestMethod.POST)
	public String freeTextSubmit(@RequestHeader("User-Agent") String userAgentText, @RequestParam String freeText,  HttpSession session, Model model) {
		
		Participant participant = (Participant) session.getAttribute(SessionAttributeHelper.PARTICIPANT);
		FeedbackContainer fbContainer = (FeedbackContainer) session.getAttribute(SessionAttributeHelper.FEEDBACK);
		Map<Long, Integer> feedbackMap = fbContainer.getFeedbackMap();
				
		String resource = "redirect:" + ApplicationPathHelper.URL_FEEDBACK_END;
		
		try {
			
			//Save participant first, might not complete
			participantService.save(participant);
			ratingQuestionService.saveFeedback(feedbackMap);
			freeTextService.saveFreeText(freeText, participant.getGender());			
			
			//Save the result for later plausibilisation/correction
			ParticipationResult pr = new ParticipationResult(participant, feedbackMap);
			participationResultService.saveParticipationResult(pr);
			
			//Save user agent for later analysis
			userAgentService.saveUserAgent(userAgentText, participant.getGender());
			
		} catch (ParticipantAlreadyExistingException e) {
			LOG.error("This should not happen, because session is destroyed on submitting feedback");
			resource = ERROR_TEMPLATE;
			
		} finally {
			if(resource.equals(ERROR_TEMPLATE)) {
				mfs.fillGlobal(model);
			}
		}
		
		return resource;
	}
}
