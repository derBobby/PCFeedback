package eu.planlos.pcfeedback.controller.admin;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.planlos.pcfeedback.constants.ApplicationPathHelper;
import eu.planlos.pcfeedback.model.Gender;
import eu.planlos.pcfeedback.model.db.Project;
import eu.planlos.pcfeedback.service.CSVExporterService;
import eu.planlos.pcfeedback.service.ProjectService;

@Controller
public class CSVDownloadController {

	@Autowired
	private CSVExporterService expService;
	
	@Autowired
	private ProjectService pService;

	@GetMapping(ApplicationPathHelper.URL_ADMIN_CSVPARTICIPANTS + "{projectName}")
	@ResponseBody
	public void participantsCSV(HttpServletResponse response, @PathVariable("projectName") String projectName) throws IOException {
		
	    setCsvHeader(response, "Teilnehmerliste.csv");
	    Project project = pService.findProject(projectName);
	    
	    expService.writeParticipantsCSV(project, response.getWriter());
	}

	@GetMapping(ApplicationPathHelper.URL_ADMIN_CSVFEEDBACK + "{projectName}")
	@ResponseBody
	public void feedbackCSV(HttpServletResponse response, @PathVariable("projectName") String projectName) throws IOException {
		
		setCsvHeader(response, "Feedback_MW.csv");
		Project project = pService.findProject(projectName);
	    	    
	    expService.writeRatingQuestionCSV(response.getWriter(), project, null);
	}
	
	@GetMapping(ApplicationPathHelper.URL_ADMIN_CSVFEEDBACK_M + "{projectName}")
	@ResponseBody
	public void feedbackMaleCSV(HttpServletResponse response, @PathVariable("projectName") String projectName) throws IOException {
		
		setCsvHeader(response, "Feedback_M.csv");
		Project project = pService.findProject(projectName);

	    expService.writeRatingQuestionCSV(response.getWriter(), project, Gender.MALE);
	}
	
	@GetMapping(ApplicationPathHelper.URL_ADMIN_CSVFEEDBACK_W + "{projectName}")
	@ResponseBody
	public void feedbackFemaleCSV(HttpServletResponse response, @PathVariable("projectName") String projectName) throws IOException {
		
		setCsvHeader(response, "Feedback_W.csv");
		Project project = pService.findProject(projectName);

	    expService.writeRatingQuestionCSV(response.getWriter(), project, Gender.FEMALE);
	}	
	
	@GetMapping(ApplicationPathHelper.URL_ADMIN_CSVFEEDBACK_FREETEXT + "{projectName}")
	@ResponseBody
	public void feedbackFreeTextCSV(HttpServletResponse response, @PathVariable("projectName") String projectName) throws IOException {
		
		setCsvHeader(response, "Feedback_Freitext.csv");
		Project project = pService.findProject(projectName);

	    expService.writeFreeTextCSV(response.getWriter(), project);
	}
	
	private void setCsvHeader(HttpServletResponse response, String filename) {
		response.setHeader("Content-Disposition", String.format("attachment; filename=%s", filename));
	    response.setContentType("text/csv");		
	}
}