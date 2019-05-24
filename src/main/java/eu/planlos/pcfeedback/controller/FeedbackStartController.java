package eu.planlos.pcfeedback.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import eu.planlos.pcfeedback.constants.ApplicationPath;
import eu.planlos.pcfeedback.constants.SessionAttribute;
import eu.planlos.pcfeedback.model.Participant;

@Controller
public class FeedbackStartController {

	@Autowired
	private ModelFillerService mfs;

	@GetMapping(path = ApplicationPath.URL_FEEDBACK_START)
	public String feedbackStart(Model model) {

		Participant participant = new Participant();
		model.addAttribute(participant);
		
		model.addAttribute("FEEDBACKSTART_ACTION", ApplicationPath.URL_FEEDBACK_START);
		
		mfs.fill(model);
		
		return ApplicationPath.RES_FEEDBACK_START;
	}
	
	@PostMapping(path = ApplicationPath.URL_FEEDBACK_START)
	public String feedbackStartSubmit(HttpSession session, @Valid Participant participant, BindingResult bindingResult, Model model) {
		
		if(bindingResult.hasErrors()) {
			
			model.addAttribute("FEEDBACKSTART_ACTION", ApplicationPath.URL_FEEDBACK_START);
			
			mfs.fill(model);
			
			return ApplicationPath.RES_FEEDBACK_START;
		}
		
		session.setAttribute(SessionAttribute.PARTICIPANT, participant);
		
		return "redirect:" + ApplicationPath.URL_FEEDBACK;
	}
}
