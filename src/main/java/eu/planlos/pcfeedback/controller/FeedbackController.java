package eu.planlos.pcfeedback.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;

import eu.planlos.pcfeedback.constants.ApplicationPath;
import eu.planlos.pcfeedback.constants.SessionAttribute;
import eu.planlos.pcfeedback.exceptions.RatingQuestionsNotExistentException;
import eu.planlos.pcfeedback.model.Participant;
import eu.planlos.pcfeedback.model.RatingQuestion;
import eu.planlos.pcfeedback.service.RatingQuestionService;

@Controller
public class FeedbackController {

	@Autowired
	private ModelFillerService bfs;
	
	@Autowired
	private RatingQuestionService rqService;
	
	@GetMapping(path = ApplicationPath.URL_FEEDBACK)
	public String feedback(HttpSession session, Model model) {
		
		Participant participant = (Participant) session.getAttribute(SessionAttribute.PARTICIPANT);
		String gender = participant.getGender();
		
		List<RatingQuestion> ratingQuestions = new ArrayList<>();
		
		try {
			ratingQuestions.addAll(rqService.loadForGender(gender));
			
		} catch (RatingQuestionsNotExistentException e) {
			//TODO
			e.printStackTrace();
		} 
		
		bfs.fillFeedback(model, ratingQuestions);
		
		return ApplicationPath.RES_FEEDBACK;
	}
	
	@GetMapping(path = ApplicationPath.URL_FEEDBACK)
	public String feedbackSubmit(@Valid List<RatingQuestion> ratingQuestions, Errors errors, HttpSession session, Model model) {
		
		//TODO ??
		if(errors.hasErrors()) {
			
		}
		
		//TODO save results
		bfs.fillGlobal(model);
		bfs.fillEndFeedback(model);
		
		return ApplicationPath.RES_FEEDBACK_END;
	}
}
