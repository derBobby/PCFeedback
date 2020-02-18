package eu.planlos.pcfeedback.controller.admin;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.planlos.pcfeedback.constants.ApplicationPath;
import eu.planlos.pcfeedback.exceptions.RatingQuestionsNotExistentException;
import eu.planlos.pcfeedback.model.Gender;
import eu.planlos.pcfeedback.model.Participant;
import eu.planlos.pcfeedback.model.RatingQuestion;
import eu.planlos.pcfeedback.service.ModelFillerService;
import eu.planlos.pcfeedback.service.ParticipantService;
import eu.planlos.pcfeedback.service.RatingQuestionService;

@Controller
public class ResultsController {

	private static final Logger LOG = LoggerFactory.getLogger(ResultsController.class);

	@Autowired
	private ParticipantService pService;
	
	@Autowired
	private RatingQuestionService rqService;
	
	@Autowired
	private ModelFillerService mfs;
	
	@RequestMapping(path = ApplicationPath.URL_ADMIN_SHOWFEEDBACK, method = RequestMethod.GET)
	public String showResults(Model model) throws RatingQuestionsNotExistentException {

		LOG.debug("Loading random participants");
		List<Participant> randomParticipantList = pService.getRandomWinnerParticipants();
		
		LOG.debug("Loading participants");
		List<Participant> participantList = pService.getAllParticipants();
		
		LOG.debug("Loading rating questions for male participants");
		List<RatingQuestion> rqListMale = rqService.loadByGender(Gender.MALE);
		
		LOG.debug("Loading rating questions for female participants");
		List<RatingQuestion> rqListFemale = rqService.loadByGender(Gender.FEMALE);
		
		mfs.fillGlobal(model);
		mfs.fillResults(model, randomParticipantList, participantList, rqListMale, rqListFemale);
		
		return ApplicationPath.RES_ADMIN_SHOWFEEDBACK;
	}
}
