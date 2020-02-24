package eu.planlos.pcfeedback.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import eu.planlos.pcfeedback.exceptions.InvalidFeedbackException;
import eu.planlos.pcfeedback.exceptions.NoFeedbackException;

@Service
public class FeedbackValidationService {

	private static final Logger LOG = LoggerFactory.getLogger(FeedbackValidationService.class);

	@Value("${eu.planlos.pcfeedback.question-count}")
	public int neededQuestionCount;
	
	public void isValidFeedback(Map<Long, Integer> feedbackMap) throws InvalidFeedbackException, NoFeedbackException {
		
		//no feedback given
		if(feedbackMap == null) {
			throw new NoFeedbackException();			
		} 

		int givenQuestionCount = feedbackMap.size();
		
		//feedback has not needed question count
		if(givenQuestionCount == neededQuestionCount) {
			
			for(Long idRatingQuestion : feedbackMap.keySet()) {
				
				Integer voteFor = feedbackMap.get(idRatingQuestion);
	
				if(voteFor == null || (voteFor != RatingQuestionService.OBJECT_ONE && voteFor != RatingQuestionService.OBJECT_TWO)) {
					String errorMsg = String.format("Feedback invalid. voteFor=%s", voteFor);
					LOG.error(errorMsg);
					throw new InvalidFeedbackException(errorMsg);
				}
			}
			
		//feedback has neede question count but wrong answers
		} else {
			String errorMsg = String.format("Feedback invalid: questions needed/given=%s/%s", neededQuestionCount, givenQuestionCount);
			LOG.error(errorMsg);
			throw new InvalidFeedbackException(errorMsg);
		}
	}
}
