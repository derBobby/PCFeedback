package eu.planlos.pcfeedback.service;

import java.io.FileWriter;
import java.io.IOException;
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

	private static final Logger logger = LoggerFactory.getLogger(CSVExporterService.class);

	private static final Object[] FILE_HEADER = { "Geschlecht", "A Bezeichnung", "A Stimmen", "B Stimmen",
			"B Bezeichnung" };

	public void exportResultTo(List<RatingQuestion> ratingQuestionList, List<Participant> participantList, String targetPath) throws IOException {

		FileWriter fileWriter = new FileWriter(targetPath);

		CSVFormat csvFile = CSVFormat.EXCEL.withHeader();
		CSVPrinter csvPrinter = new CSVPrinter(fileWriter, csvFile);

		logger.debug("Write csv file header");
		csvPrinter.printRecord(FILE_HEADER);

		for (RatingQuestion ratingQuestion : ratingQuestionList) {

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

			List<Object> ratingQuestionRecord = new ArrayList<Object>();

			ratingQuestionRecord.add(gender.toString());
			ratingQuestionRecord.add(nameOne);
			ratingQuestionRecord.add(votesOneStr);
			ratingQuestionRecord.add(votesTwoStr);
			ratingQuestionRecord.add(nameTwo);

			logger.debug("Write ratingQuestion to file:" + 
					" idRatingQuestion=" + idRatingQuestion
					+ " gender=" + gender.toString() 
					+ " name1=" + nameOne
					+ " votes1=" + votesOneStr
					+ " name2=" + nameTwo
					+ " votes2=" + votesTwoStr
				);
			
			csvPrinter.printRecord(ratingQuestionRecord);
		}

		for (Participant participant : participantList) {

			long idParticipant = participant.getIdParticipant();
			String firstname = participant.getFirstname();
			String name = participant.getName();
			Gender gender = participant.getGender();
			String participationDate = participant.getformattedParticipationDateString();
			
			List<Object> participantRecord = new ArrayList<Object>();
			participantRecord.add(firstname);
			participantRecord.add(name);
			participantRecord.add(gender.toString());
			participantRecord.add(participationDate);

			logger.debug("Write participant to file:" + 
					" idParticipant=" + idParticipant
					+ " firstname=" + firstname
					+ " name=" + name
					+ " gender=" + gender.toString() 
					+ " participationDate=" + participationDate
				);
			
			csvPrinter.printRecord(participantRecord);
		}

		csvPrinter.close();
		fileWriter.close();
	}
}