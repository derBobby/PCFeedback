package eu.planlos.pcfeedback.controller.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.planlos.pcfeedback.constants.ApplicationPathHelper;
import eu.planlos.pcfeedback.service.ParticipantService;
import eu.planlos.pcfeedback.service.ParticipationResultService;
import eu.planlos.pcfeedback.service.RatingQuestionService;

@RestController
public class ResetController {
	
	private static final Logger LOG = LoggerFactory.getLogger(ResetController.class);
			
	@Autowired
	private ParticipantService pService;

	@Autowired
	private RatingQuestionService rqService;
	
	@Autowired
	private ParticipationResultService prService;

	@GetMapping(path = ApplicationPathHelper.URL_ADMIN_RESET)
	public String reset() {
		
		LOG.error("#----------#   DATABASE IS BEING RESET BY AMDIN !!!   #----------#");
		
		prService.resetDB();
		pService.resetDB();
		rqService.resetDB();
		
		LOG.error("#----------#   DATABASE HAS BEEN RESET BY AMDIN !!!   #----------#");
		
		return "Die DB wurde zurückgesetzt.";
	}
}
