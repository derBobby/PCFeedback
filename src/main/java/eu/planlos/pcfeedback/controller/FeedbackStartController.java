package eu.planlos.pcfeedback.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import eu.planlos.pcfeedback.constants.ApplicationPath;
import eu.planlos.pcfeedback.constants.ApplicationRole;
import eu.planlos.pcfeedback.constants.SessionAttribute;
import eu.planlos.pcfeedback.exceptions.ParticipantAlreadyExistsException;
import eu.planlos.pcfeedback.model.Gender;
import eu.planlos.pcfeedback.model.Participant;
import eu.planlos.pcfeedback.service.ParticipantService;

@Controller
public class FeedbackStartController {

	private static final Logger logger = LoggerFactory.getLogger(FeedbackStartController.class);

	@Autowired
	private ModelFillerService mfs;

	@Autowired
	private ParticipantService participantService;

	@GetMapping(path = ApplicationPath.URL_FEEDBACK_START)
	public String feedbackStart(Model model) {

		Participant participant = new Participant("A", "B", "ab@example.com", "123", Gender.FEMALE);
		model.addAttribute(participant);

		return filledSite(model);
	}

	@PostMapping(path = ApplicationPath.URL_FEEDBACK_START)
	public String feedbackStartSubmit(HttpSession session, Authentication authentication, @Valid Participant participant, BindingResult bindingResult, Model model) {

		if (bindingResult.hasErrors()) {
			logger.debug("Input from form not valid");
			
			FieldError genderFieldError = bindingResult.getFieldError("gender");
			if(genderFieldError != null) {
				logger.debug("Gender is missing");
				model.addAttribute("genderError", "darf nicht unausgewählt sein");
			}
			
			return filledSite(model);
		}

		try {
			participantService.exists(participant);
			
		} catch (ParticipantAlreadyExistsException e) {
			//TODO Show message on site that user can't be created
			logger.debug("Participant already exists, returning to form");
			return filledSite(model);
		}

		logger.debug("Adding role participant to session");
		authentication.getAuthorities().add(new SimpleGrantedAuthority(ApplicationRole.ROLE_PARTICIPANT));
		session.setAttribute(SessionAttribute.PARTICIPANT, participant);
		
		return "redirect:" + ApplicationPath.URL_FEEDBACK;
	}

	private String filledSite(Model model) {
		
		mfs.fillStartFeedback(model);
		mfs.fillGlobal(model);
		return ApplicationPath.RES_FEEDBACK_START;
	}
}
