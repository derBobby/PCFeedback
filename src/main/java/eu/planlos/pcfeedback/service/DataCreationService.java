package eu.planlos.pcfeedback.service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import eu.planlos.pcfeedback.exceptions.DuplicateRatingObjectException;
import eu.planlos.pcfeedback.exceptions.ParticipantAlreadyExistingException;
import eu.planlos.pcfeedback.exceptions.ProjectAlreadyExistingException;
import eu.planlos.pcfeedback.exceptions.RatingQuestionsNotExistentException;
import eu.planlos.pcfeedback.exceptions.UiTextException;
import eu.planlos.pcfeedback.exceptions.WrongRatingQuestionCountExistingException;
import eu.planlos.pcfeedback.model.Gender;
import eu.planlos.pcfeedback.model.UiTextKey;
import eu.planlos.pcfeedback.model.db.Participant;
import eu.planlos.pcfeedback.model.db.ParticipationResult;
import eu.planlos.pcfeedback.model.db.Project;
import eu.planlos.pcfeedback.model.db.RatingObject;
import eu.planlos.pcfeedback.model.db.RatingQuestion;
import eu.planlos.pcfeedback.util.ZonedDateTimeUtility;

@Slf4j
@Service
@Profile({"DEV", "REV"})
public class DataCreationService {

	private RatingObjectService roService;
	private RatingQuestionService rqService;
	private UiTextService uiTextService;
	private ParticipantService participantService;
	private ParticipationResultService participationResultService;
	private ProjectService projectService;
	
	public DataCreationService(RatingObjectService roService, RatingQuestionService rqService,
			UiTextService uiTextService, ParticipantService participantService,
			ParticipationResultService participationResultService, ProjectService projectService) {
		this.roService = roService;
		this.rqService = rqService;
		this.uiTextService = uiTextService;
		this.participantService = participantService;
		this.participationResultService = participationResultService;
		this.projectService = projectService;
	}
	
	@PostConstruct
	private void initialize() throws WrongRatingQuestionCountExistingException, UiTextException,
			RatingQuestionsNotExistentException, ParticipantAlreadyExistingException, ProjectAlreadyExistingException, DuplicateRatingObjectException {
		
		log.debug("Initializing database");
		createSampleProject("Demo-Projekt", false, false, true);
		createSampleProject("Demo-Projekt 1", false, true, true);
		createSampleProject("Demo-Projekt 2", true, false, true);
		createSampleProject("Demo-Projekt 3", true, true, true);
//		createSampleProject("Demo-Projekt 4", true);
//		createSampleProject("Demo-Projekt 5", false);
//		createSampleProject("Demo-Projekt 6", false);
		log.debug("Initializing database ... DONE");		
	}

	private void createSampleProject(String projectName, boolean needMail, boolean needMobile, boolean active) throws WrongRatingQuestionCountExistingException, UiTextException, RatingQuestionsNotExistentException, ParticipantAlreadyExistingException, ProjectAlreadyExistingException, DuplicateRatingObjectException {

		log.debug("###### Creating sample project: {}", projectName);

		ZonedDateTime startTime = ZonedDateTime.of(2020, 1, 1, 14, 30, 0, 0, ZoneId.of(ZonedDateTimeUtility.CET));
		ZonedDateTime endTime = ZonedDateTime.of(2021, 12, 30, 0, 50, 0, 0, ZoneId.of(ZonedDateTimeUtility.CET));
		
		int neededQuestionCount = 5;
		
		List<RatingObject> roList = createRatingObjects();
		
		Project project = new Project(projectName, roList, needMail, needMobile, active, startTime, endTime, neededQuestionCount);
		projectService.save(project);

		//Create UiTexts and check if enough in method are created.
		createUiText(project);

		//Throws Exception if not
		uiTextService.checkEnoughUiTexts(project, false);

		if(! active) {
			return;
		}

		//Create RatingQuestions and check if enough in method are created.
		createRatingQuestions(project);		

		//Throws Exception if not
		rqService.checkEnoughRatingQuestions(project, false);
		
		createParticipations(project, Gender.MALE, 10);
		createParticipations(project, Gender.FEMALE, 10);		
	}

	private List<RatingObject> createRatingObjects() throws DuplicateRatingObjectException {

		log.debug("# Creating rating objects");

		List<RatingObject> roList = new ArrayList<>();
		roList.add(new RatingObject("gute Zeit mit Freunden haben"));
		roList.add(new RatingObject("guter Referent im Opening"));
		roList.add(new RatingObject("gutes Programm im Opening"));
		roList.add(new RatingObject("gute \"Zeitraum\" Leute, die für mich beten oder mir zuhören"));
//		roList.add(new RatingObject("gute kreative Aktionen die Nacht über"));
//		roList.add(new RatingObject("gute actionreiche Aktionen die Nacht über"));
//		roList.add(new RatingObject("neue Aktionen die Nacht über"));
//		roList.add(new RatingObject("coole Stationsmitarbeiter"));
//		roList.add(new RatingObject("gute Band"));
//		roList.add(new RatingObject("gute Seminare"));
//		roList.add(new RatingObject("gute chillige Aktionen die Nacht über"));
//		roList.add(new RatingObject("gutes Essen / guter Megabrunch"));
//		roList.add(new RatingObject("neue Leute kennenlernen"));
//		roList.add(new RatingObject("gute Zeit mit meinen Teenkreis Mitarbeitern haben"));		

		roService.validateAndSaveList(roList);

		return roList;
	}

	private void createRatingQuestions(Project project) throws ProjectAlreadyExistingException {

		log.debug("# Creating rating questions");

		List<RatingQuestion> rqList = new ArrayList<>();
		rqList.addAll(rqService.create(project));
		project.setActive(true);
		rqService.saveAll(rqList);
		projectService.save(project);

	}

	private void createUiText(Project project) {
		
		// ~~~~~~~~~~~~~~~~ Preparing empty texts ~~~~~~~~~~~~~~~~
		
		uiTextService.initializeUiText(project);
		
		// ~~~~~~~~~~~~~~~~ General texts ~~~~~~~~~~~~~~~~
		
		uiTextService.updateText(
				project,
				UiTextKey.MSG_PROJECTHOME,
				"Gib uns Feedback und hilf uns die Teennight noch besser zu machen. Unter den Teilnehmern verlosen wir zwei Freiplätze für die Teennight 2020!"
			);
		uiTextService.updateText(
				project,
				UiTextKey.MSG_PRICEGAME,
				priceGameStatement()
			);
		
		// ~~~~~~~~~~~~~~~~ Feedback texts ~~~~~~~~~~~~~~~~
		
		uiTextService.updateText(
				project,
				UiTextKey.MSG_FEEDBACK_START,
				"Nach der Teennight werden deine Daten gelöscht!"
			);
		uiTextService.updateText(
				project,
				UiTextKey.MSG_FEEDBACK_QUESTION,
				"Was ist dir während der Teennight <u>wichtiger</u>?"
			);
		uiTextService.updateText(
				project,
				UiTextKey.MSG_FEEDBACK_FREETEXT,
				"HIER IST PLATZ FÜR ALLES WAS DU SAGEN WILLST:<br>" +
						"- das feier ich an der Teennight<br>" +
						"- das könntet ihr noch besser machen<br>" +
						"- das wäre mal eine Idee für die Teennight<br>" + 
						"- das habe ich mit Gott erlebt auf der Teennight<br>"
			);
		uiTextService.updateText(
				project,
				UiTextKey.MSG_FEEDBACK_END,
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

	private void createParticipations(Project project, Gender gender, int count) throws RatingQuestionsNotExistentException, ParticipantAlreadyExistingException {

		log.debug("# Creating participations: {}");

		int localCount = count;
		
		while(localCount != 0) {
			
			localCount--;
			
			// Create and save Participant itself
			Participant participant = participantService.createParticipantForDB(project, gender);
			log.debug("Creating sample participant: {} {}", participant.getFirstname(), participant.getName());
			participantService.save(participant);
			
			// Create and save ParticipationResult
			Map<Long, Integer> feedbackMap = new HashMap<>();
			List<RatingQuestion> ratingQuestions = new ArrayList<>();
			rqService.addRatingQuestionsForProjectAndGenderToList(ratingQuestions, project, gender);
			for(RatingQuestion ratingQuestion : ratingQuestions) {
				long idRatingQuestion = ratingQuestion.getIdRatingQuestion();
				feedbackMap.put(idRatingQuestion, gender.equals(Gender.MALE) ? 1 : 2);
			}
			ParticipationResult pResult = new ParticipationResult(project, participant, feedbackMap, Instant.now().toString());
			participationResultService.saveParticipationResult(pResult);
			
			// Update RatingQuestion
			rqService.saveFeedback(feedbackMap);
		}
	}
}
