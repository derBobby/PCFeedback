package eu.planlos.pcfeedback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eu.planlos.pcfeedback.constants.ApplicationProfileHelper;
import eu.planlos.pcfeedback.model.Gender;
import eu.planlos.pcfeedback.service.DataCreationService;

@Component
@Profile(value = {ApplicationProfileHelper.DEV_PROFILE, ApplicationProfileHelper.REV_PROFILE})
public class DemoDataCreaterApplication implements ApplicationRunner {

	private static final Logger LOG = LoggerFactory.getLogger(DemoDataCreaterApplication.class);
	
	@Autowired
	private DataCreationService dcService;

	@Value("${eu.planlos.pcfeedback.question-count}")
	private int neededQuestionCount;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		LOG.debug("Initializing database");
		initDB();
		LOG.debug("Initializing database ... DONE");
	}
	
	//TODO MONGO Transactional working?
	@Transactional
	private void initDB() throws Exception {
		dcService.createCommon();
		dcService.createFreeText(Gender.MALE, 5);
		dcService.createFreeText(Gender.FEMALE, 5);
		dcService.createParticipations(Gender.MALE, 4);
		dcService.createParticipations(Gender.FEMALE, 2);
	}

}