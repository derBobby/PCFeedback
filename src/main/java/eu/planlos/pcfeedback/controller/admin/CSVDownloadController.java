package eu.planlos.pcfeedback.controller.admin;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.planlos.pcfeedback.constants.ApplicationPath;
import eu.planlos.pcfeedback.model.Gender;
import eu.planlos.pcfeedback.service.CSVExporterService;

@Controller
public class CSVDownloadController {

	@Autowired
	private CSVExporterService expService;

	@GetMapping(ApplicationPath.URL_ADMIN_CSVPARTICIPANTS)
	@ResponseBody
	public void participantsCSV(HttpServletResponse response) throws IOException {
		
	    response.setHeader("Content-Disposition", "attachment; filename=Teilnehmer.csv");
	    response.setContentType("text/csv");
	    	    
	    expService.writeParticipantsCSV(response.getWriter());
	}
	
	@GetMapping(ApplicationPath.URL_ADMIN_CSVFEEDBACK)
	@ResponseBody
	public void feedbackCSV(HttpServletResponse response) throws IOException {
		
	    response.setHeader("Content-Disposition", "attachment; filename=Feedback.csv");
	    response.setContentType("text/csv");
	    	    
	    expService.writeRatingQuestionCSV(response.getWriter(), null);
	}
	
	@GetMapping(ApplicationPath.URL_ADMIN_CSVFEEDBACK_M)
	@ResponseBody
	public void feedbackMaleCSV(HttpServletResponse response) throws IOException {
		
	    response.setHeader("Content-Disposition", "attachment; filename=Feedback_M.csv");
	    response.setContentType("text/csv");
	    	    
	    expService.writeRatingQuestionCSV(response.getWriter(), Gender.MALE);
	}
	
	@GetMapping(ApplicationPath.URL_ADMIN_CSVFEEDBACK_W)
	@ResponseBody
	public void feedbackFemaleCSV(HttpServletResponse response) throws IOException {
		
	    response.setHeader("Content-Disposition", "attachment; filename=Feedback_W.csv");
	    response.setContentType("text/csv");
	    	    
	    expService.writeRatingQuestionCSV(response.getWriter(), Gender.FEMALE);
	}	
}