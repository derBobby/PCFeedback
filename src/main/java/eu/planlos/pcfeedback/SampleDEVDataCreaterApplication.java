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
import eu.planlos.pcfeedback.service.SampleDataCreationService;

@Component
@Profile(value = ApplicationProfile.DEV_PROFILE)
public class SampleDEVDataCreaterApplication implements ApplicationRunner {

	private static final Logger logger = LoggerFactory.getLogger(SampleDEVDataCreaterApplication.class);
	
	@Autowired
	private SampleDataCreationService sdcs;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		logger.debug("Initializing database");
		initDB();
		logger.debug("Initializing database ... DONE");
	}
	
	//TODO does this work? :D
	@Transactional
	private void initDB() throws Exception {

		sdcs.createCommon();
		sdcs.createParticipants();		
	}

}