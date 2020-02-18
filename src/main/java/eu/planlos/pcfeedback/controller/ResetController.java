package eu.planlos.pcfeedback.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.planlos.pcfeedback.constants.ApplicationPath;
import eu.planlos.pcfeedback.service.ParticipantService;
import eu.planlos.pcfeedback.service.RatingQuestionService;

@RestController
public class ResetController {
	
	private static final Logger LOG = LoggerFactory.getLogger(ResetController.class);
			
	@Autowired
	private ParticipantService ps;

	@Autowired
	private RatingQuestionService rqs;

	@GetMapping(path = ApplicationPath.URL_ADMIN_RESET)
	public String reset() {
		
		LOG.error("#----------#   DATABASE IS BEING RESET BY AMDIN !!!   #----------#");
		
		ps.resetDB();
		rqs.resetDB();
		
		LOG.error("#----------#   DATABASE HAS BEEN RESET BY AMDIN !!!   #----------#");
		
		return "Die DB wurde zurückgesetzt.";
	}
}
