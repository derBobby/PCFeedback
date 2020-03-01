package eu.planlos.pcfeedback.controller.admin;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.planlos.pcfeedback.constants.ApplicationPathHelper;
import eu.planlos.pcfeedback.model.Gender;
import eu.planlos.pcfeedback.service.CSVExporterService;

@Controller
public class CSVDownloadController {

	@Autowired
	private CSVExporterService expService;

	@GetMapping(ApplicationPathHelper.URL_ADMIN_CSVPARTICIPANTS)
	@ResponseBody
	public void participantsCSV(HttpServletResponse response) throws IOException {
		
	    response.setHeader("Content-Disposition", "attachment; filename=Teilnehmerliste.csv");
	    response.setContentType("text/csv");
	    	    
	    expService.writeParticipantsCSV(response.getWriter());
	}
	
	@GetMapping(ApplicationPathHelper.URL_ADMIN_CSVFEEDBACK)
	@ResponseBody
	public void feedbackCSV(HttpServletResponse response) throws IOException {
		
	    response.setHeader("Content-Disposition", "attachment; filename=Feedback_MW.csv");
	    response.setContentType("text/csv");
	    	    
	    expService.writeRatingQuestionCSV(response.getWriter(), null);
	}
	
	@GetMapping(ApplicationPathHelper.URL_ADMIN_CSVFEEDBACK_M)
	@ResponseBody
	public void feedbackMaleCSV(HttpServletResponse response) throws IOException {
		
	    response.setHeader("Content-Disposition", "attachment; filename=Feedback_M.csv");
	    response.setContentType("text/csv");
	    	    
	    expService.writeRatingQuestionCSV(response.getWriter(), Gender.MALE);
	}
	
	@GetMapping(ApplicationPathHelper.URL_ADMIN_CSVFEEDBACK_W)
	@ResponseBody
	public void feedbackFemaleCSV(HttpServletResponse response) throws IOException {
		
	    response.setHeader("Content-Disposition", "attachment; filename=Feedback_W.csv");
	    response.setContentType("text/csv");
	    	    
	    expService.writeRatingQuestionCSV(response.getWriter(), Gender.FEMALE);
	}	
	
	@GetMapping(ApplicationPathHelper.URL_ADMIN_CSVFEEDBACK_FREETEXT)
	@ResponseBody
	public void feedbackFreeTextCSV(HttpServletResponse response) throws IOException {
		
	    response.setHeader("Content-Disposition", "attachment; filename=Feedback_Freitext.csv");
	    response.setContentType("text/csv");
	    	    
	    expService.writeFreeTextCSV(response.getWriter());
	}	
}