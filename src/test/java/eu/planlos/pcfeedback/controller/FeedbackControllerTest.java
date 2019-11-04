package eu.planlos.pcfeedback.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import eu.planlos.pcfeedback.exceptions.InvalidFeedbackException;
import eu.planlos.pcfeedback.model.RatingQuestion;
import eu.planlos.pcfeedback.repository.RatingQuestionRepository;
import eu.planlos.pcfeedback.service.RatingQuestionService;

public class FeedbackControllerTest {

	@Autowired
	private RatingQuestionService ratingQuestionService;
	
	@Autowired
	private RatingQuestionRepository ratingQuestioRepository;
	
	@Value("${eu.planlos.pcfeedback.question-count}")
	private int neededQuestionCount;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public final void savingValidFeedbackMap() throws InvalidFeedbackException {
		
		//Given
		Map<Long, Integer> validFeedbackMap = new HashMap<>();
		for(int i=0; i<neededQuestionCount; i++) {
			validFeedbackMap.put(randomId(), randomAnswer());
		}
		
		// Assert
		ratingQuestionService.saveFeedback(validFeedbackMap);
		
		// Expect
		List<RatingQuestion> rqList = new ArrayList<>();
		rqList.addAll((Collection<RatingQuestion>) ratingQuestioRepository.findAllById(validFeedbackMap.keySet()));
		
		int sumVotedCount = 0;
		int sumVotedOne = 0;
		int sumVotedTwo = 0;
		
		for(RatingQuestion rq : rqList) {
			sumVotedCount += rq.getCountVoted();
			sumVotedOne += rq.getVotesOne();
			sumVotedTwo += rq.getVotesTwo();
		}

		int shouldBeVotedCount = 0;
		int shouldBeVotedOne = 0;
		int shouldBeVotedTwo = 0; 
		
		for(long x : validFeedbackMap.keySet()) {
//			shouldBeVotedCount 
		}
		
	}
	
	private int randomAnswer() {
		return Math.random() <= 0.5 ? 1 : 2;
	}

	private long randomId() {

		int min = 0;
		int max = 14;
		
		Random r = new Random();
		return (long) r.nextInt((max - min) + 1) + min;
	}

}
