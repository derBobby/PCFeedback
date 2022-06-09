package eu.planlos.pcfeedback.controller.admin;

import eu.planlos.pcfeedback.constants.ApplicationPaths;
import eu.planlos.pcfeedback.service.ParticipantService;
import eu.planlos.pcfeedback.service.ParticipationResultService;
import eu.planlos.pcfeedback.service.RatingQuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ResetController {
	
	private final ParticipantService pService;
	private final RatingQuestionService rqService;
	private final ParticipationResultService prService;
	
	public ResetController(ParticipantService pService, RatingQuestionService rqService, ParticipationResultService prService) {
		this.pService = pService;
		this.rqService = rqService;
		this.prService = prService;
	}
	
	@GetMapping(path = ApplicationPaths.URL_ADMIN_RESET)
	public String reset() {
		
		log.error("#----------#   DATABASE IS BEING RESET BY AMDIN !!!   #----------#");
		
		prService.resetDB();
		pService.resetDB();
		rqService.resetDB();
		
		log.error("#----------#   DATABASE HAS BEEN RESET BY AMDIN !!!   #----------#");
		
		return "Die DB wurde zurückgesetzt.";
	}
}
