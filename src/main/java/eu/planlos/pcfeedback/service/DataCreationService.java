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
import eu.planlos.pcfeedback.exceptions.WrongRatingQuestionCountExistingException;
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
	
	@Autowired
	private FreeTextService fts;
	
	/**
	 * Method creates data for all the stages and throws Exception if not enough UiTexts or RatingQuestions are created
	 * @throws UiTextException if not enough UiTexts are created in method
	 * @throws WrongRatingQuestionCountExistingException
	 */
	public void createCommon() throws UiTextException, WrongRatingQuestionCountExistingException {
		
		//Create RatingQuestions and check if enough in method are created.
		createRatingQuestions();
		//Throws Exception if not
		rqService.checkEnoughRatingQuestions(false);
		
		//Create UiTexts and check if enough in method are created.
		createUiText();
		//Throws Exception if not
		uiTextService.checkEnoughUiTexts(false);
	}

	/**
	 * Checks whether application is fully initialized for production system.
	 * Needed are enough RatingQuestions and initialized UiTexts.
	 * @return boolean
	 */
	public boolean isProdDataAlreadyCreated() {
		
		boolean result = true;
				
		try {
			uiTextService.checkEnoughUiTexts(true);
			rqService.checkEnoughRatingQuestions(true);
		} catch (UiTextException | WrongRatingQuestionCountExistingException e) {
			result = false;
		}
		
		return result;
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

	private void createUiText() {
		
		// ~~~~~~~~~~~~~~~~ Preparing texts ~~~~~~~~~~~~~~~~
		uiTextService.initializeUiText();
		
		// ~~~~~~~~~~~~~~~~ General texts ~~~~~~~~~~~~~~~~
		uiTextService.createText(
				UiTextKey.MSG_HOME,
				"Begrüßung",
				"Gib uns Feedback und hilf uns die Teennight noch besser zu machen. Unter den Teilnehmern verlosen wir zwei Freiplätze für die Teennight 2020!"
			);
		uiTextService.createText(
				UiTextKey.MSG_PRICEGAME,
				"Gewinnspielhinweise",
				priceGameStatement()
			);
		
		// ~~~~~~~~~~~~~~~~ Feedback texts ~~~~~~~~~~~~~~~~
		uiTextService.createText(
				UiTextKey.MSG_FEEDBACK_START,
				"Formular",
				"Nach der Teennight werden deine Daten gelöscht!"
			);
		uiTextService.createText(
				UiTextKey.MSG_FEEDBACK_QUESTION,
				"Fragestellung",
				"Was ist dir während der Teennight <u>wichtiger</u>?"
			);
		uiTextService.createText(
				UiTextKey.MSG_FEEDBACK_FREETEXT,
				"Freitext",
				"HIER IST PLATZ FÜR ALLES WAS DU SAGEN WILLST:<br>" +
						"- das feier ich an der Teennight<br>" +
						"- das könntet ihr noch besser machen<br>" +
						"- das wäre mal eine Idee für die Teennight<br>" + 
						"- das habe ich mit Gott erlebt auf der Teennight<br>"
			);
		uiTextService.createText(
				UiTextKey.MSG_FEEDBACK_END,
				"Ende",
				"Die Teennight sagt danke! Dein Feedback hilft uns sehr. Du bist jetzt im Lostopf fürs Closing."
			);
	}
	
	private String priceGameStatement() {
		return "<h3>Verlosung</h3>"
				+ "Die Teilnahme am Gewinnspiel ist möglich zwischen Ende des \"Opening\" und Beginn des \"Closing\" der Teennight.<br>"
				+ "Teilnahmeberechtigt sind alle Teilnehmer der Teennight. Mitarbeiter sind ?ausgeschlossen?.<br>"
				+ "Pro Teennight-Teilnehmer darf nur eine Teilnahme am Gewinnspiel erfolgen. Eine Mehrfachteilnahme führt zum Ausschluss.<br>"
				+ "Der oder die Gewinner wird oder werden aus allen Teilnehmenden zufällig vom Feedbacktool bestimmt.<br>"
				+ "Der Gewinner wird während des \"Closing\" auf der Bühne bekannt gegeben.<br>"
				+ "Dieser kann den Preis nach dem Closing am EC Merchandisingstand abholen.<br>" + "<br>"
				+ "<h3>Preis</h3>"
				+ "Der oder die Gewinner erhalten für das folgende Jahr eine Freikarte zur Teennight.<br>"
				+ "Der Preis kann nicht getauscht oder übertragen werden<br>" + "<br>"
				+ "<h3>Rechtliches</h3>"
				+ "Der Erwerb von Produkten oder Dienstleistungen hat keine Auwirkung auf das Gewinnspiel.<br>"
				+ "Der Teilnehmer erklärt sich damit einverstanden, dass sein Name beim \"Closing\" auf Bühne bekannt gegeben wird.<br>"
				+ "Es gelten die üblichen <a href=\"https://swdec.de/datenschutz\">Datenschutzhinweise</a> des SWD EC.<br>"
				+ "Der Rechtsweg ist ausgeschlossen.";
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

	/**
	 * Creates given count of free text submissions in DB
	 * @param gender the gender to use for the free text elements
	 * @param count the count of free text elements to create
	 */
	public void createFreeText(Gender gender, int count) {

		int localCount = count;
		
		while(localCount != 0) {
			localCount--;			
			fts.saveFreeText(((Long) System.currentTimeMillis()).toString(), gender);
		}
	}
}
