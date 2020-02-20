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
import eu.planlos.pcfeedback.exceptions.ParticipantAlreadyExistingException;
import eu.planlos.pcfeedback.model.Participant;
import eu.planlos.pcfeedback.model.UiTextKey;
import eu.planlos.pcfeedback.service.ModelFillerService;
import eu.planlos.pcfeedback.service.ParticipantService;

@Controller
public class FeedbackStartController {

	private static final Logger LOG = LoggerFactory.getLogger(FeedbackStartController.class);

	@Autowired
	private ModelFillerService mfs;

	@Autowired
	private ParticipantService participantService;

	@RequestMapping(path = ApplicationPath.URL_FEEDBACK_START)
	public String feedbackStart(Model model) {

		Participant participant = participantService.createParticipantForForm();
		
		model.addAttribute(participant);
		
		mfs.fillUiText(model, UiTextKey.MSG_FEEDBACKSTART);
		mfs.fillGlobal(model);
		return ApplicationPath.RES_FEEDBACK_START;
	}
	
	@PostMapping(path = ApplicationPath.URL_FEEDBACK_START)
	public String feedbackStartSubmit(HttpSession session, @Valid Participant participant, BindingResult bindingResult, Model model) throws ServletException, IOException {

		if (bindingResult.hasErrors()) {
			LOG.debug("Input from form not valid");
			
			FieldError genderFieldError = bindingResult.getFieldError("gender");
			if(genderFieldError != null) {
				LOG.debug("Gender is missing");
				model.addAttribute("genderError", "muss ausgewählt sein");
			}
			
			mfs.fillUiText(model, UiTextKey.MSG_FEEDBACKSTART);
			mfs.fillGlobal(model);
			return ApplicationPath.RES_FEEDBACK_START;
		}
		
		LOG.debug("Input from form is valid");

		try {
			
			LOG.debug("Checking if participant exists: {}", participant.toString());
			participantService.exists(participant);
			
			LOG.debug("Adding participant to session");
			session.setAttribute(SessionAttribute.PARTICIPANT, participant);
			
			LOG.debug("Proceeding to feedback site");
			return "redirect:" + ApplicationPath.URL_FEEDBACK;
			
		} catch (ParticipantAlreadyExistingException e) {
			
			LOG.error("Participant exists already, returning to form");
			
			model.addAttribute("PARTICIPANT_EXISTS", true);
			
			mfs.fillUiText(model, UiTextKey.MSG_FEEDBACKSTART);
			mfs.fillGlobal(model);			
			return ApplicationPath.RES_FEEDBACK_START;
		}
	}
}
