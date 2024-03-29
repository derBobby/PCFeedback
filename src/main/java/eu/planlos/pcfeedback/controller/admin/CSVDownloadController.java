package eu.planlos.pcfeedback.controller.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.planlos.pcfeedback.constants.ApplicationPaths;
import eu.planlos.pcfeedback.model.Gender;
import eu.planlos.pcfeedback.model.db.Participant;
import eu.planlos.pcfeedback.model.db.ParticipationResult;
import eu.planlos.pcfeedback.model.db.Project;
import eu.planlos.pcfeedback.model.db.RatingQuestion;
import eu.planlos.pcfeedback.service.ParticipantService;
import eu.planlos.pcfeedback.service.ParticipationResultService;
import eu.planlos.pcfeedback.service.ProjectService;
import eu.planlos.pcfeedback.service.RatingQuestionService;
import eu.planlos.pcfeedback.util.RatingQuestionEvaluator;
import eu.planlos.pcfeedback.util.csv.FreeTextRecordCSVExporter;
import eu.planlos.pcfeedback.util.csv.ICSVExporter;
import eu.planlos.pcfeedback.util.csv.ParticipantCSVExporter;
import eu.planlos.pcfeedback.util.csv.RatingQuestionCSVExporter;

@Slf4j
@Controller
public class CSVDownloadController {

	private final ProjectService pService;
	private final ParticipantService participantService;
	private final RatingQuestionService rqService;
	private final ParticipationResultService prService;

	public CSVDownloadController(ProjectService pService, ParticipantService participantService, RatingQuestionService rqService, ParticipationResultService prService) {
		this.pService = pService;
		this.participantService = participantService;
		this.rqService = rqService;
		this.prService = prService;
	}
	
	@GetMapping(ApplicationPaths.URL_ADMIN_CSVPARTICIPANTS + "{idProject}")
	@ResponseBody
	public void participantsToCSV(HttpServletResponse response, @PathVariable("idProject") Long idProject) throws IOException {
		
	    Project project = pService.findProject(idProject);
	    List<Participant> pList = participantService.getAllParticipantsForProject(project);

	    setResponseHeader(response, "Teilnehmerliste.csv");
	    
	    ICSVExporter exporter = new ParticipantCSVExporter();
	    exporter.writeCSV(pList, response.getWriter());
	}

	@GetMapping(ApplicationPaths.URL_ADMIN_CSVFEEDBACK + "{idProject}")
	@ResponseBody
	public void allFeedbackToCSV(HttpServletResponse response, @PathVariable("idProject") Long idProject) throws IOException {
		
		Project project = pService.findProject(idProject);
		
		List<RatingQuestion> rqList = new ArrayList<>();
		List<RatingQuestion> rqListMale = rqService.loadByProjectAndGender(project, Gender.MALE);
		List<RatingQuestion> rqListFemale = rqService.loadByProjectAndGender(project, Gender.FEMALE);
		
		RatingQuestionEvaluator evaluator = new RatingQuestionEvaluator();
		evaluator.aggregateGenders(rqList, rqListMale, rqListFemale);
		
		setResponseHeader(response, "Feedback.csv");

		ICSVExporter exporter = new RatingQuestionCSVExporter();
	    exporter.writeCSV(rqList, response.getWriter());
	}
	
	@GetMapping(ApplicationPaths.URL_ADMIN_CSVFEEDBACK + "{idProject}" + ApplicationPaths.URL_DELIMETER + "{gender}")
	@ResponseBody
	public void genderSpecificFeedbackToCSV(HttpServletResponse response, @PathVariable("idProject") Long idProject, @PathVariable("gender") Gender gender) throws IOException {
		
		setResponseHeader(response, String.format("Feedback_%s.csv", gender.toString()));
		Project project = pService.findProject(idProject);

		List<RatingQuestion> rqList = rqService.loadByProjectAndGender(project, gender);
		
		ICSVExporter exporter = new RatingQuestionCSVExporter();
		exporter.writeCSV(rqList, response.getWriter());
	}
	
	@GetMapping(ApplicationPaths.URL_ADMIN_CSVFEEDBACK_FREETEXT + "{idProject}")
	@ResponseBody
	public void feedbackFreeTextToCSV(HttpServletResponse response, @PathVariable("idProject") Long idProject) throws IOException {
			
		//TODO validate project
		Project project = pService.findProject(idProject);
		String projectName = project.getProjectName();
		
		if(! project.isAskFreetext()) {
			log.error("Project name='{}' is not configured for free text. -> sending 404", projectName);
			response.sendError(404, String.format("Projekt %s ist nicht für Freitext konfiguriert", projectName));	
		}
		
		List<ParticipationResult> prList = prService.findAllByProject(project);
		
		setResponseHeader(response, "Feedback_Freitext.csv");
		
		ICSVExporter exporter = new FreeTextRecordCSVExporter();
		exporter.writeCSV(prList, response.getWriter());
	}
	
	private void setResponseHeader(HttpServletResponse response, String filename) {
		response.setHeader("Content-Disposition", String.format("attachment; filename=%s", filename));
	    response.setContentType("text/csv");		
	}
}