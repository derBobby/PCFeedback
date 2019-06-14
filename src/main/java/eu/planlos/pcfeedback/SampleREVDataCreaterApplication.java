package eu.planlos.pcfeedback;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eu.planlos.pcfeedback.constants.ApplicationProfile;
import eu.planlos.pcfeedback.exceptions.ParticipantAlreadyExistsException;
import eu.planlos.pcfeedback.exceptions.RatingQuestionsNotExistentException;
import eu.planlos.pcfeedback.model.Gender;
import eu.planlos.pcfeedback.model.Participant;
import eu.planlos.pcfeedback.model.RatingObject;
import eu.planlos.pcfeedback.model.RatingQuestion;
import eu.planlos.pcfeedback.service.ParticipantService;
import eu.planlos.pcfeedback.service.RatingObjectService;
import eu.planlos.pcfeedback.service.RatingQuestionService;

@Component
@Profile(value = ApplicationProfile.REV_PROFILE)
public class SampleREVDataCreaterApplication implements ApplicationRunner {

	private static final Logger logger = LoggerFactory.getLogger(SampleREVDataCreaterApplication.class);

	@Autowired
	private RatingQuestionService rqs;

	@Autowired
	private RatingObjectService ros;
	
	@Autowired
	private ParticipantService ps;

	@Override
	public void run(ApplicationArguments args) throws RatingQuestionsNotExistentException, ParticipantAlreadyExistsException {

		initDB();
	}

	//TODO does this work? :D
	@Transactional
	private void initDB() throws RatingQuestionsNotExistentException, ParticipantAlreadyExistsException {

		/*
		 * CREATE
		 */
		RatingObject ro1 = new RatingObject("Pizza");
		RatingObject ro2 = new RatingObject("Kaffee");
		RatingObject ro3 = new RatingObject("Auf's Maul");
		RatingObject ro4 = new RatingObject("Weltfrieden");

		List<RatingObject> roList = new ArrayList<>();
		roList.add(ro1);
		roList.add(ro2);
		roList.add(ro3);
		roList.add(ro4);
		
		logger.debug("Saving rating object sample data");
		ros.saveAll(roList);

		List<RatingQuestion> rqList = new ArrayList<>();
		rqList.addAll(rqs.create(roList));

		for(RatingQuestion rq : rqList) {
			rq.setVotesOne(2);
		}
		
		logger.debug("Saving rating question sample data");
		rqs.saveAll(rqList);

		Participant participantM = new Participant("Ein", "Typ", "typ@example.com", "000000000", Gender.MALE);
		ps.save(participantM);
		
		Participant participantW = new Participant("Eine", "Typin", "typin@example.com", "1111111111", Gender.FEMALE);
		ps.save(participantW);
	}
}