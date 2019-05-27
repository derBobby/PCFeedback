package eu.planlos.pcfeedback.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.planlos.pcfeedback.constants.ApplicationConfig;
import eu.planlos.pcfeedback.exceptions.RatingQuestionsNotExistentException;
import eu.planlos.pcfeedback.model.RatingQuestion;
import eu.planlos.pcfeedback.repository.RatingQuestionRepository;

@Service
public class RatingQuestionService {

	@Autowired
	private RatingQuestionRepository ratingQuestionRepo;
	
	//TODO
//	@Autowired
//	private ConfigurationRepository configRepo;
	
	public List<RatingQuestion> loadForGender(String gender) throws RatingQuestionsNotExistentException {
		
		List<Integer> questionIds = chooseIdsOfLowestCountVoted(gender);
		List<RatingQuestion> questions = ratingQuestionRepo.findAllByIdRatingQuestion(questionIds);
		
		return questions;			
	}
	
	private List<Integer> chooseIdsOfLowestCountVoted(String gender) throws RatingQuestionsNotExistentException {
		
		Integer neededCount = ApplicationConfig.NEEDED_QUESTION_COUNT;
		
		//Load the minimum count of answers for pairing gender and project
		int lowestCountVoted = getLowestVotedCount(gender);
		
		List<Integer> newQuestionIds = new ArrayList<Integer>();
			
		while(newQuestionIds.size() < neededCount) {
			
			// Load IDs with minimum count of answers
			// SELECT id FROM RatingQuestion WHERE countVoted = :countVoted AND Q.gender = :gender
			List<Integer> loadedIds = ratingQuestionRepo.loadByGenderAndCountVoted(gender, lowestCountVoted);
			
			// If exact amount is found
			if(loadedIds.size() == neededCount) {

				//TODO Logger
				newQuestionIds.addAll(loadedIds);
				break;
			}
			
			//If more questions are available chose random ones
			if(loadedIds.size() > neededCount) {

				//TODO Logger
				List<Integer> randomIds = getRandomQuestionIds(neededCount, loadedIds);
				newQuestionIds.addAll(randomIds);
				break;
			}
						
			//If not enough questions are available
			if(loadedIds.size() < neededCount) {
				
				newQuestionIds.addAll(loadedIds);
				lowestCountVoted = getLowestVotedCount(gender, lowestCountVoted);		
			}
		}
		
		return newQuestionIds;
	}

	private int getLowestVotedCount(String gender) throws RatingQuestionsNotExistentException {
		
		return getLowestVotedCount(gender, -1);
	}
	
	private int getLowestVotedCount(String gender, int chosenCount) throws RatingQuestionsNotExistentException {
		
		// Get the count of the least voted question so the least voted or least+1 voted questions can be loaded  
		// SELECT min(countVoted) FROM RatingQuestion WHERE project = :project AND countVoted > :chosenCount AND gender = :gender
		int questionCount = ratingQuestionRepo.findMinCountVotedByCountVotedGreaterThanAndGender(chosenCount, gender);
		
		if(questionCount == 0) {
			throw new RatingQuestionsNotExistentException();
		}
			
		return questionCount;
	}
	
	/**
	 * Method chooses randomly a given number of IDs out of the given list of more IDs
	 * @param neededCount how many IDs do you need?
	 * @param questionIds List of IDs from which should be chosen
	 * @return List of chosen random IDs
	 */
	private List<Integer> getRandomQuestionIds(int neededCount, List<Integer> questionIds) {

		Collections.shuffle(questionIds);

		List<Integer> newQuestionIds = new ArrayList<Integer>();
		Iterator<Integer> questionIdsIterator = questionIds.iterator();

		while(neededCount > 0) {
			
			Integer randomQuestion = questionIdsIterator.next();
			newQuestionIds.add(randomQuestion);
			neededCount--;
		}
		
		return newQuestionIds;
	}
}
