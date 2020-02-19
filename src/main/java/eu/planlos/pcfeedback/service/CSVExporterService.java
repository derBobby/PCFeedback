package eu.planlos.pcfeedback.service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import eu.planlos.pcfeedback.model.Gender;
import eu.planlos.pcfeedback.model.Participant;
import eu.planlos.pcfeedback.model.RatingObject;
import eu.planlos.pcfeedback.model.RatingQuestion;

@Service
public class CSVExporterService {

	private static final Logger LOG = LoggerFactory.getLogger(CSVExporterService.class);

	private static final Object[] FILE_HEADER = { "Geschlecht", "A Bezeichnung", "A Stimmen", "B Stimmen",
			"B Bezeichnung" };

	//TODO exception handling not good enough
	public void exportResultTo(List<RatingQuestion> rqList, List<Participant> pList, String targetPath) throws IOException {

		BufferedWriter fileWriter = Files.newBufferedWriter(Paths.get(targetPath));

		CSVFormat csvFile = CSVFormat.EXCEL.withHeader();
		CSVPrinter csvPrinter = new CSVPrinter(fileWriter, csvFile);

		LOG.debug("Write csv file header");
		csvPrinter.printRecord(FILE_HEADER);

		// --- Print RatingQuestions ---
		LOG.debug("Write ratingQuestions");
		for (RatingQuestion ratingQuestion : rqList) {	
			csvPrinter.printRecord(createRatingQuestionRecord(ratingQuestion));
		}

		// --- Print Participants ---
		LOG.debug("Write participants");
		for (Participant participant : pList) {
			csvPrinter.printRecord(createParticipantRecord(participant));
		}

		csvPrinter.close();
		fileWriter.close();
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
				idRatingQuestion, gender.toString(), nameOne, votesOneStr, nameTwo, votesTwoStr
			);
		
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

		LOG.debug("Creating record for participant: idParticipant={} firstname={} name={} gender={} participationDate{}=", 
				idParticipant, firstname, name, gender.toString() , participationDate
			);
		
		return participantRecord;
	}
}