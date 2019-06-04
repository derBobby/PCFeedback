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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import eu.planlos.pcfeedback.constants.ApplicationPath;
import eu.planlos.pcfeedback.constants.SessionAttribute;
import eu.planlos.pcfeedback.exceptions.RatingQuestionsNotExistentException;
import eu.planlos.pcfeedback.model.Gender;
import eu.planlos.pcfeedback.model.Participant;
import eu.planlos.pcfeedback.model.RatingQuestion;
import eu.planlos.pcfeedback.service.ModelFillerService;
import eu.planlos.pcfeedback.service.RatingQuestionService;

@Controller
public class FeedbackController {

	private static final Logger logger = LoggerFactory.getLogger(FeedbackController.class);
	
	@Autowired
	private ModelFillerService mfs;
	
	@Autowired
	private RatingQuestionService ratingQuestionService;
	
	@RequestMapping(path = ApplicationPath.URL_FEEDBACK)
	public String feedback(Model model, HttpSession session) {
		
		Participant participant = (Participant) session.getAttribute(SessionAttribute.PARTICIPANT);
		Gender gender = participant.getGender();

		if(Gender.FEMALE.equals(gender)) {
			System.out.println("WEIBLICH :-)");
		}
		if(Gender.MALE.equals(gender)) {
			System.out.println("MÄNNLICH :-)");
		}
		
		List<RatingQuestion> ratingQuestions = new ArrayList<>();
		
		try {
			logger.debug("Participant has gender: " + gender.toString());
			ratingQuestions.addAll(ratingQuestionService.loadForGender(gender));
			
		} catch (RatingQuestionsNotExistentException e) {
			//TODO
			e.printStackTrace();
		} 
		
		mfs.fillFeedback(model, ratingQuestions);
		
		return ApplicationPath.RES_FEEDBACK;
	}
	
	@PostMapping(path = ApplicationPath.URL_FEEDBACK)
	public String feedbackSubmit(@Valid List<RatingQuestion> ratingQuestionList, BindingResult bindingResult, HttpSession session, Model model) {
		
		//TODO ??
		if(bindingResult.hasErrors()) {
			System.out.println("### FAIL ###");
		}
		
		ratingQuestionService.saveFeedback(ratingQuestionList);
		
		mfs.fillGlobal(model);
		mfs.fillEndFeedback(model);
		
		return ApplicationPath.RES_FEEDBACK_END;
	}
}
