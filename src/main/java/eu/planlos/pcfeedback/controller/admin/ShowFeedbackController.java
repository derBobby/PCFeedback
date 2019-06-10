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
import eu.planlos.pcfeedback.model.RatingQuestion;
import eu.planlos.pcfeedback.service.ModelFillerService;
import eu.planlos.pcfeedback.service.RatingQuestionService;

@Controller
public class ShowFeedbackController {

	private static final Logger logger = LoggerFactory.getLogger(ShowFeedbackController.class);
	
	@Autowired
	private RatingQuestionService rqs;
	
	@Autowired
	private ModelFillerService mfs;
	
	//TODO AUTH
	@RequestMapping(path = ApplicationPath.URL_ADMIN_EXPORTFEEDBACK, method = RequestMethod.GET)
	public String showFeedback(Model model) throws RatingQuestionsNotExistentException {

		logger.debug("Loading rating questions for male participants");
		List<RatingQuestion> rqListMale = rqs.loadByGender(Gender.MALE);
		
		logger.debug("Loading rating questions for female participants");
		List<RatingQuestion> rqListFemale = rqs.loadByGender(Gender.FEMALE);

		for(RatingQuestion rq : rqListMale) {
			System.out.println(rq.getGender() + ": " + rq.getObjectOne().toString() + " - " + rq.getObjectTwo().toString());
		}
		for(RatingQuestion rq : rqListFemale) {
			System.out.println(rq.getGender() + ": " + rq.getObjectOne().toString() + " - " + rq.getObjectTwo().toString());
		}
		
		mfs.fillGlobal(model);
		mfs.fillExport(model, rqListMale, rqListFemale);
		
		return ApplicationPath.RES_ADMIN_EXPORT;
	}
}
