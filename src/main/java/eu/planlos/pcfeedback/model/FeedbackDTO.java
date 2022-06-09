package eu.planlos.pcfeedback.model;

import eu.planlos.pcfeedback.exceptions.InvalidFeedbackException;
import eu.planlos.pcfeedback.exceptions.NoFeedbackException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
@Slf4j
public class FeedbackDTO implements Serializable {

	private static final long serialVersionUID = -7206601709611371003L;

	public static final int OBJECT_ONE = 1;
	public static final int OBJECT_TWO = 2;

	public FeedbackDTO(Map<Long, Integer> feedbackMap) {
		this.feedbackMap = feedbackMap;
	}

	private Map<Long, Integer> feedbackMap = new HashMap<>();

	/**
	 * Validates if the Container has valid feedback
	 *
	 * @param neededQuestionCount The number of configured and therefore needed RatingQuestions
	 * @return
	 * @throws InvalidFeedbackException
	 * @throws NoFeedbackException
	 */
	public void validate(int neededQuestionCount) throws InvalidFeedbackException, NoFeedbackException {

		//no feedback given
		if(feedbackMap == null) {
			throw new NoFeedbackException();
		}

		int givenQuestionCount = feedbackMap.size();

		//feedback has NOT needed question count
		if(givenQuestionCount != neededQuestionCount) {
			log.error("Feedback invalid: questions given/needed={} / {}", givenQuestionCount, neededQuestionCount);
			throw new InvalidFeedbackException(String.format("Es wurden nicht alle Fragen beantwortet (%s/%s)", givenQuestionCount, neededQuestionCount));
		}

		//feedback has needed question count
		for(Long idRatingQuestion : feedbackMap.keySet()) {

			Integer voteFor = feedbackMap.get(idRatingQuestion);

			//feedback has needed question count but wrong answers
			if(voteFor == null || voteFor != OBJECT_ONE && voteFor != OBJECT_TWO) {
					log.error("Feedback invalid. voteFor={}", voteFor);
					throw new InvalidFeedbackException("Fehler im Feedback, bitte noch mal versuchen.");
			}
		}
	}
}
