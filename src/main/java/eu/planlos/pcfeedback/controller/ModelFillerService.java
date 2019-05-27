package eu.planlos.pcfeedback.controller;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import eu.planlos.pcfeedback.constants.ApplicationPath;
import eu.planlos.pcfeedback.model.RatingQuestion;

@Service
public class ModelFillerService {

	public void fillGlobal(Model model) {

		model.addAttribute("URL_HOME", ApplicationPath.URL_HOME);
		model.addAttribute("URL_IMPRESSUM", ApplicationPath.URL_IMPRESSUM);
		model.addAttribute("URL_DATENSCHUTZ", ApplicationPath.URL_DATENSCHUTZ);
	}

	public void fillStartFeedback(Model model) {
		
		model.addAttribute("URL_FEEDBACK_START", ApplicationPath.URL_FEEDBACK_START);
	}
	
	public void fillFeedback(Model model, List<RatingQuestion> ratingQuestions) {

		model.addAttribute("LIST_RATINGQUESTIONS", ratingQuestions);
		model.addAttribute("URL_RESTART", ApplicationPath.URL_RESTART);
	}

	public void fillEndFeedback(Model model) {
		
		// TODO Auto-generated method stub
	}
}
