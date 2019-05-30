package eu.planlos.pcfeedback.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.planlos.pcfeedback.constants.ApplicationConfig;
import eu.planlos.pcfeedback.exceptions.RatingQuestionsNotExistentException;
import eu.planlos.pcfeedback.model.Gender;
import eu.planlos.pcfeedback.model.RatingObject;
import eu.planlos.pcfeedback.model.RatingQuestion;
import eu.planlos.pcfeedback.repository.RatingQuestionRepository;

//TODO Loggin
@Service
public class RatingQuestionService {

	private static final Logger logger = LoggerFactory.getLogger(RatingQuestionService.class);
	
	@Autowired
	private RatingQuestionRepository ratingQuestionRepository;
	
	public List<RatingQuestion> loadForGender(Gender gender) throws RatingQuestionsNotExistentException {
		
		logger.debug("Get the ids of ratingQuestions with lowest voted count");
		List<Integer> questionIds = chooseIdsOfLowestCountVoted(gender);
		
		logger.debug("Get the ratingQuestions with lowest voted count");
		List<RatingQuestion> questions = ratingQuestionRepository.findByIdRatingQuestion(questionIds);
		
		return questions;			
	}
	
	private List<Integer> chooseIdsOfLowestCountVoted(Gender gender) throws RatingQuestionsNotExistentException {
		
		logger.debug("Needed ratingQuestion count is: " + ApplicationConfig.NEEDED_QUESTION_COUNT);
		Integer neededCount = ApplicationConfig.NEEDED_QUESTION_COUNT;
		
		logger.debug("Get the lowest number a ratingQuestion is voted for gender: " + gender.toString());
		int lowestCount = getLowestCountRatingQuestionIsVoted(gender);
		
		logger.debug("Start adding ratingQuestions to result set");
		List<Integer> newQuestionIds = new ArrayList<Integer>();
		while(newQuestionIds.size() < neededCount) {
			
			// Load IDs with minimum count of answers
			// SELECT id FROM RatingQuestion WHERE countVoted = :countVoted AND Q.gender = :gender
			logger.debug("Load all questions for lowest number voted: " + lowestCount);
			List<Integer> loadedIds = ratingQuestionRepository.findByGenderAndCountVoted(gender.toString(), lowestCount);
			
			// If exact amount is found
			if(loadedIds.size() == neededCount) {

				logger.debug("Required count was loaded, add to result set and stop");
				newQuestionIds.addAll(loadedIds);
				break;
			}
			
			//If more questions are available chose random ones
			if(loadedIds.size() > neededCount) {

				logger.debug("More than required count was loaded, get random ratingQuestions of that list");
				List<Integer> randomIds = getRandomQuestionIds(neededCount, loadedIds);
				newQuestionIds.addAll(randomIds);
				break;
			}
						
			//If not enough questions are available
			if(loadedIds.size() < neededCount) {
				
				logger.debug("Lesser than required count was loaded, get new lowest number voted");
				newQuestionIds.addAll(loadedIds);
				lowestCount = getLowestCountRatingQuestionIsVoted(gender, lowestCount);		
			}
		}
		logger.debug("End of adding ratingQuestions to result set");
		
		return newQuestionIds;
	}

	private int getLowestCountRatingQuestionIsVoted(Gender gender) throws RatingQuestionsNotExistentException {
		return getLowestCountRatingQuestionIsVoted(gender, -1);
	}
	
	private int getLowestCountRatingQuestionIsVoted(Gender gender, int chosenCount) throws RatingQuestionsNotExistentException {

		//TODO is it really int?
		// Get the count of the least voted question so the least voted or least+1 voted questions can be loaded  
		int voteCount = ratingQuestionRepository.findFirstCountVotedByCountVotedGreaterThanAndGenderOrderByCountVotedAsc(chosenCount, gender.toString());
		
		if(voteCount == 0) {
			//TODO message needed?
			logger.debug("Whoopsie Doopsie");
			throw new RatingQuestionsNotExistentException("Whoopsie Doopsie");
		}
			
		return voteCount;
	}
	
	/**
	 * Method chooses randomly a given number of IDs out of the given list of more IDs
	 * @param neededCount how many IDs do you need?
	 * @param questionIds List of IDs from which should be chosen
	 * @return List of chosen random IDs
	 */
	private List<Integer> getRandomQuestionIds(int neededCount, List<Integer> questionIds) {

		logger.debug("Shuffle question ids in list");
		Collections.shuffle(questionIds);

		List<Integer> newQuestionIds = new ArrayList<Integer>();
		Iterator<Integer> questionIdsIterator = questionIds.iterator();

		while(neededCount > 0) {
			
			int randomQuestion = questionIdsIterator.next();
			newQuestionIds.add(randomQuestion);
			neededCount--;
			
			logger.debug("Added question id from shuffled list: " + randomQuestion);
		}
		
		return newQuestionIds;
	}

	//TODO does this work? :D
	@Transactional
	public void saveFeedback(List<RatingQuestion> ratingQuestionList) {

		logger.debug("Save feedback to database");
		
		for(RatingQuestion ratingQuestion : ratingQuestionList) {

			long idRatingQuestion = ratingQuestion.getIdRatingQuestion();
			int votesOne = ratingQuestion.getVotesOne();
			int votesTwo = ratingQuestion.getVotesTwo();
			
			logger.debug("Save ratingQuestion id=" + idRatingQuestion + " with votesOne=" + votesOne + " votesTwo=" + votesTwo);
			
			ratingQuestionRepository.addVotes(idRatingQuestion, votesOne, votesTwo);
		}	
	}
	
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
				ratingQuestionM.setGender(Gender.MALE);
				ratingQuestionM.setObjectOne(ratingObjectOne);
				ratingQuestionM.setObjectTwo(ratingObjectTwo);
				
				ratingQuestions.add(ratingQuestionM);
				
				RatingQuestion ratingQuestionW = new RatingQuestion();
				ratingQuestionW.setVotesOne(0);
				ratingQuestionW.setVotesTwo(0);
				ratingQuestionW.setCountVoted(0);
				ratingQuestionW.setGender(Gender.FEMALE);
				ratingQuestionW.setObjectOne(ratingObjectOne);
				ratingQuestionW.setObjectTwo(ratingObjectTwo);

				ratingQuestions.add(ratingQuestionW);

			}
		}
		return ratingQuestions;
	}
	
	public int maxQuestionCountForNumberOfRatingObjects(int existingRatingItems) {
		
		int possibleQuestionCount = 0;
		for(int i=0; i<existingRatingItems; i++) {
			possibleQuestionCount+=i;
		}
		
		return possibleQuestionCount;
	}
}
