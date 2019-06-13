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
	public String feedbackSubmit(@ModelAttribute FeedbackContainer fbc, HttpSession session) {
		
		Participant participant = (Participant) session.getAttribute(SessionAttribute.PARTICIPANT);
		Map<Long, Integer> feedbackMap = fbc.getFeedbackMap();
		
		try {
			//Throws exception because it is already catched from the other save operations
			ratingQuestionService.saveFeedback(feedbackMap);
			participantService.save(participant);
			
		} catch (ParticipantAlreadyExistsException e) {
			
			logger.error("Participant has been created by another user while this one did the feedback");
			// TODO Load site???
			//mfs.fillGlobal(model);
			e.printStackTrace();
			
		} catch (InvalidFeedbackException e) {
			
			logger.error("Something with the given feedback went wrong");
			// TODO Load site???
			//mfs.fillGlobal(model);
			e.printStackTrace();
		}

		session.invalidate();
		
		return "redirect:" + ApplicationPath.URL_FEEDBACK_END;
	}
}
