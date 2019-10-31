package eu.planlos.pcfeedback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eu.planlos.pcfeedback.constants.ApplicationProfile;
import eu.planlos.pcfeedback.model.Gender;
import eu.planlos.pcfeedback.service.RatingQuestionService;
import eu.planlos.pcfeedback.service.SampleDataCreationService;

@Component
@Profile(value = ApplicationProfile.PROD_PROFILE)
public class SamplePRODDataCreaterApplication implements ApplicationRunner {

	private static final Logger logger = LoggerFactory.getLogger(SamplePRODDataCreaterApplication.class);

	@Autowired
	private RatingQuestionService rqs;

	@Autowired
	private SampleDataCreationService sdcs;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		if(rqs.loadByGender(Gender.MALE).size() == 0) {
			logger.debug("Initializing database");
			initDB();
			logger.debug("Initializing database ... DONE");
			return;
		}
		logger.debug("No db init necessary. Already rating questions existing");
	}

	//TODO does this work? :D
	@Transactional
	private void initDB() throws Exception {

		sdcs.createCommon();
	}
}