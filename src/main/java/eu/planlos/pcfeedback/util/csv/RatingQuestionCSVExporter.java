package eu.planlos.pcfeedback.util.csv;

import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.planlos.pcfeedback.model.Gender;
import eu.planlos.pcfeedback.model.db.RatingObject;
import eu.planlos.pcfeedback.model.db.RatingQuestion;

public class RatingQuestionCSVExporter implements ICSVExporter {

	private static final Logger LOG = LoggerFactory.getLogger(RatingQuestionCSVExporter.class);

	private static final String[] FILE_RATINGQUESTION_HEADER = { "Geschlecht","A Bezeichnung","A Stimmen","B Stimmen","B Bezeichnung" };

	@Override
	public List<Object> createRecord(Object object) throws InvalidObjectException {

		if(!(object instanceof RatingQuestion)) {
			throw new InvalidObjectException("Invalid object was given.");
		}
		RatingQuestion ratingQuestion = (RatingQuestion) object;
		
		// Gender evaluation
		Gender gender = ratingQuestion.getGender();
		String genderString = "aggregiert";
		if(gender != null) {
			genderString = gender.toString();
		}
		
		// Read object one
		RatingObject ratingObjectOne = ratingQuestion.getObjectOne();

		String nameOne = ratingObjectOne.getName();
		Integer votesOne = ratingQuestion.getVotesOne();
		String votesOneStr = String.valueOf(votesOne);

		// Read object two
		RatingObject ratingObjectTwo = ratingQuestion.getObjectTwo();

		String nameTwo = ratingObjectTwo.getName();
		Integer votesTwo = ratingQuestion.getVotesTwo();
		String votesTwoStr = String.valueOf(votesTwo);

		// Create Record
		List<Object> rqRecord = new ArrayList<>();

		rqRecord.add(genderString);
		rqRecord.add(nameOne);
		rqRecord.add(votesOneStr);
		rqRecord.add(votesTwoStr);
		rqRecord.add(nameTwo);

		LOG.debug("Created record: gender={} name1={} votes1={} name2={} votes2={}",
				genderString, nameOne, votesOneStr, nameTwo, votesTwoStr);

		return rqRecord;
	}

	@Override
	public String[] getHeader() {
		return FILE_RATINGQUESTION_HEADER;
	}
}
