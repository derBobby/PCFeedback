package eu.planlos.pcfeedback.controller.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.planlos.pcfeedback.constants.ApplicationPathHelper;
import eu.planlos.pcfeedback.model.Gender;
import eu.planlos.pcfeedback.model.db.Participant;
import eu.planlos.pcfeedback.model.db.ParticipationResult;
import eu.planlos.pcfeedback.model.db.Project;
import eu.planlos.pcfeedback.model.db.RatingQuestion;
import eu.planlos.pcfeedback.service.ParticipantService;
import eu.planlos.pcfeedback.service.ParticipationResultService;
import eu.planlos.pcfeedback.service.ProjectService;
import eu.planlos.pcfeedback.service.RatingQuestionEvaluator;
import eu.planlos.pcfeedback.service.RatingQuestionService;
import eu.planlos.pcfeedback.service.csv.FreeTextRecordCSVExporter;
import eu.planlos.pcfeedback.service.csv.ICSVExporter;
import eu.planlos.pcfeedback.service.csv.ParticipantCSVExporter;
import eu.planlos.pcfeedback.service.csv.RatingQuestionCSVExporter;

@Controller
public class CSVDownloadController {

	private static final Logger LOG = LoggerFactory.getLogger(CSVDownloadController.class);
	
	@Autowired
	private ProjectService pService;
	
	@Autowired
	private ParticipantService participantService;

	@Autowired
	private RatingQuestionService rqService;
	
	@Autowired
	private ParticipationResultService prService;

	@GetMapping(ApplicationPathHelper.URL_ADMIN_CSVPARTICIPANTS + "{idProject}")
	@ResponseBody
	public void participantsToCSV(HttpServletResponse response, @PathVariable("idProject") Long idProject) throws IOException {
		
	    Project project = pService.findProject(idProject);
	    List<Participant> pList = participantService.getAllParticipantsForProject(project);

	    setResponseHeader(response, "Teilnehmerliste.csv");
	    
	    ICSVExporter exporter = new ParticipantCSVExporter();
	    exporter.writeCSV(pList, response.getWriter());
	}

	@GetMapping(ApplicationPathHelper.URL_ADMIN_CSVFEEDBACK + "{idProject}")
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
	
	@GetMapping(ApplicationPathHelper.URL_ADMIN_CSVFEEDBACK + "{idProject}" + ApplicationPathHelper.URL_DELIMETER + "{gender}")
	@ResponseBody
	public void genderSpecificFeedbackToCSV(HttpServletResponse response, @PathVariable("idProject") Long idProject, @PathVariable("gender") Gender gender) throws IOException {
		
		setResponseHeader(response, String.format("Feedback_%s.csv", gender.toString()));
		Project project = pService.findProject(idProject);

		List<RatingQuestion> rqList = rqService.loadByProjectAndGender(project, gender);
		
		ICSVExporter exporter = new RatingQuestionCSVExporter();
		exporter.writeCSV(rqList, response.getWriter());
	}
	
	@GetMapping(ApplicationPathHelper.URL_ADMIN_CSVFEEDBACK_FREETEXT + "{idProject}")
	@ResponseBody
	public void feedbackFreeTextToCSV(HttpServletResponse response, @PathVariable("idProject") Long idProject) throws IOException {
			
		//TODO validate project
		Project project = pService.findProject(idProject);
		String projectName = project.getProjectName();
		
		if(! project.getAskFreetext()) {
			LOG.error("Project name='{}' is not configured for free text. -> sending 404", projectName);
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