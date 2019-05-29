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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import eu.planlos.pcfeedback.constants.ApplicationPath;
import eu.planlos.pcfeedback.constants.SessionAttribute;
import eu.planlos.pcfeedback.exceptions.RatingQuestionsNotExistentException;
import eu.planlos.pcfeedback.model.Gender;
import eu.planlos.pcfeedback.model.Participant;
import eu.planlos.pcfeedback.model.RatingQuestion;
import eu.planlos.pcfeedback.service.RatingQuestionService;

@Controller
public class FeedbackController {

	private static final Logger logger = LoggerFactory.getLogger(FeedbackController.class);
	
	@Autowired
	private ModelFillerService bfs;
	
	@Autowired
	private RatingQuestionService ratingQuestionService;
	
	@GetMapping(path = ApplicationPath.URL_FEEDBACK)
	public String feedback(HttpSession session, Model model) {
		
		Participant participant = (Participant) session.getAttribute(SessionAttribute.PARTICIPANT);
		Gender gender = participant.getGender();
		
		List<RatingQuestion> ratingQuestions = new ArrayList<>();
		
		try {
			logger.debug("Participant has gender: " + gender.toString());
			ratingQuestions.addAll(ratingQuestionService.loadForGender(gender));
			
		} catch (RatingQuestionsNotExistentException e) {
			//TODO
			e.printStackTrace();
		} 
		
		bfs.fillFeedback(model, ratingQuestions);
		
		return ApplicationPath.RES_FEEDBACK;
	}
	
	@PostMapping(path = ApplicationPath.URL_FEEDBACK)
	public String feedbackSubmit(@Valid List<RatingQuestion> ratingQuestionList, BindingResult bindingResult, HttpSession session, Model model) {
		
		//TODO ??
		if(bindingResult.hasErrors()) {
			System.out.println("### FAIL ###");
		}
		
		ratingQuestionService.saveFeedback(ratingQuestionList);
		
		bfs.fillGlobal(model);
		bfs.fillEndFeedback(model);
		
		return ApplicationPath.RES_FEEDBACK_END;
	}
}
