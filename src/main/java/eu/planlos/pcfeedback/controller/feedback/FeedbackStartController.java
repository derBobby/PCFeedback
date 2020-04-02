package eu.planlos.pcfeedback.controller.feedback;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import eu.planlos.pcfeedback.constants.ApplicationPathHelper;
import eu.planlos.pcfeedback.constants.SessionAttributeHelper;
import eu.planlos.pcfeedback.exceptions.ParticipantAlreadyExistingException;
import eu.planlos.pcfeedback.model.Participant;
import eu.planlos.pcfeedback.model.Project;
import eu.planlos.pcfeedback.model.UiTextKey;
import eu.planlos.pcfeedback.service.ModelFillerService;
import eu.planlos.pcfeedback.service.ParticipantService;

@Controller
@SessionAttributes(names = {"participant", "project"})
public class FeedbackStartController {

	private static final Logger LOG = LoggerFactory.getLogger(FeedbackStartController.class);

	@Autowired
	private ModelFillerService mfs;

	@Autowired
	private ParticipantService participantService;

	/**
	 * Shows form to enter participant details like name etc.
	 * 
	 * @param model
	 * @param session
	 * @param project
	 * @return
	 */
	@RequestMapping(path = ApplicationPathHelper.URL_FEEDBACK_START)
	public String participantForm(Model model,
			HttpSession session,
			@ModelAttribute(SessionAttributeHelper.PROJECT) Project project) {

		// Filter validates not null
		Participant participant = participantService.createParticipantForForm(project);
		
		LOG.debug("Adding new participant to form: project={}", project.getName());
		model.addAttribute(participant);

		mfs.fillUiText(model, project, UiTextKey.MSG_FEEDBACK_START);
		mfs.fillGlobal(model);
		return ApplicationPathHelper.RES_FEEDBACK_START;
	}

	/**
	 * Checks submitted participant for validity.
	 * If successful writes him into session and redirects to feedpack page
	 * 
	 * @param model
	 * @param session
	 * @param bindingResult
	 * @param project
	 * @param participant
	 * @return
	 */
	@PostMapping(path = ApplicationPathHelper.URL_FEEDBACK_START)
	public String participantSubmit(Model model,
			HttpSession session,
			@ModelAttribute(SessionAttributeHelper.PROJECT) Project project,
			@ModelAttribute(SessionAttributeHelper.PARTICIPANT) @Valid Participant participant,
			BindingResult bindingResult) {

		// validate model input
		if (bindingResult.hasErrors()) {
			LOG.debug("Input from form not valid");

			FieldError genderFieldError = bindingResult.getFieldError("gender");
			if (genderFieldError != null) {
				LOG.debug("Gender is missing");
				model.addAttribute("genderError", "muss ausgewählt sein");
			}

			mfs.fillUiText(model, project, UiTextKey.MSG_FEEDBACK_START);
			mfs.fillGlobal(model);
			return ApplicationPathHelper.RES_FEEDBACK_START;
		}

		LOG.debug("Form input is valid");

		try {

			LOG.debug("Checking if participant exists: {}", participant.toString());
			participantService.exists(participant);

			LOG.debug("Adding participant to session");
			Object o1 = session.getAttribute(SessionAttributeHelper.PROJECT);
			System.err.println(o1.hashCode());
			session.setAttribute(SessionAttributeHelper.PARTICIPANT, participant);
			Object o2 = session.getAttribute(SessionAttributeHelper.PROJECT);
			System.err.println(o1.hashCode());
			System.err.println(o2.hashCode());
			
			LOG.debug("Proceeding to feedback site");
			return "redirect:" + ApplicationPathHelper.URL_FEEDBACK_QUESTION;

		} catch (ParticipantAlreadyExistingException e) {

			LOG.error("Participant exists already, returning to form");

			model.addAttribute("PARTICIPANT_EXISTS", true);

			mfs.fillUiText(model, project, UiTextKey.MSG_FEEDBACK_START);
			mfs.fillGlobal(model);
			return ApplicationPathHelper.RES_FEEDBACK_START;
		}
	}
}
