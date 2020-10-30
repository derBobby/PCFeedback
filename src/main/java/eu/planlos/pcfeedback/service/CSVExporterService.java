package eu.planlos.pcfeedback.service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.planlos.pcfeedback.model.Gender;
import eu.planlos.pcfeedback.model.db.FreeText;
import eu.planlos.pcfeedback.model.db.Participant;
import eu.planlos.pcfeedback.model.db.Project;
import eu.planlos.pcfeedback.model.db.RatingObject;
import eu.planlos.pcfeedback.model.db.RatingQuestion;

@Service
public class CSVExporterService {
	
	@Autowired
	private RatingQuestionService rqService;
	
	@Autowired
	private ParticipantService pService;
	
	@Autowired
	private FreeTextService ftService;
		
	private static final Logger LOG = LoggerFactory.getLogger(CSVExporterService.class);

	private static final String[] FILE_RATINGQUESTION_HEADER = { "Geschlecht","A Bezeichnung","A Stimmen","B Stimmen","B Bezeichnung" };
	private static final String[] FILE_PARTICIPANT_HEADER = { "Vorname","Nachname","Geschlecht","Teilnahmezeitpuntk" };
	private static final String[] FILE_FREETEXT_HEADER = { "M/W","Text" };
	
	public void writeParticipantsCSV(Project project, PrintWriter writer) {
		
		List<Participant> pList = pService.getAllParticipantsForProject(project);

		CSVFormat csvFile = CSVFormat.EXCEL.withHeader(FILE_PARTICIPANT_HEADER).withAutoFlush(true).withDelimiter(';');
		CSVPrinter csvPrinter = null;

		try {
			csvPrinter = new CSVPrinter(new BufferedWriter(writer), csvFile);

			// --- Print Participants ---
			LOG.debug("Write participants");
			for (Participant participant : pList) {
				csvPrinter.printRecord(createParticipantRecord(participant));
				csvPrinter.flush();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.flush();
				writer.close();
				csvPrinter.close();
			} catch (IOException e) {
				LOG.error("Error while flushing/closing fileWriter/csvPrinter !!!");
			}
		}
	}

	public void writeRatingQuestionCSV(PrintWriter writer, Project project, Gender gender) {

		List<RatingQuestion> rqList = new ArrayList<>();
		
		if(gender == null) {
			rqList.addAll(rqService.loadByProjectAndGender(project, Gender.MALE));
			rqList.addAll(rqService.loadByProjectAndGender(project, Gender.FEMALE));
		} else {
			rqList.addAll(rqService.loadByProjectAndGender(project, gender));
		}
				
		CSVFormat csvFile = CSVFormat.EXCEL.withHeader(FILE_RATINGQUESTION_HEADER).withAutoFlush(true).withDelimiter(';');
		CSVPrinter csvPrinter = null;

		try {
			csvPrinter = new CSVPrinter(new BufferedWriter(writer), csvFile);

			// --- Print RatingQuestions ---
			LOG.debug("Write ratingQuestions");
			for (RatingQuestion ratingQuestion : rqList) {
				csvPrinter.printRecord(createRatingQuestionRecord(ratingQuestion));
				csvPrinter.flush();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.flush();
				writer.close();
				csvPrinter.close();
			} catch (IOException e) {
				LOG.error("Error while flushing/closing fileWriter/csvPrinter !!!");
			}
		}
	}
	
	public void writeFreeTextCSV(PrintWriter writer, Project project) {

		List<FreeText> ftList = ftService.findAllByProject(project);

		CSVFormat csvFile = CSVFormat.EXCEL.withHeader(FILE_FREETEXT_HEADER).withAutoFlush(true).withDelimiter(';');
		CSVPrinter csvPrinter = null;

		try {
			csvPrinter = new CSVPrinter(new BufferedWriter(writer), csvFile);

			// --- Print Participants ---
			LOG.debug("Write free texts");
			for (FreeText freeText : ftList) {
				csvPrinter.printRecord(createFreeTextRecord(freeText));
				csvPrinter.flush();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.flush();
				writer.close();
				csvPrinter.close();
			} catch (IOException e) {
				LOG.error("Error while flushing/closing fileWriter/csvPrinter !!!");
			}
		}		
	}
	
	private List<Object> createRatingQuestionRecord(RatingQuestion ratingQuestion) {

		Gender gender = ratingQuestion.getGender();
		long idRatingQuestion = ratingQuestion.getIdRatingQuestion();

		RatingObject ratingObjectOne = ratingQuestion.getObjectOne();

		String nameOne = ratingObjectOne.getName();
		Integer votesOne = ratingQuestion.getVotesOne();
		String votesOneStr = String.valueOf(votesOne);

		RatingObject ratingObjectTwo = ratingQuestion.getObjectTwo();

		String nameTwo = ratingObjectTwo.getName();
		Integer votesTwo = ratingQuestion.getVotesTwo();
		String votesTwoStr = String.valueOf(votesTwo);

		List<Object> rqRecord = new ArrayList<>();

		rqRecord.add(gender.toString());
		rqRecord.add(nameOne);
		rqRecord.add(votesOneStr);
		rqRecord.add(votesTwoStr);
		rqRecord.add(nameTwo);

		LOG.debug("Write ratingQuestion to file: idRatingQuestion={} gender={} name1={} votes1={} name2={} votes2={}",
				idRatingQuestion, gender.toString(), nameOne, votesOneStr, nameTwo, votesTwoStr);

		return rqRecord;
	}

	private List<Object> createParticipantRecord(Participant participant) {

		long idParticipant = participant.getIdParticipant();
		String firstname = participant.getFirstname();
		String name = participant.getName();
		Gender gender = participant.getGender();
		String participationDate = participant.getformattedParticipationDateString();

		List<Object> participantRecord = new ArrayList<>();
		participantRecord.add(firstname);
		participantRecord.add(name);
		participantRecord.add(gender.toString());
		participantRecord.add(participationDate);

		LOG.debug(
				"Creating record for participant: idParticipant={} firstname={} name={} gender={} participationDate={}",
				idParticipant, firstname, name, gender.toString(), participationDate);

		return participantRecord;
	}
	
	private List<Object> createFreeTextRecord(FreeText freeText) {

		Gender gender = freeText.getGender();
		String text = freeText.getText();

		List<Object> participantRecord = new ArrayList<>();
		participantRecord.add(gender.toString());
		participantRecord.add(text);

		LOG.debug(
				"Creating record for free text: idFreeText={}",
				freeText.getIdFreeText());

		return participantRecord;
	}
}