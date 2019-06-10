package eu.planlos.pcfeedback.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
		
		FeedbackContainer fbc = new FeedbackContainer(ratingQuestionList);
		
		mfs.fillFeedback(model, fbc);
		mfs.fillGlobal(model);
		
		return ApplicationPath.RES_FEEDBACK;
	}
	
	@RequestMapping(path = ApplicationPath.URL_FEEDBACK, method = RequestMethod.POST)
	public String feedbackSubmit(@Valid @ModelAttribute FeedbackContainer fbc, BindingResult bindingResult, HttpSession session, Model model) {
		
		//TODO ??
		if(bindingResult.hasErrors()) {
			System.out.println("### FAIL ###");
		}
		
		Participant participant = (Participant) session.getAttribute(SessionAttribute.PARTICIPANT);
		List<RatingQuestion> rqList = fbc.getRatingQuestionList();
		
		try {
			
			checkIfRatingQuestionsAreValid(rqList);
			ratingQuestionService.saveFeedback(rqList);
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

	private void checkIfRatingQuestionsAreValid(List<RatingQuestion> rqList) throws InvalidFeedbackException {
		
		for(RatingQuestion rQ : rqList) {
			if( (rQ.getObjectOne() == null && rQ.getObjectTwo() == null)
				|| (rQ.getObjectOne() != null && rQ.getObjectTwo() != null) ) {
				
				logger.error("feedback is invalid, none or more than one rating object has a vote");
				throw new InvalidFeedbackException();
			}
			
			logger.debug("Feedback is valid, one rating object has a vote");
		}
		
	}
}
