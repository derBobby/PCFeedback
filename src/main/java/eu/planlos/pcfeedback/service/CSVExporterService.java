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
import eu.planlos.pcfeedback.model.Participant;
import eu.planlos.pcfeedback.model.RatingObject;
import eu.planlos.pcfeedback.model.RatingQuestion;

@Service
public class CSVExporterService {
	
	@Autowired
	private RatingQuestionService rqService;
	
	@Autowired
	private ParticipantService pService;
		
	private static final Logger LOG = LoggerFactory.getLogger(CSVExporterService.class);

	private static final String[] FILE_RATINGQUESTION_HEADER = { "Geschlecht","A Bezeichnung","A Stimmen","B Stimmen","B Bezeichnung" };
	private static final String[] FILE_PARTICIPANT_HEADER = { "Vorname","Nachname","Geschlecht","Teilnahmezeitpuntk" };
	
	public void writeParticipantsCSV(PrintWriter writer) {
		
		List<Participant> pList = pService.getAllParticipants();

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
				System.out.println("Error while flushing/closing fileWriter/csvPrinter !!!");
				e.printStackTrace();
			}
		}
	}

	public void writeRatingQuestionCSV(PrintWriter writer, Gender gender) {

		List<RatingQuestion> rqList = new ArrayList<>();
		
		if(gender == null) {
			rqList.addAll(rqService.loadByGender(Gender.MALE));
			rqList.addAll(rqService.loadByGender(Gender.FEMALE));
		} else {
			rqList.addAll(rqService.loadByGender(gender));
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
				System.out.println("Error while flushing/closing fileWriter/csvPrinter !!!");
				e.printStackTrace();
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

}