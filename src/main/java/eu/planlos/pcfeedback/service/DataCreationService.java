package eu.planlos.pcfeedback.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import eu.planlos.pcfeedback.exceptions.ParticipantAlreadyExistingException;
import eu.planlos.pcfeedback.exceptions.RatingQuestionsNotExistentException;
import eu.planlos.pcfeedback.exceptions.UiTextException;
import eu.planlos.pcfeedback.exceptions.WrongRatingQuestionCountExistingException;
import eu.planlos.pcfeedback.model.Gender;
import eu.planlos.pcfeedback.model.Participant;
import eu.planlos.pcfeedback.model.ParticipationResult;
import eu.planlos.pcfeedback.model.Project;
import eu.planlos.pcfeedback.model.RatingObject;
import eu.planlos.pcfeedback.model.RatingQuestion;
import eu.planlos.pcfeedback.model.UiTextKey;

@Service
public class DataCreationService {

	private static final Logger LOG = LoggerFactory.getLogger(DataCreationService.class);

	@Value("${eu.planlos.pcfeedback.question-count}")
	private int neededQuestionCount;
	
	@Autowired
	private RatingObjectService roService;

	@Autowired
	private RatingQuestionService rqService;

	@Autowired
	private UiTextService uiTextService;
	
	@Autowired
	private ParticipantService participantService;

	@Autowired
	private ParticipationResultService participationResultService;
	
	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private FreeTextService fts;
	
	@PostConstruct
	private void createSampleData() throws WrongRatingQuestionCountExistingException, UiTextException,
			RatingQuestionsNotExistentException, ParticipantAlreadyExistingException {
		
		LOG.debug("Initializing database");

		Project project = new Project("Demo-Projekt");
		projectService.save(project);
		
		//Create RatingQuestions and check if enough in method are created.
		createRatingQuestions(project);		
		
		//Throws Exception if not
		rqService.checkEnoughRatingQuestions(project, false);
		
		//Create UiTexts and check if enough in method are created.
		createUiText(project);
		
		//Throws Exception if not
		uiTextService.checkEnoughUiTexts(project, false);
		
		createFreeText(project, Gender.MALE, 10);
		createFreeText(project, Gender.FEMALE, 10);
		createParticipations(project, Gender.MALE, 10);
		createParticipations(project, Gender.FEMALE, 10);
		
		LOG.debug("Initializing database ... DONE");

	}

	private void createRatingQuestions(Project project) {

		// ~~~~~~~~~~~~ RO ~~~~~~~~~~~~
		
		List<RatingObject> roList = new ArrayList<>();
		roList.add(new RatingObject("gute Zeit mit Freunden haben"));
		roList.add(new RatingObject("guter Referent im Opening"));
		roList.add(new RatingObject("gutes Programm im Opening"));
		roList.add(new RatingObject("gute \"Zeitraum\" Leute, die für mich beten oder mir zuhören"));
		roList.add(new RatingObject("gute kreative Aktionen die Nacht über"));
		roList.add(new RatingObject("gute actionreiche Aktionen die Nacht über"));
		roList.add(new RatingObject("neue Aktionen die Nacht über"));
		roList.add(new RatingObject("coole Stationsmitarbeiter"));
		roList.add(new RatingObject("gute Band"));
		roList.add(new RatingObject("gute Seminare"));
		roList.add(new RatingObject("gute chillige Aktionen die Nacht über"));
		roList.add(new RatingObject("gutes Essen / guter Megabrunch"));
		roList.add(new RatingObject("neue Leute kennenlernen"));
		roList.add(new RatingObject("gute Zeit mit meinen Teenkreis Mitarbeitern haben"));		
		roService.saveAll(roList);
		
		LOG.debug("Demo data created: rating objects");

		// ~~~~~~~~~~~~ RQ ~~~~~~~~~~~~
		
		List<RatingQuestion> rqList = new ArrayList<>();
		rqList.addAll(rqService.create(project, roList));
		rqService.saveAll(rqList);		
		
		LOG.debug("Demo data created: rating questions");	
	}

	private void createUiText(Project project) {
		
		// ~~~~~~~~~~~~~~~~ Preparing empty texts ~~~~~~~~~~~~~~~~
		
		uiTextService.initializeUiText(project);
		
		// ~~~~~~~~~~~~~~~~ General texts ~~~~~~~~~~~~~~~~
		
		uiTextService.createText(
				project,
				UiTextKey.MSG_HOME,
				"Begrüßung",
				"Gib uns Feedback und hilf uns die Teennight noch besser zu machen. Unter den Teilnehmern verlosen wir zwei Freiplätze für die Teennight 2020!"
			);
		uiTextService.createText(
				project,
				UiTextKey.MSG_PRICEGAME,
				"Gewinnspielhinweise",
				priceGameStatement()
			);
		
		// ~~~~~~~~~~~~~~~~ Feedback texts ~~~~~~~~~~~~~~~~
		
		uiTextService.createText(
				project,
				UiTextKey.MSG_FEEDBACK_START,
				"Formular",
				"Nach der Teennight werden deine Daten gelöscht!"
			);
		uiTextService.createText(
				project,
				UiTextKey.MSG_FEEDBACK_QUESTION,
				"Fragestellung",
				"Was ist dir während der Teennight <u>wichtiger</u>?"
			);
		uiTextService.createText(
				project,
				UiTextKey.MSG_FEEDBACK_FREETEXT,
				"Freitext",
				"HIER IST PLATZ FÜR ALLES WAS DU SAGEN WILLST:<br>" +
						"- das feier ich an der Teennight<br>" +
						"- das könntet ihr noch besser machen<br>" +
						"- das wäre mal eine Idee für die Teennight<br>" + 
						"- das habe ich mit Gott erlebt auf der Teennight<br>"
			);
		uiTextService.createText(
				project,
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

	public void createParticipations(Project project, Gender gender, int count) throws RatingQuestionsNotExistentException, ParticipantAlreadyExistingException {
		
		int localCount = count;
		
		while(localCount != 0) {
			
			localCount--;
			
			// Create and save Participant itself
			Participant participant = participantService.createParticipantForDB(project, gender);
			participantService.save(participant);
			
			// Create and save ParticipationResult
			Map<Long, Integer> feedbackMap = new HashMap<>();
			List<RatingQuestion> ratingQuestions = new ArrayList<>();
			rqService.addRatingQuestionsForProjectAndGenderToList(ratingQuestions, project, gender);
			for(RatingQuestion ratingQuestion : ratingQuestions) {
				long id = ratingQuestion.getIdRatingQuestion();
				feedbackMap.put(id, gender.equals(Gender.MALE) ? 1 : 2);
			}
			ParticipationResult pResult = new ParticipationResult(participant, feedbackMap);
			participationResultService.saveParticipationResult(pResult);
			
			// Update RatingQuestion
			rqService.saveFeedback(feedbackMap);
		}
	}

	/**
	 * Creates given count of free text submissions in DB
	 * @param gender the gender to use for the free text elements
	 * @param count the count of free text elements to create
	 */
	public void createFreeText(Project project, Gender gender, int count) {

		int localCount = count;
		
		while(localCount != 0) {
			localCount--;			
			fts.createFreeText(project, ((Long) System.currentTimeMillis()).toString(), gender);
		}
	}
}
