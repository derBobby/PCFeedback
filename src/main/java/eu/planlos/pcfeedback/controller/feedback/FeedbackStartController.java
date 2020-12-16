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
import eu.planlos.pcfeedback.exceptions.ParticipantIsMissingEmailException;
import eu.planlos.pcfeedback.exceptions.ParticipantIsMissingMobileException;
import eu.planlos.pcfeedback.model.UiTextKey;
import eu.planlos.pcfeedback.model.db.Participant;
import eu.planlos.pcfeedback.model.db.Project;
import eu.planlos.pcfeedback.model.db.UiText;
import eu.planlos.pcfeedback.service.ModelFillerService;
import eu.planlos.pcfeedback.service.ParticipantService;
import eu.planlos.pcfeedback.service.UiTextService;

@Controller
@SessionAttributes(names = {"participant", "project"})
public class FeedbackStartController {

	private static final Logger LOG = LoggerFactory.getLogger(FeedbackStartController.class);

	@Autowired
	private ModelFillerService mfs;

	@Autowired
	private ParticipantService participantService;
	
	@Autowired
	private UiTextService uts;

	/**
	 * Creates form for participant with empty participant object
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
		
		LOG.debug("Adding new participant to form: project={}", project.getProjectName());
		model.addAttribute(participant);

		UiText uiText = uts.getUiText(project, UiTextKey.MSG_FEEDBACK_START);
		mfs.fillUiText(model, uiText);
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

			return backToForm(model, project);
		}


		try {

			validateOptionalEmail(project, participant);
			validateOptionalMobile(project, participant);
			
			LOG.debug("Form input is valid");

			LOG.debug("Checking if participant exists: {}", participant.toString());
			participantService.exists(participant);

			LOG.debug("Adding participant to session");
			session.setAttribute(SessionAttributeHelper.PARTICIPANT, participant);

			LOG.debug("Proceeding to feedback site");
			return "redirect:" + ApplicationPathHelper.URL_FEEDBACK_QUESTION;

		} catch (ParticipantAlreadyExistingException e) {
			LOG.error("Participant exists already, returning to form");
			model.addAttribute("PARTICIPANT_EXISTS", true);
			return backToForm(model, project);

		} catch (ParticipantIsMissingMobileException e) {
			LOG.error("Participant has no mobile number set");
			bindingResult.addError(new FieldError("participant", "mobile", e.getMessage()));
			return backToForm(model, project);

		} catch (ParticipantIsMissingEmailException e) {
			LOG.error("Participant has no email address set");
			bindingResult.addError(new FieldError("participant", "email", e.getMessage()));
			return backToForm(model, project);
		}
	}

	private String backToForm(Model model, Project project) {
		UiText uiText = uts.getUiText(project, UiTextKey.MSG_FEEDBACK_START);
		mfs.fillUiText(model, uiText);
		mfs.fillGlobal(model);
		return ApplicationPathHelper.RES_FEEDBACK_START;		
	}
	
	private void validateOptionalEmail(Project project, Participant participant) throws ParticipantIsMissingEmailException {
		
		String email = participant.getEmail();
		
		if(project.getNeedMail() &&
				(email == null || email.equals(""))){
			throw new ParticipantIsMissingEmailException("E-Mailadresse ist ein Pflichtfeld");
		}
		LOG.debug("Mail not necessary or was given");
	}

	private void validateOptionalMobile(Project project, Participant participant) throws ParticipantIsMissingMobileException {
		
		String mobile = participant.getMobile();
		
		if(project.getNeedMobile() &&
				(mobile == null || mobile.equals(""))){
			throw new ParticipantIsMissingMobileException("Mobilnummmer ist ein Pflichtfeld");
		}		
		LOG.debug("Mobile not necessary or was given");
	}
}
