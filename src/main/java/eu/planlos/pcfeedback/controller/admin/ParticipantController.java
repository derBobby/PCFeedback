package eu.planlos.pcfeedback.controller.admin;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.planlos.pcfeedback.constants.ApplicationPath;
import eu.planlos.pcfeedback.exceptions.ParticipantNotFoundException;
import eu.planlos.pcfeedback.model.Participant;
import eu.planlos.pcfeedback.service.EditParticipantService;
import eu.planlos.pcfeedback.service.ModelFillerService;
import eu.planlos.pcfeedback.service.ParticipantService;

@Controller
public class ParticipantController {

	private static final Logger logger = LoggerFactory.getLogger(ParticipantController.class);
	
	@Autowired
	private ModelFillerService mfs;
	
	@Autowired
	private EditParticipantService eps;

	@Autowired
	private ParticipantService ps;
	
	@RequestMapping(path = ApplicationPath.URL_ADMIN_EDITPARTICIPANT + "{idParticipant}", method = RequestMethod.GET)
	public String editParticipant(@PathVariable Long idParticipant, Model model) throws ParticipantNotFoundException {

		Participant participant;
		
		try {
			participant = ps.findByIdParticipant(idParticipant);
			model.addAttribute(participant);
			model.addAttribute("URL_ADMIN_EDITPARTICIPANT", ApplicationPath.URL_ADMIN_EDITPARTICIPANT);
			mfs.fillGlobal(model);
			return ApplicationPath.RES_ADMIN_EDITPARTICIPANT;
			
		} catch (ParticipantNotFoundException e) {
			logger.error(e.getMessage());
			//TODO throw best?
			throw e;
		}
	}
	
	@RequestMapping(path = ApplicationPath.URL_ADMIN_EDITPARTICIPANT, method = RequestMethod.POST)
	public String submitEditParticipant(@Valid Participant participant, BindingResult bindingResult, Model model) throws ParticipantNotFoundException {
		
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

			if(eps.saveParticipantAndCorrectRatingQuestions(participant)) {
				mfs.fillGlobal(model);
				return ApplicationPath.RES_ADMIN_EDITPARTICIPANTDONE;
			}
			return "redirect:" + ApplicationPath.URL_ADMIN_SHOWFEEDBACK;
			
		} catch (ParticipantNotFoundException e) {
			logger.debug(e.getMessage());
			throw e;
		}
	}
}
