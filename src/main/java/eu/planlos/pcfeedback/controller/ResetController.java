package eu.planlos.pcfeedback.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.planlos.pcfeedback.constants.ApplicationPath;
import eu.planlos.pcfeedback.service.ParticipantService;
import eu.planlos.pcfeedback.service.ParticipationResultService;
import eu.planlos.pcfeedback.service.RatingQuestionService;
import eu.planlos.pcfeedback.service.UserAgentService;

@RestController
public class ResetController {
	
	private static final Logger LOG = LoggerFactory.getLogger(ResetController.class);
			
	@Autowired
	private ParticipantService pService;

	@Autowired
	private RatingQuestionService rqService;

	@Autowired
	private UserAgentService uaService;
	
	@Autowired
	private ParticipationResultService prService;

	@GetMapping(path = ApplicationPath.URL_ADMIN_RESET)
	public String reset() {
		
		LOG.error("#----------#   DATABASE IS BEING RESET BY AMDIN !!!   #----------#");
		
		uaService.resetDB();
		prService.resetDB();
		pService.resetDB();
		rqService.resetDB();
		
		LOG.error("#----------#   DATABASE HAS BEEN RESET BY AMDIN !!!   #----------#");
		
		return "Die DB wurde zurückgesetzt.";
	}
}
