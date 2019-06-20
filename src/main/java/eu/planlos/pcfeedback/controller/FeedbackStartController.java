package eu.planlos.pcfeedback.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import eu.planlos.pcfeedback.constants.ApplicationPath;
import eu.planlos.pcfeedback.constants.SessionAttribute;
import eu.planlos.pcfeedback.exceptions.ParticipantAlreadyExistsException;
import eu.planlos.pcfeedback.model.Participant;
import eu.planlos.pcfeedback.service.ModelFillerService;
import eu.planlos.pcfeedback.service.ParticipantService;

@Controller
public class FeedbackStartController {

	private static final Logger logger = LoggerFactory.getLogger(FeedbackStartController.class);

	@Autowired
	private ModelFillerService mfs;

	@Autowired
	private ParticipantService participantService;

	@RequestMapping(path = ApplicationPath.URL_FEEDBACK_START)
	public String feedbackStart(Model model) {
		//TODO dependent on the profile create participant. Factory?
		Participant participant = participantService.createParticipantForForm();
		model.addAttribute(participant);

		mfs.fillGlobal(model);
		return ApplicationPath.RES_FEEDBACK_START;
	}

	@PostMapping(path = ApplicationPath.URL_FEEDBACK_START)
	public String feedbackStartSubmit(HttpSession session, @Valid Participant participant, BindingResult bindingResult, Model model) throws ServletException, IOException {

		if (bindingResult.hasErrors()) {
			logger.debug("Input from form not valid");
			
			FieldError genderFieldError = bindingResult.getFieldError("gender");
			if(genderFieldError != null) {
				logger.debug("Gender is missing");
				model.addAttribute("genderError", "muss ausgewählt sein");
			}
			
			mfs.fillGlobal(model);
			return ApplicationPath.RES_FEEDBACK_START;
		}
		logger.debug("Input from form is valid");

		try {
			participantService.exists(participant);
			
		} catch (ParticipantAlreadyExistsException e) {
			//TODO Show message on site that user can't be created
			logger.debug("Participant already exists, returning to form");
			
			model.addAttribute("PARTICIPANT_EXISTS", true);
			mfs.fillGlobal(model);
			return ApplicationPath.RES_FEEDBACK_START;
		}
		
		logger.debug("Adding new participant to session");
		session.setAttribute(SessionAttribute.PARTICIPANT, participant);
		
		return "redirect:" + ApplicationPath.URL_FEEDBACK;
	}
}
