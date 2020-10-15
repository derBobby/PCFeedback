package eu.planlos.pcfeedback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eu.planlos.pcfeedback.constants.ApplicationProfileHelper;
import eu.planlos.pcfeedback.exceptions.UiTextException;
import eu.planlos.pcfeedback.exceptions.WrongRatingQuestionCountExistingException;
import eu.planlos.pcfeedback.service.DataCreationService;

@Component
@Profile(value = ApplicationProfileHelper.PROD_PROFILE)
public class ProdDataCreaterApplication implements ApplicationRunner {

	private static final Logger LOG = LoggerFactory.getLogger(ProdDataCreaterApplication.class);

	@Autowired
	private DataCreationService dcs;
	
	@Override
	public void run(ApplicationArguments args) throws UiTextException, WrongRatingQuestionCountExistingException {
		
		if(dcs.isProdDataAlreadyCreated()) {
			LOG.debug("No db init necessary. Already rating questions existing");
			return;
		}
		
		LOG.debug("Initializing database");
		initDB();
		LOG.debug("Initializing database ... DONE");
	}

	//TODO MONGO Transactional working?
	@Transactional
	private void initDB() throws UiTextException, WrongRatingQuestionCountExistingException {
		dcs.createCommon();
	}
}