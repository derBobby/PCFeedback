package eu.planlos.pcfeedback.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.planlos.pcfeedback.exceptions.UiTextException;
import eu.planlos.pcfeedback.model.Gender;
import eu.planlos.pcfeedback.model.Participant;
import eu.planlos.pcfeedback.model.ParticipationResult;
import eu.planlos.pcfeedback.model.RatingObject;
import eu.planlos.pcfeedback.model.RatingQuestion;
import eu.planlos.pcfeedback.model.UiTextKey;

@Service
public class DataCreationService {

	private static final Logger logger = LoggerFactory.getLogger(DataCreationService.class);
	
	@Autowired
	private RatingObjectService ros;

	@Autowired
	private RatingQuestionService rqs;

	@Autowired
	private UiTextService uts;
	
	@Autowired
	private ParticipantService ps;
	
	@Autowired
	private ParticipationResultService prs;
	
	public void createCommon() throws UiTextException {
		
		createRatingQuestions();
		createUiText();
	}

	private void createRatingQuestions() {
		
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

		logger.debug("Create rating question sample data");
		List<RatingQuestion> rqList = new ArrayList<>();
		rqList.addAll(rqs.create(roList));

		for(RatingQuestion rq : rqList) {
			rq.setVotesOne(0);
		}
		
		logger.debug("Saving rating question sample data");
		rqs.saveAll(rqList);		
	}

	private void createUiText() throws UiTextException {
		
		uts.initializeUiText();
		
		uts.createText(
				UiTextKey.MSG_HOME,
				"Begrüßung",
				"Gib uns Feedback und hilf uns die Teennight noch besser zu machen. Unter den Teilnehmern verlosen wir zwei Freiplätze für die Teennight 2020!"
			);
		uts.createText(
				UiTextKey.MSG_FEEDBACKSTART,
				"Formular",
				"Nach der Teennight werden deine Daten gelöscht!"
			);
		uts.createText(
				UiTextKey.MSG_FEEDBACKQUESTION,
				"Fragestellung",
				"Was ist dir während der Teennight <u>wichtiger</u>?"
			);
		uts.createText(
				UiTextKey.MSG_FEEDBACKEND,
				"Ende",
				"Die Teennight sagt danke! Dein Feedback hilft uns sehr. Du bist jetzt im Lostopf fürs Closing."
			);
		
		if(! uts.isFullyInitialized()) {
			throw new UiTextException("Es wurden nicht für jedes UiTextField ein Element initialisiert oder der zugehörige Text fehlt.");
		}
	}

	public void createParticipations(Gender gender, int count) throws Exception {
		
		while(count-- != 0) {
			
			// Create and save Participant itself
			Participant participant = ps.createParticipantForDB(gender);
			ps.save(participant);
			Thread.sleep(1);
			
			// Create and save ParticipationResult
			Map<Long, Integer> feedbackMap = new HashMap<>();
			List<RatingQuestion> ratingQuestions = new ArrayList<>();
			rqs.addRatingQuestionsForGenderToList(ratingQuestions, gender);
			for(RatingQuestion rq : ratingQuestions) {
				long id = rq.getIdRatingQuestion();
				feedbackMap.put(id, gender.equals(Gender.MALE) ? 1 : 2);
			}
			ParticipationResult pr = new ParticipationResult(participant, feedbackMap);
			prs.saveParticipationResult(pr);
			
			// Update RatingQuestion
			rqs.saveFeedback(feedbackMap);
		}
	}

	public boolean isDataAlreadyCreated() {
		
		if(rqs.loadByGender(Gender.MALE).size() == 0) {
			return false;
		}
		return true;
	}
}
