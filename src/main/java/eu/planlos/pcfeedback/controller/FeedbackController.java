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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.planlos.pcfeedback.constants.ApplicationPath;
import eu.planlos.pcfeedback.constants.SessionAttribute;
import eu.planlos.pcfeedback.exceptions.InvalidFeedbackException;
import eu.planlos.pcfeedback.exceptions.ParticipantHasAlreadyCompletedFeedbackException;
import eu.planlos.pcfeedback.exceptions.RatingQuestionsNotExistentException;
import eu.planlos.pcfeedback.model.FeedbackContainer;
import eu.planlos.pcfeedback.model.Gender;
import eu.planlos.pcfeedback.model.Participant;
import eu.planlos.pcfeedback.model.RatingQuestion;
import eu.planlos.pcfeedback.service.ModelFillerService;
import eu.planlos.pcfeedback.service.ParticipantService;
import eu.planlos.pcfeedback.service.RatingQuestionService;

@Controller
public class FeedbackController {

	private static final Logger logger = LoggerFactory.getLogger(FeedbackController.class);
	
	@Autowired
	private ModelFillerService mfs;
	
	@Autowired
	private RatingQuestionService ratingQuestionService;
	
	@Autowired
	private ParticipantService participantService;
	
	@RequestMapping(path = ApplicationPath.URL_FEEDBACK)
	public String feedback(Model model, HttpSession session) {
		
		Participant participant = (Participant) session.getAttribute(SessionAttribute.PARTICIPANT);
		
		if(participant == null) {
			logger.debug("User tried to access feedback without entering participant details");
			return "redirect:" + ApplicationPath.URL_FEEDBACK_START;
		}
		
		Gender gender = participant.getGender();
		
		List<RatingQuestion> ratingQuestionList = new ArrayList<>();
		
		try {
			logger.debug("Participant has gender: " + gender.toString());
			ratingQuestionService.addRatingQuestionsForGenderToList(ratingQuestionList, gender);
			
		} catch (RatingQuestionsNotExistentException e) {
			//TODO Can this happen?
			e.printStackTrace();
		} 
				
		model.addAttribute("ratingQuestionList", ratingQuestionList);
		mfs.fillGlobal(model);
		
		return ApplicationPath.RES_FEEDBACK;
	}
	
	@RequestMapping(path = ApplicationPath.URL_FEEDBACK_SUBMIT, method = RequestMethod.POST)
	public String feedbackSubmit(@ModelAttribute FeedbackContainer fbc, HttpSession session, Model model) {
		
		Participant participant = (Participant) session.getAttribute(SessionAttribute.PARTICIPANT);
		Map<Long, Integer> feedbackMap = fbc.getFeedbackMap();
		
		try {
			//Throws exception because it is already catched from the other save operations
			ratingQuestionService.saveFeedback(feedbackMap);
			participantService.completeFeedback(participant);
			
		} catch (InvalidFeedbackException e) {
			
			logger.error("Something with the given feedback went wrong");
			
			List<RatingQuestion> ratingQuestionList = new ArrayList<>();
			try {
				
				ratingQuestionList.addAll(ratingQuestionService.reloadForInvalidFeedback(participant.getGender(), feedbackMap));
				
			} catch (RatingQuestionsNotExistentException f) {
				//TODO Can this happen?
				f.printStackTrace();
			}
			
			model.addAttribute("feedbackError", e.getMessage());
			model.addAttribute("ratingQuestionList", ratingQuestionList);
			model.addAttribute("chosenList", feedbackMap);
			mfs.fillGlobal(model);
			
			return ApplicationPath.RES_FEEDBACK;
		} catch (ParticipantHasAlreadyCompletedFeedbackException e) {
			// TODO One guy tries twice to submit?
			e.printStackTrace();
		}
		
		return "redirect:" + ApplicationPath.URL_FEEDBACK_END;
	}
}
