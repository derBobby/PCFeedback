package eu.planlos.pcfeedback.old;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Service;

import eu.planlos.pcfeedback.model.RatingObject;
import eu.planlos.pcfeedback.model.RatingQuestion;

@Service
public class RatingQuestionFactory {

	public List<RatingQuestion> create(List<?> ratingObjectListOne) {
		
		List<RatingQuestion> ratingQuestions = new ArrayList<RatingQuestion>();
		
		for (Iterator<?> ratingObjectsOneIterator = ratingObjectListOne.iterator(); ratingObjectsOneIterator.hasNext();) {
			
			RatingObject ratingObjectOne = (RatingObject) ratingObjectsOneIterator.next();
			
			for (Iterator<?> ratingObjectsTwoIterator = ratingObjectListOne.iterator(); ratingObjectsTwoIterator.hasNext();) {
				
				RatingObject ratingObjectTwo = (RatingObject) ratingObjectsTwoIterator.next();

				if(ratingObjectOne.equals(ratingObjectTwo)) break;
				
				RatingQuestion ratingQuestionM = new RatingQuestion();
				ratingQuestionM.setVotesOne(0);
				ratingQuestionM.setVotesTwo(0);
				ratingQuestionM.setCountVoted(0);
				ratingQuestionM.setGender("männlich");
				ratingQuestionM.setObjectOne(ratingObjectOne);
				ratingQuestionM.setObjectTwo(ratingObjectTwo);
				
				ratingQuestions.add(ratingQuestionM);
				
				RatingQuestion ratingQuestionW = new RatingQuestion();
				ratingQuestionW.setVotesOne(0);
				ratingQuestionW.setVotesTwo(0);
				ratingQuestionW.setCountVoted(0);
				ratingQuestionW.setGender("weiblich");
				ratingQuestionW.setObjectOne(ratingObjectOne);
				ratingQuestionW.setObjectTwo(ratingObjectTwo);

				ratingQuestions.add(ratingQuestionW);

			}
		}
		return ratingQuestions;
	}
}
