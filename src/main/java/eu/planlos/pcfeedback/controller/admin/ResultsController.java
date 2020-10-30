package eu.planlos.pcfeedback.controller.admin;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.planlos.pcfeedback.constants.ApplicationPathHelper;
import eu.planlos.pcfeedback.exceptions.RatingQuestionsNotExistentException;
import eu.planlos.pcfeedback.model.Gender;
import eu.planlos.pcfeedback.model.db.FreeText;
import eu.planlos.pcfeedback.model.db.Participant;
import eu.planlos.pcfeedback.model.db.Project;
import eu.planlos.pcfeedback.model.db.RatingQuestion;
import eu.planlos.pcfeedback.service.FreeTextService;
import eu.planlos.pcfeedback.service.ModelFillerService;
import eu.planlos.pcfeedback.service.ParticipantService;
import eu.planlos.pcfeedback.service.ProjectService;
import eu.planlos.pcfeedback.service.RatingQuestionService;

@Controller
public class ResultsController {

	private static final Logger LOG = LoggerFactory.getLogger(ResultsController.class);

	@Autowired
	private ParticipantService pService;
	
	@Autowired
	private RatingQuestionService rqService;

	@Autowired
	private FreeTextService ftService;
	
	@Autowired
	private ProjectService prService;
	
	@Autowired
	private ModelFillerService mfs;
	
	@RequestMapping(path = ApplicationPathHelper.URL_ADMIN_SHOWFEEDBACK + "{projectName}", method = RequestMethod.GET)
	public String showResults(@PathVariable(name = "projectName") String projectName, Model model) throws RatingQuestionsNotExistentException {

		Project project = prService.findProject(projectName);
		if(project == null) {
			//TODO real handling 
			return "FUCK";
		}
		
		LOG.debug("Loading random participants");
		List<Participant> randomParticipantList = pService.getRandomWinnerParticipantsForProject(project);
		
		LOG.debug("Loading participants");
		List<Participant> participantList = pService.getAllParticipantsForProject(project);
		
		LOG.debug("Loading rating questions for male participants");
		List<RatingQuestion> rqListMale = rqService.loadByProjectAndGender(project, Gender.MALE);

		LOG.debug("Loading rating questions for female participants");
		List<RatingQuestion> rqListFemale = rqService.loadByProjectAndGender(project, Gender.FEMALE);
		
		LOG.debug("Loading rating questions for female participants");
		List<FreeText> freeTextList = ftService.findAllByProject(project);
		
		mfs.fillGlobal(model);
		mfs.fillResults(model, randomParticipantList, participantList, rqListMale, rqListFemale, freeTextList);
		
		return ApplicationPathHelper.RES_ADMIN_SHOWFEEDBACK;
	}
}
