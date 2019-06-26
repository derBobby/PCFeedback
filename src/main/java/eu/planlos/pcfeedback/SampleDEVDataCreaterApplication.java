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
import eu.planlos.pcfeedback.exceptions.ParticipantAlreadyExistingException;
import eu.planlos.pcfeedback.exceptions.RatingQuestionsNotExistentException;
import eu.planlos.pcfeedback.model.Gender;
import eu.planlos.pcfeedback.model.Participant;
import eu.planlos.pcfeedback.model.RatingObject;
import eu.planlos.pcfeedback.model.RatingQuestion;
import eu.planlos.pcfeedback.service.ParticipantService;
import eu.planlos.pcfeedback.service.RatingObjectService;
import eu.planlos.pcfeedback.service.RatingQuestionService;

@Component
@Profile(value = ApplicationProfile.DEV_PROFILE)
public class SampleDEVDataCreaterApplication implements ApplicationRunner {

	private static final Logger logger = LoggerFactory.getLogger(SampleDEVDataCreaterApplication.class);

	@Autowired
	private RatingQuestionService rqs;

	@Autowired
	private RatingObjectService ros;
	
	@Autowired
	private ParticipantService ps;

	@Override
	public void run(ApplicationArguments args) throws RatingQuestionsNotExistentException, ParticipantAlreadyExistingException, InterruptedException {

		initDB();
	}

	//TODO does this work? :D
	@Transactional
	private void initDB() throws RatingQuestionsNotExistentException, ParticipantAlreadyExistingException, InterruptedException {

		/*
		 * CREATE
		 */
		RatingObject ro01 = new RatingObject("gute Zeit mit Freunden haben");
		RatingObject ro02 = new RatingObject("guter Referent im Opening");
		RatingObject ro03 = new RatingObject("gutes Programm im Opening");
		RatingObject ro04 = new RatingObject("gute \"Zeitraum\" Leute, die für mich beten oder mir zuhören");
		RatingObject ro05 = new RatingObject("gute kreative Aktionen die Nacht über");
		RatingObject ro06 = new RatingObject("gute actionreiche Aktionen die Nacht über");
		RatingObject ro07 = new RatingObject("neue Aktionen die Nacht über");
		RatingObject ro08 = new RatingObject("coole Stationsmitarbeiter");
		RatingObject ro09 = new RatingObject("gute Band");
		RatingObject ro10 = new RatingObject("gute Seminare");
		RatingObject ro11 = new RatingObject("gute chillige Aktionen die Nacht über");
		RatingObject ro12 = new RatingObject("gutes Essen / guter Megabrunch");
		RatingObject ro13 = new RatingObject("neue Leute kennenlernen");
		RatingObject ro14 = new RatingObject("gute Zeit mit meinen Teenkreis Mitarbeitern haben");

		List<RatingObject> roList = new ArrayList<>();
		roList.add(ro01);
		roList.add(ro02);
		roList.add(ro03);
		roList.add(ro04);
		roList.add(ro05);
		roList.add(ro06);
		roList.add(ro07);
		roList.add(ro08);
		roList.add(ro09);
		roList.add(ro10);
		roList.add(ro11);
		roList.add(ro12);
		roList.add(ro13);
		roList.add(ro14);
		
		logger.debug("Saving rating object sample data");
		ros.saveAll(roList);

		List<RatingQuestion> rqList = new ArrayList<>();
		rqList.addAll(rqs.create(roList));

		for(RatingQuestion rq : rqList) {
			rq.setVotesOne(2);
		}
		
		logger.debug("Saving rating question sample data");
		rqs.saveAll(rqList);

		int femaleCount = 1;
		int maleCount = 3;

		while(maleCount-- != 0) {
			Participant participantM = ps.createParticipantForDB(Gender.MALE);
			ps.save(participantM);
			Thread.sleep(1);
		}
		
		while(femaleCount-- != 0) {
			Participant participantW = ps.createParticipantForDB(Gender.FEMALE);
			ps.save(participantW);
			Thread.sleep(1);
		}
		
	}
}