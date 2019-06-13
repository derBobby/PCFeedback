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

import eu.planlos.pcfeedback.constants.ApplicationConfig;
import eu.planlos.pcfeedback.constants.ApplicationPath;
import eu.planlos.pcfeedback.constants.SessionAttribute;
import eu.planlos.pcfeedback.exceptions.ParticipantAlreadyExistsException;
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
		Gender gender = participant.getGender();
		
		List<RatingQuestion> ratingQuestionList = new ArrayList<>();
		
		try {
			logger.debug("Participant has gender: " + gender.toString());
			ratingQuestionList.addAll(ratingQuestionService.loadForFeedbackByGender(gender));
			
		} catch (RatingQuestionsNotExistentException e) {
			//TODO
			e.printStackTrace();
		} 
				
		model.addAttribute("ratingQuestionList", ratingQuestionList);
		mfs.fillGlobal(model);
		
		return ApplicationPath.RES_FEEDBACK;
	}
	
	@RequestMapping(path = ApplicationPath.URL_FEEDBACK, method = RequestMethod.POST)
	public String feedbackSubmit(@ModelAttribute FeedbackContainer fbc, HttpSession session, Model model) {
		
		Participant participant = (Participant) session.getAttribute(SessionAttribute.PARTICIPANT);
		Map<Long, Integer> feedbackMap = fbc.getFeedbackMap();
		
		try {
			//Throws exception because it is already catched from the other save operations
			checkIfRatingQuestionsAreValid(feedbackMap);
			ratingQuestionService.saveFeedback(feedbackMap);
			participantService.save(participant);
			
		} catch (ParticipantAlreadyExistsException e) {
			
			logger.error("Participant has been created by another user while this one did the feedback");
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} catch (InvalidFeedbackException e) {
			
			logger.error("Something with the given feedback went wrong");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		mfs.fillGlobal(model);
		
		return ApplicationPath.RES_FEEDBACK_END;
	}

	private void checkIfRatingQuestionsAreValid(Map<Long, Integer> feedbackMap) throws InvalidFeedbackException {
		
		if(feedbackMap.size() != ApplicationConfig.NEEDED_QUESTION_COUNT) {
			logger.error("Feedback HashMap does not contain needed amount of answered questions");
			throw new InvalidFeedbackException();
		}
		
		for(Long idRatingQuestion : feedbackMap.keySet()) {
			
			if(feedbackMap.get(idRatingQuestion) == null) {
				logger.error("Feedback HashMap is invalid");
				throw new InvalidFeedbackException();
			}
		}
		logger.debug("Feedback is valid");
// TODO clean up
//		for(RatingQuestion rQ : rqList) {
//			if( (rQ.getObjectOne() == null && rQ.getObjectTwo() == null)
//				|| (rQ.getObjectOne() != null && rQ.getObjectTwo() != null) ) {
//				
//				logger.error("Feedback is invalid, none or more than one rating object has a vote");
//				throw new InvalidFeedbackException();
//			}
//			
//			logger.debug("Rating question is valid, exactly one rating object has a vote");
//		}
//		
//		logger.debug("Feedback is valid");
	}
}
