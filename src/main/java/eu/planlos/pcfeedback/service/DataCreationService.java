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

	private static final Logger LOG = LoggerFactory.getLogger(DataCreationService.class);
	
	@Autowired
	private RatingObjectService roService;

	@Autowired
	private RatingQuestionService rqService;

	@Autowired
	private UiTextService uiTextService;
	
	@Autowired
	private ParticipantService pService;
	
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
		
		LOG.debug("Saving rating object sample data");
		roService.saveAll(roList);

		LOG.debug("Create rating question sample data");
		List<RatingQuestion> rqList = new ArrayList<>();
		rqList.addAll(rqService.create(roList));

		for(RatingQuestion rq : rqList) {
			rq.setVotesOne(0);
		}
		
		LOG.debug("Saving rating question sample data");
		rqService.saveAll(rqList);		
	}

	private void createUiText() throws UiTextException {
		
		uiTextService.initializeUiText();
		
		uiTextService.createText(
				UiTextKey.MSG_HOME,
				"Begrüßung",
				"Gib uns Feedback und hilf uns die Teennight noch besser zu machen. Unter den Teilnehmern verlosen wir zwei Freiplätze für die Teennight 2020!"
			);
		uiTextService.createText(
				UiTextKey.MSG_FEEDBACKSTART,
				"Formular",
				"Nach der Teennight werden deine Daten gelöscht!"
			);
		uiTextService.createText(
				UiTextKey.MSG_FEEDBACKQUESTION,
				"Fragestellung",
				"Was ist dir während der Teennight <u>wichtiger</u>?"
			);
		uiTextService.createText(
				UiTextKey.MSG_FEEDBACKEND,
				"Ende",
				"Die Teennight sagt danke! Dein Feedback hilft uns sehr. Du bist jetzt im Lostopf fürs Closing."
			);
		
		if(! uiTextService.isFullyInitialized()) {
			throw new UiTextException("Es wurden nicht für jedes UiTextField ein Element initialisiert oder der zugehörige Text fehlt.");
		}
	}

	public void createParticipations(Gender gender, int count) throws Exception {
		
		int localCount = count;
		
		while(localCount != 0) {
			
			localCount--;
			
			// Create and save Participant itself
			Participant participant = pService.createParticipantForDB(gender);
			pService.save(participant);
			
			// Create and save ParticipationResult
			Map<Long, Integer> feedbackMap = new HashMap<>();
			List<RatingQuestion> ratingQuestions = new ArrayList<>();
			rqService.addRatingQuestionsForGenderToList(ratingQuestions, gender);
			for(RatingQuestion ratingQuestion : ratingQuestions) {
				long id = ratingQuestion.getIdRatingQuestion();
				feedbackMap.put(id, gender.equals(Gender.MALE) ? 1 : 2);
			}
			ParticipationResult pResult = new ParticipationResult(participant, feedbackMap);
			prs.saveParticipationResult(pResult);
			
			// Update RatingQuestion
			rqService.saveFeedback(feedbackMap);
		}
	}

	public boolean enoughRatingQuestionsExisting(int neededQuestionCount) {
		return rqService.enoughRatingQuestionsExisting(neededQuestionCount);
	}
	
	public boolean isDataAlreadyCreated() {
		return rqService.loadByGender(Gender.MALE).size() == 0 ? false : true;
	}
}
