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

import eu.planlos.pcfeedback.constants.ApplicationPath;
import eu.planlos.pcfeedback.constants.SessionAttribute;
import eu.planlos.pcfeedback.exceptions.InvalidFeedbackException;
import eu.planlos.pcfeedback.exceptions.ParticipantAlreadyExistingException;
import eu.planlos.pcfeedback.exceptions.RatingQuestionsNotExistentException;
import eu.planlos.pcfeedback.model.FeedbackContainer;
import eu.planlos.pcfeedback.model.Gender;
import eu.planlos.pcfeedback.model.Participant;
import eu.planlos.pcfeedback.model.ParticipationResult;
import eu.planlos.pcfeedback.model.RatingQuestion;
import eu.planlos.pcfeedback.model.UiTextKey;
import eu.planlos.pcfeedback.service.ModelFillerService;
import eu.planlos.pcfeedback.service.ParticipantService;
import eu.planlos.pcfeedback.service.ParticipationResultService;
import eu.planlos.pcfeedback.service.RatingQuestionService;
import eu.planlos.pcfeedback.service.UserAgentService;

@Controller
public class FeedbackController {

	private static final Logger LOG = LoggerFactory.getLogger(FeedbackController.class);
	
	@Autowired
	private ModelFillerService mfs;
	
	@Autowired
	private RatingQuestionService ratingQuestionService;
	
	@Autowired
	private ParticipantService participantService;
	
	@Autowired
	private UserAgentService userAgentService;
	
	@Autowired
	private ParticipationResultService participationResultService;
	
	@RequestMapping(path = ApplicationPath.URL_FEEDBACK)
	public String feedback(Model model, HttpSession session) {
		
		Participant participant = (Participant) session.getAttribute(SessionAttribute.PARTICIPANT);
		
		if(participant == null) {
			LOG.debug("User tried to access feedback without entering participant details");
			return "redirect:" + ApplicationPath.URL_FEEDBACK_START;
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

		mfs.fillUiText(model, UiTextKey.MSG_FEEDBACKQUESTION);
		mfs.fillGlobal(model);
		return ApplicationPath.RES_FEEDBACK;
	}
	
	@RequestMapping(path = ApplicationPath.URL_FEEDBACK_SUBMIT, method = RequestMethod.POST)
	public String feedbackSubmit(@RequestHeader("User-Agent") String userAgentText, @ModelAttribute FeedbackContainer fbc, HttpSession session, Model model) throws NoParticipantException {
		
		Participant participant = (Participant) session.getAttribute(SessionAttribute.PARTICIPANT);
		Map<Long, Integer> feedbackMap = fbc.getFeedbackMap();
		
		String ressource = "redirect:" + ApplicationPath.URL_FEEDBACK_END;
		
		try {

			if(participant == null) {
				throw new NoParticipantException();
			}
			
			//Save participant first, might not complete
			participantService.save(participant);
			ratingQuestionService.saveFeedback(feedbackMap);
						
			//Save the result for later plausibilisation/correction
			ParticipationResult pr = new ParticipationResult(participant, feedbackMap);
			participationResultService.saveParticipationResult(pr);
			
			//Save user agent for later analysis
			userAgentService.saveUserAgent(userAgentText, participant.getGender());
			
		} catch (ParticipantAlreadyExistingException e) {
			LOG.error("This should not happen, because session is destroyed on submitting feedback");
		} catch (NoParticipantException e) {
			LOG.error("No participant in session available");
		} catch (InvalidFeedbackException e) {
			LOG.error("Something with the given feedback went wrong");
			
			List<RatingQuestion> ratingQuestionList = new ArrayList<>();
			try {
				
				ratingQuestionList.addAll(ratingQuestionService.reloadForInvalidFeedback(participant.getGender(), feedbackMap));
			
				model.addAttribute("feedbackError", e.getMessage());
				model.addAttribute("ratingQuestionList", ratingQuestionList);
				model.addAttribute("chosenList", feedbackMap);
				
				mfs.fillUiText(model, UiTextKey.MSG_FEEDBACKQUESTION);
				mfs.fillGlobal(model);
				
				return ApplicationPath.RES_FEEDBACK;
				
			} catch (RatingQuestionsNotExistentException f) {
				f.printStackTrace();
			}
			
		} finally {
			ressource = "feedback_error";
		}
		
		return ressource;
	}
}
