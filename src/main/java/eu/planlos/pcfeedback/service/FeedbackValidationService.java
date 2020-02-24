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
		
		//feedback has needed question count
		if(givenQuestionCount == neededQuestionCount) {
			
			for(Long idRatingQuestion : feedbackMap.keySet()) {
				
				Integer voteFor = feedbackMap.get(idRatingQuestion);
	
				if(voteFor == null || (voteFor != RatingQuestionService.OBJECT_ONE && voteFor != RatingQuestionService.OBJECT_TWO)) {
					LOG.error("Feedback invalid. voteFor={}", voteFor);
					throw new InvalidFeedbackException("Fehler im Feedback, bitte noch mal versuchen.");
				}
			}
			
		//feedback has needed question count but wrong answers
		} else {
			LOG.error("Feedback invalid: questions given/needed={} / {}", givenQuestionCount, neededQuestionCount);
			throw new InvalidFeedbackException(String.format("Es wurden nicht alle Fragen beantwortet (%s/%s)", givenQuestionCount, neededQuestionCount));
		}
	}
}
