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
import eu.planlos.pcfeedback.service.DataCreationService;

@Component
@Profile(value = {ApplicationProfile.DEV_PROFILE, ApplicationProfile.REV_PROFILE})
public class DemoDataCreaterApplication implements ApplicationRunner {

	private static final Logger logger = LoggerFactory.getLogger(DemoDataCreaterApplication.class);
	
	@Autowired
	private DataCreationService dcs;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		logger.debug("Initializing database");
		initDB();
		logger.debug("Initializing database ... DONE");
	}
	
	//TODO does this work? :D
	@Transactional
	private void initDB() throws Exception {
		dcs.createCommon();
		dcs.createParticipations(Gender.MALE, 1);
		dcs.createParticipations(Gender.FEMALE, 1);
	}

}