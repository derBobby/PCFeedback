package eu.planlos.pcfeedback.service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import eu.planlos.pcfeedback.model.Gender;
import eu.planlos.pcfeedback.model.db.Participant;
import eu.planlos.pcfeedback.model.db.ParticipationResult;
import eu.planlos.pcfeedback.model.db.RatingObject;
import eu.planlos.pcfeedback.model.db.RatingQuestion;

@Service
public class CSVExporterService {
		
	private static final Logger LOG = LoggerFactory.getLogger(CSVExporterService.class);

	private static final String[] FILE_RATINGQUESTION_HEADER = { "Geschlecht","A Bezeichnung","A Stimmen","B Stimmen","B Bezeichnung" };
	private static final String[] FILE_PARTICIPANT_HEADER = { "Vorname","Nachname","Geschlecht","Teilnahmezeitpunkt" };
	private static final String[] FILE_FREETEXT_HEADER = { "M/W","Text" };
	
//	public void writeParticipantsCSV(List<Participant> pList, PrintWriter writer) {
//		
//		CSVFormat csvFile = csvFormatForHeader(FILE_PARTICIPANT_HEADER);
//		CSVPrinter csvPrinter = null;
//
//		try {
//			csvPrinter = new CSVPrinter(new BufferedWriter(writer), csvFile);
//			
//			LOG.debug("Write participants");
//			for (Participant participant : pList) {
//				
//				List<Object> participantRecord = createParticipantRecord(participant);
//				
//				csvPrinter.printRecord(participantRecord);
//				csvPrinter.flush();
//			}
//			
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				writer.flush();
//				writer.close();
//				csvPrinter.close();
//			} catch (IOException e) {
//				LOG.error("Error while flushing/closing fileWriter/csvPrinter !!!");
//			}
//		}
//	}
//
//	public void writeRatingQuestionCSV(List<RatingQuestion> rqList, PrintWriter writer) {
//				
//		CSVFormat csvFile = csvFormatForHeader(FILE_RATINGQUESTION_HEADER);
//		CSVPrinter csvPrinter = null;
//
//		try {
//			LOG.debug("Write ratingQuestions");
//
//			csvPrinter = new CSVPrinter(new BufferedWriter(writer), csvFile);
//
//			for (RatingQuestion ratingQuestion : rqList) {
//				
//				List<Object> ratingQuestionRecord = createRatingQuestionRecord(ratingQuestion);
//				
//				csvPrinter.printRecord(ratingQuestionRecord);
//				csvPrinter.flush();
//			}
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				writer.flush();
//				writer.close();
//				csvPrinter.close();
//			} catch (IOException e) {
//				LOG.error("Error while flushing/closing fileWriter/csvPrinter !!!");
//			}
//		}
//	}
//	
//	public void writeFreeTextCSV(List<ParticipationResult> prList, PrintWriter writer) {
//		
//		CSVFormat csvFile = csvFormatForHeader(FILE_FREETEXT_HEADER);
//		CSVPrinter csvPrinter = null;
//
//		try {
//			LOG.debug("Write free texts");
//
//			csvPrinter = new CSVPrinter(new BufferedWriter(writer), csvFile);
//			for (ParticipationResult pr : prList) {
//				
//				List<Object> freeTextRecord = createFreeTextRecord(pr);
//				
//				csvPrinter.printRecord(freeTextRecord);
//				csvPrinter.flush();
//			}
//			
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				writer.flush();
//				writer.close();
//				csvPrinter.close();
//			} catch (IOException e) {
//				LOG.error("Error while flushing/closing fileWriter/csvPrinter !!!");
//			}
//		}		
//	}
	
	public void writeCSV(List<?> objectList, PrintWriter writer) {
		
		CSVPrinter csvPrinter = null;		
		Object first = objectList.iterator().next();

		Method method = null;
		CSVFormat csvFile = null;

		try {
			
			if (first instanceof ParticipationResult) {
				method = this.getClass().getDeclaredMethod("createFreeTextRecord", new Class[] { ParticipationResult.class });
				csvFile = csvFormatForHeader(FILE_FREETEXT_HEADER);
			}
			
			if (first instanceof Participant) {
				method = this.getClass().getDeclaredMethod("createParticipantRecord", new Class[] { Participant.class });
				csvFile = csvFormatForHeader(FILE_PARTICIPANT_HEADER);
			}

			if (first instanceof RatingQuestion) {
				method = this.getClass().getDeclaredMethod("createRatingQuestionRecord", new Class[] { RatingQuestion.class });
				csvFile = csvFormatForHeader(FILE_RATINGQUESTION_HEADER);
			}
			
			if (method == null) {
				throw new Exception("Unsupported class given");
			}
			
			csvPrinter = new CSVPrinter(new BufferedWriter(writer), csvFile);
			
			for (Object o : objectList) {
				
				@SuppressWarnings("unchecked")
				List<Object> record = (List<Object>) method.invoke(this, o);
								
				csvPrinter.printRecord(record);
				csvPrinter.flush();
			}
			
		} catch (Exception e) {
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
	
	@SuppressWarnings("unused")
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

		LOG.debug("Created question record: id={} gender={} name1={} votes1={} name2={} votes2={}",
				idRatingQuestion, gender.toString(), nameOne, votesOneStr, nameTwo, votesTwoStr);

		return rqRecord;
	}

	@SuppressWarnings("unused")
	private List<Object> createParticipantRecord(Participant participant) {

		long idParticipant = participant.getIdParticipant();
		String firstname = participant.getFirstname();
		String name = participant.getName();
		Gender gender = participant.getGender();
		String participationDate = participant.getformattedParticipationTimeString();

		List<Object> participantRecord = new ArrayList<>();
		participantRecord.add(firstname);
		participantRecord.add(name);
		participantRecord.add(gender.toString());
		participantRecord.add(participationDate);

		LOG.debug(
				"Created participant record: id={} firstname={} name={} gender={} participationDate={}",
				idParticipant, firstname, name, gender.toString(), participationDate);

		return participantRecord;
	}
	
	@SuppressWarnings("unused")
	private List<Object> createFreeTextRecord(ParticipationResult pr) {
		
		Long idParticipationResult = pr.getIdParticipationResult();
		String freeText = pr.getFreeText();
		Gender gender = pr.getParticipant().getGender();
		
		List<Object> freeTextRecord = new ArrayList<>();
		freeTextRecord.add(gender.toString());
		freeTextRecord.add(freeText);

		LOG.debug(
				"Created free text record: id={}", idParticipationResult);
		
		return freeTextRecord;
	}

	private CSVFormat csvFormatForHeader(String[] fileParticipantHeader) {
		return CSVFormat.EXCEL.withHeader(fileParticipantHeader).withAutoFlush(true).withDelimiter(';');
	}
}