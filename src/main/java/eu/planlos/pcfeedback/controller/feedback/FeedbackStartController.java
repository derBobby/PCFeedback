package eu.planlos.pcfeedback.controller.feedback;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import eu.planlos.pcfeedback.constants.ApplicationPaths;
import eu.planlos.pcfeedback.constants.ApplicationSessionAttributes;
import eu.planlos.pcfeedback.exceptions.DataPrivacyStatementNotAcceptedException;
import eu.planlos.pcfeedback.exceptions.ParticipantAlreadyExistingException;
import eu.planlos.pcfeedback.exceptions.ParticipantIsMissingEmailException;
import eu.planlos.pcfeedback.exceptions.ParticipantIsMissingMobileException;
import eu.planlos.pcfeedback.exceptions.PriceGameStatementNotAcceptedException;
import eu.planlos.pcfeedback.model.UiTextKey;
import eu.planlos.pcfeedback.model.db.Participant;
import eu.planlos.pcfeedback.model.db.Project;
import eu.planlos.pcfeedback.model.db.UiText;
import eu.planlos.pcfeedback.service.ModelFillerService;
import eu.planlos.pcfeedback.service.ParticipantService;
import eu.planlos.pcfeedback.service.UiTextService;
import org.springframework.web.bind.annotation.SessionAttributes;

@Slf4j
@Controller
@SessionAttributes(names = {"participant", "project"})
public class FeedbackStartController {

	private final ModelFillerService mfs;
	private final ParticipantService participantService;
	private final UiTextService uts;
	
	public FeedbackStartController(ModelFillerService mfs, ParticipantService participantService, UiTextService uts) {
		this.mfs = mfs;
		this.participantService = participantService;
		this.uts = uts;
	}
	
	/**
	 * Creates form for participant with empty participant object
	 * 
	 * @param model
	 * @param session
	 * @param project
	 * @return
	 */
	@RequestMapping(path = ApplicationPaths.URL_FEEDBACK_START)
	public String participantForm(Model model,
			HttpSession session,
			@ModelAttribute(ApplicationSessionAttributes.PROJECT) Project project) {

		// Filter validates not null
		Participant participant = participantService.createParticipantForForm(project);
		
		log.debug("Adding new participant to form: project={}", project.getProjectName());
		model.addAttribute(participant);

		UiText uiText = uts.getUiText(project, UiTextKey.MSG_FEEDBACK_START);
		mfs.fillUiText(model, uiText);
		mfs.fillGlobal(model);
		return ApplicationPaths.RES_FEEDBACK_START;
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
	@PostMapping(path = ApplicationPaths.URL_FEEDBACK_START)
	public String participantSubmit(Model model,
			HttpSession session,
			@ModelAttribute(ApplicationSessionAttributes.PROJECT) Project project,
			@ModelAttribute(ApplicationSessionAttributes.PARTICIPANT) @Valid Participant participant,
			BindingResult bindingResult) {

		// validate model input
		if (bindingResult.hasErrors()) {
			log.debug("Input from form not valid, details see next line:");
			log.debug("{}", bindingResult.getFieldError());
	
			FieldError genderFieldError = bindingResult.getFieldError("gender");
			if (genderFieldError != null) {
				log.debug("Gender is missing");
				model.addAttribute("genderError", "muss ausgewählt sein");
			}

			return backToForm(model, project);
		}


		try {

			printDebug(participant);
			
			validateOptionalEmail(project, participant);
			validateOptionalMobile(project, participant);
			validateOptionalPriceGameStatementAccepted(project, participant);
			validateDataPrivacyStatementAccepted(participant);
			
			log.debug("Form input is valid");

			log.debug("Checking if participant exists: {}", participant.toString());
			participantService.exists(participant);

			log.debug("Adding participant to session");
			session.setAttribute(ApplicationSessionAttributes.PARTICIPANT, participant);

			log.debug("Proceeding to feedback site");
			return "redirect:" + ApplicationPaths.URL_FEEDBACK_QUESTION;

		} catch (ParticipantAlreadyExistingException e) {
			log.error("Participant exists already, returning to form");
			model.addAttribute("PARTICIPANT_EXISTS", true);
			return backToForm(model, project);

		} catch (ParticipantIsMissingMobileException e) {
			log.error("Participant has no mobile number set");
			bindingResult.addError(new FieldError("participant", "mobile", e.getMessage()));
			return backToForm(model, project);

		} catch (ParticipantIsMissingEmailException e) {
			log.error("Participant has no email address set");
			bindingResult.addError(new FieldError("participant", "email", e.getMessage()));
			return backToForm(model, project);
			
		} catch (DataPrivacyStatementNotAcceptedException e) {
			log.error("Data privacy statement was not accepted");
			bindingResult.addError(new FieldError("participant", "dataPrivacyStatementAccepted", e.getMessage()));
			return backToForm(model, project);
			
		} catch (PriceGameStatementNotAcceptedException e) {
			log.error("Price game statement was not accepted");
			bindingResult.addError(new FieldError("participant", "priceGameStatementAccepted", e.getMessage()));
			return backToForm(model, project);
		}
	}

	private String backToForm(Model model, Project project) {
		UiText uiText = uts.getUiText(project, UiTextKey.MSG_FEEDBACK_START);
		Participant participant = (Participant) model.getAttribute("participant");
		participant.setDataPrivacyStatementAccepted(false);
		participant.setPriceGameStatementAccepted(false);
		mfs.fillUiText(model, uiText);
		mfs.fillGlobal(model);
		return ApplicationPaths.RES_FEEDBACK_START;
	}
	
	private void validateOptionalEmail(Project project, Participant participant) throws ParticipantIsMissingEmailException {
		
		String email = participant.getEmail();
		
		if(project.isNeedMail() &&
				(email == null || email.equals(""))){
			throw new ParticipantIsMissingEmailException("E-Mailadresse ist ein Pflichtfeld");
		}
		log.debug("Mail not necessary or was given");
	}

	private void validateOptionalMobile(Project project, Participant participant) throws ParticipantIsMissingMobileException {
		
		String mobile = participant.getMobile();
		
		if(project.isNeedMobile() &&
				(mobile == null || mobile.equals(""))){
			throw new ParticipantIsMissingMobileException("Mobilnummmer ist ein Pflichtfeld");
		}		
		log.debug("Mobile not necessary or was given");
	}

	private void validateOptionalPriceGameStatementAccepted(Project project, @Valid Participant participant) throws PriceGameStatementNotAcceptedException {

		boolean priceGameStatementAccepted = participant.isPriceGameStatementAccepted();
		
		if(project.isPricegame() &&
				! priceGameStatementAccepted) {
			throw new PriceGameStatementNotAcceptedException("Die Gewinnspielbedingungen wurden nicht akzeptiert");
		}		
		log.debug("Price game statement acceptance not necessary or was given");
	}

	private void validateDataPrivacyStatementAccepted(@Valid Participant participant) throws DataPrivacyStatementNotAcceptedException {
		boolean dataPrivacyStatementAccepted = participant.isDataPrivacyStatementAccepted();
		
		if(! dataPrivacyStatementAccepted) {
			throw new DataPrivacyStatementNotAcceptedException("Die Datenschutzerklärung wurde nicht akzeptiert");
		}
		log.debug("Data privacy statement acceptance was given");
	}
	
	private void printDebug(@Valid Participant participant) {
		log.debug("firstname={}", participant.getFirstname());
		log.debug("name={}", participant.getName());
		log.debug("email={}", participant.getEmail());
		log.debug("mobile={}", participant.getMobile());
		log.debug("user-agent={}", participant.getUserAgent());
		log.debug("dataprivacyStatementAccepted={}", participant.isDataPrivacyStatementAccepted());
		log.debug("priceGameStatementAccepted={}", participant.isPriceGameStatementAccepted());
	}
}
