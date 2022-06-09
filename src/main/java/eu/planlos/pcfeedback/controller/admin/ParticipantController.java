package eu.planlos.pcfeedback.controller.admin;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.planlos.pcfeedback.constants.ApplicationPaths;
import eu.planlos.pcfeedback.exceptions.ParticipantNotFoundException;
import eu.planlos.pcfeedback.model.db.Participant;
import eu.planlos.pcfeedback.model.db.Project;
import eu.planlos.pcfeedback.service.ModelFillerService;
import eu.planlos.pcfeedback.service.ParticipantService;
import eu.planlos.pcfeedback.service.ParticipationService;

@Slf4j
@Controller
public class ParticipantController {

	private final ModelFillerService mfs;
	private final ParticipationService eps;
	private final ParticipantService participantService;
	
	public ParticipantController(ModelFillerService mfs, ParticipationService eps, ParticipantService participantService) {
		this.mfs = mfs;
		this.eps = eps;
		this.participantService = participantService;
	}
	
	@RequestMapping(path = ApplicationPaths.URL_ADMIN_EDITPARTICIPANT + "{idParticipant}", method = RequestMethod.GET)
	public String editParticipant(@PathVariable Long idParticipant, Model model) throws ParticipantNotFoundException {

		Participant participant;
		
		try {
			participant = participantService.findByIdParticipant(idParticipant);
			model.addAttribute(participant);
			model.addAttribute("URL_ADMIN_EDITPARTICIPANT", ApplicationPaths.URL_ADMIN_EDITPARTICIPANT);
			mfs.fillGlobal(model);
			return ApplicationPaths.RES_ADMIN_EDITPARTICIPANT;
			
		} catch (ParticipantNotFoundException e) {
			log.error(e.getMessage());
			throw e;
		}
	}
	
	@RequestMapping(path = ApplicationPaths.URL_ADMIN_EDITPARTICIPANT, method = RequestMethod.POST)
	public String submitEditParticipant(@Valid Participant participant, BindingResult bindingResult, Model model) throws ParticipantNotFoundException {
		
		if (bindingResult.hasErrors()) {
			log.debug("Input from form not valid");
			
			FieldError genderFieldError = bindingResult.getFieldError("gender");
			if(genderFieldError != null) {
				log.debug("Gender is missing");
				model.addAttribute("genderError", "muss ausgewählt sein");
			}
			
			model.addAttribute("URL_ADMIN_EDITPARTICIPANT", ApplicationPaths.URL_ADMIN_EDITPARTICIPANT);
			mfs.fillGlobal(model);
			return ApplicationPaths.RES_ADMIN_EDITPARTICIPANT;
		}
		
		log.debug("Input from form is valid");
		
		try {

			if(eps.editParticipant(participant)) {
				mfs.fillGlobal(model);
				model.addAttribute("URL_TOPROJECT", ApplicationPaths.URL_ADMIN_SHOWFEEDBACK + participant.getProject().getProjectName());
				return ApplicationPaths.RES_ADMIN_EDITPARTICIPANTDONE;
			}
			return "redirect:" + ApplicationPaths.URL_ADMIN_SHOWFEEDBACK + participant.getProject().getProjectName();
			
		} catch (ParticipantNotFoundException e) {
			log.debug(e.getMessage());
			throw e;
		}
	}
	
	@RequestMapping(path = ApplicationPaths.URL_ADMIN_DELETEPARTICIPANT + "{idParticipant}", method = RequestMethod.GET)
	public String deleteParticipant(@PathVariable Long idParticipant, Model model) throws ParticipantNotFoundException {

		try {
			Participant participant = participantService.findByIdParticipant(idParticipant);
			Project project = participant.getProject();
			eps.deleteParticipation(participant);
			return "redirect:" + ApplicationPaths.URL_ADMIN_SHOWFEEDBACK + project.getProjectName();
			
		} catch (ParticipantNotFoundException e) {
			log.error(e.getMessage());
			throw e;
		}
	}
}
