package eu.planlos.pcfeedback.controller.admin;

import eu.planlos.pcfeedback.constants.ApplicationPaths;
import eu.planlos.pcfeedback.exceptions.RatingQuestionsNotExistentException;
import eu.planlos.pcfeedback.model.Gender;
import eu.planlos.pcfeedback.model.db.*;
import eu.planlos.pcfeedback.service.*;
import eu.planlos.pcfeedback.util.RatingQuestionEvaluator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class ResultsController {

	private final ParticipantService pService;
	private final RatingQuestionService rqService;
	private final ProjectService psService;
	private final ParticipationResultService prService;
	private final ModelFillerService mfs;
	
	public ResultsController(ParticipantService pService, RatingQuestionService rqService,
			ProjectService psService, ParticipationResultService prService,
			ModelFillerService mfs) {
		this.pService = pService;
		this.rqService = rqService;
		this.psService = psService;
		this.prService = prService;
		this.mfs = mfs;
	}
	
	@RequestMapping(path = ApplicationPaths.URL_ADMIN_SHOWFEEDBACK + "{projectName}", method = RequestMethod.GET)
	public String showResults(ServletResponse response, @PathVariable(name = "projectName") String projectName, Model model) throws RatingQuestionsNotExistentException, IOException {

		HttpServletResponse res = (HttpServletResponse) response;

		Project project = psService.findProject(projectName);
		if(project == null) {
			log.error("Project name='{}' does not exist -> sending 400", projectName);
			res.sendError(404, "Kein Projekt angegeben");
			return null;
		}
		
		RatingQuestionEvaluator evaluator = new RatingQuestionEvaluator();
		
		log.debug("Loading random participants");
		List<Participant> randomParticipantList = pService.getRandomWinnerParticipantsForProject(project);
		
		log.debug("Loading participants");
		List<Participant> participantList = pService.getAllParticipantsForProject(project);
		
		log.debug("Loading rating questions for male participants");
		List<RatingQuestion> rqListMale = rqService.loadByProjectAndGender(project, Gender.MALE);

		log.debug("Loading rating questions for female participants");
		List<RatingQuestion> rqListFemale = rqService.loadByProjectAndGender(project, Gender.FEMALE);
		
		log.debug("Loading free texts");
		List<ParticipationResult> prList = prService.findAllByProject(project);

		log.debug("Creating results");
		Map<RatingObject, BigDecimal> maleResultMap = evaluator.rateWithGender(rqListMale);

		log.debug("Creating results");
		Map<RatingObject, BigDecimal> femaleResultMap = evaluator.rateWithGender(rqListFemale);
		
		log.debug("Creating results");
		Map<RatingObject, BigDecimal> overallResultMap = evaluator.rateWithoutGender(rqListMale, rqListFemale);
		
		mfs.fillGlobal(model);
		mfs.fillResults(model, project, randomParticipantList, participantList, rqListMale, rqListFemale, prList, maleResultMap, femaleResultMap, overallResultMap);
		
		return ApplicationPaths.RES_ADMIN_SHOWFEEDBACK;
	}
}
