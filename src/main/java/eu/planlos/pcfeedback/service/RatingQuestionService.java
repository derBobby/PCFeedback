package eu.planlos.pcfeedback.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.planlos.pcfeedback.constants.ApplicationConfig;
import eu.planlos.pcfeedback.exceptions.InvalidFeedbackException;
import eu.planlos.pcfeedback.exceptions.RatingQuestionsNotExistentException;
import eu.planlos.pcfeedback.model.Gender;
import eu.planlos.pcfeedback.model.RatingObject;
import eu.planlos.pcfeedback.model.RatingQuestion;
import eu.planlos.pcfeedback.repository.RatingQuestionRepository;

//TODO Logging
@Service
public class RatingQuestionService {

	private static final Logger logger = LoggerFactory.getLogger(RatingQuestionService.class);
	
	@Autowired
	private RatingQuestionRepository ratingQuestionRepository;
	
	/**
	 * Returns rating questions for the feedback process for fiven gender
	 * @param gender
	 * @return List<RatingQuestion>
	 * @throws RatingQuestionsNotExistentException
	 */
	public List<RatingQuestion> loadForFeedbackByGender(Gender gender) throws RatingQuestionsNotExistentException {
		
		logger.debug("Get the ratingQuestions with lowest voted count");
		List<RatingQuestion> ratingQuestions = chooseLowestVotedCount(gender);
				
		return ratingQuestions;			
	}
	
	/**
	 * Returns all rating questions for given gender
	 * @param gender
	 * @return List<RatingQuestion>
	 */
	public List<RatingQuestion> loadByGender(Gender gender) {
		List<RatingQuestion> ratingQuestions = ratingQuestionRepository.findByGender(gender);
		return ratingQuestions;	
	}
	
	private List<RatingQuestion> chooseLowestVotedCount(Gender gender) throws RatingQuestionsNotExistentException {
		
		logger.debug("Needed ratingQuestion count is: " + ApplicationConfig.NEEDED_QUESTION_COUNT);
		Integer neededCount = ApplicationConfig.NEEDED_QUESTION_COUNT;
		
		logger.debug("Get the lowest number a ratingQuestion is voted for gender: " + gender.toString());
		int lowestVotedCount = getLowestCountRatingQuestionIsVoted(gender);
		
		logger.debug("Start adding ratingQuestions to result set");
		List<RatingQuestion> usedQuestions = new ArrayList<>();
		while(usedQuestions.size() < neededCount) {
			
			// Load IDs with minimum count of answers
			logger.debug("Load all questions for lowest number voted: " + lowestVotedCount);
			List<RatingQuestion> loadedQuestions = ratingQuestionRepository.findByGenderAndCountVoted(gender, lowestVotedCount);
			
			// If exact amount is found
			if(loadedQuestions.size() == neededCount) {

				logger.debug("Required count was loaded, add to result set and stop");
				usedQuestions.addAll(loadedQuestions);
				break;
			}
			
			//If more questions are available chose random ones
			if(loadedQuestions.size() > neededCount) {

				logger.debug("More than required count was loaded, get random ratingQuestions of that list");
				List<RatingQuestion> randomQuestions = getRandomQuestionIds(neededCount, loadedQuestions);
				usedQuestions.addAll(randomQuestions);
				break;
			}
						
			//If not enough questions are available
			if(loadedQuestions.size() < neededCount) {
				
				logger.debug("Lesser than required count was loaded, get new lowest number voted");
				usedQuestions.addAll(loadedQuestions);
				lowestVotedCount = getLowestCountRatingQuestionIsVoted(gender, lowestVotedCount);		
			}
		}
		logger.debug("End of adding ratingQuestions to result set");
		
		return usedQuestions;
	}

	private int getLowestCountRatingQuestionIsVoted(Gender gender) throws RatingQuestionsNotExistentException {
		return getLowestCountRatingQuestionIsVoted(gender, -1);
	}
	
	private int getLowestCountRatingQuestionIsVoted(Gender gender, int chosenCount) throws RatingQuestionsNotExistentException {

		// Get the count of the least voted question so the least voted or least+1 voted questions can be loaded
		RatingQuestion lowestVotedCountQuestion = ratingQuestionRepository.findFirstByCountVotedGreaterThanAndGenderOrderByCountVotedAsc(chosenCount, gender);
		
		if(lowestVotedCountQuestion == null) {
			logger.error("No rating questions found with more votes than: " + chosenCount);
			throw new RatingQuestionsNotExistentException("No rating questios found with more votes than: " + chosenCount);
		}
					
		return lowestVotedCountQuestion.getCountVoted();
	}
	
	/**
	 * Method chooses randomly a given number of IDs out of the given list of more IDs
	 * @param neededCount how many IDs do you need?
	 * @param givenQuestions List of IDs from which should be chosen
	 * @return List of chosen random IDs
	 */
	private List<RatingQuestion> getRandomQuestionIds(int neededCount, List<RatingQuestion> givenQuestions) {

		logger.debug("Shuffle question ids in list");
		Collections.shuffle(givenQuestions);

		List<RatingQuestion> usedQuestions = new ArrayList<>();
		Iterator<RatingQuestion> givenQuestionIterator = givenQuestions.iterator();

		while(neededCount > 0) {
			
			RatingQuestion randomQuestion = givenQuestionIterator.next();
			usedQuestions.add(randomQuestion);
			neededCount--;
			
			logger.debug("Added question id from shuffled list: " + randomQuestion);
		}
		
		return usedQuestions;
	}

	//TODO does this work? :D
	@Transactional
	public void saveFeedback(Map<Long, Integer> feedbackMap) throws InvalidFeedbackException {

		logger.debug("Save feedback to database");
		
		// Makes sure voteFor will be 1 or 2 
		checkIfRatingQuestionsAreValid(feedbackMap);
		
		for(Long idRatingQuestion : feedbackMap.keySet()) {
			
			int voteFor = feedbackMap.get(idRatingQuestion);
			logger.debug("Rating question " + idRatingQuestion + " got vote for rating obejct \"" + voteFor + "\"");
			
			if(voteFor == 1) {
				ratingQuestionRepository.addVoteForRatingObjectOne(idRatingQuestion);
				continue;
			}
			ratingQuestionRepository.addVoteForRatingObjectTwo(idRatingQuestion);
		}	
	}

	private void checkIfRatingQuestionsAreValid(Map<Long, Integer> feedbackMap) throws InvalidFeedbackException {
		
		if(feedbackMap.size() != ApplicationConfig.NEEDED_QUESTION_COUNT) {
			logger.error("Feedback HashMap is invalid: not matching needed amount of questions");
			throw new InvalidFeedbackException();
		}
		
		for(Long idRatingQuestion : feedbackMap.keySet()) {
			
			Integer voteFor = feedbackMap.get(idRatingQuestion);

			if(voteFor == null) {
				logger.error("Feedback HashMap is invalid: vote is null");
				throw new InvalidFeedbackException();
			}
			if(voteFor != 1 && voteFor != 2) {
				logger.error("Feedback HashMap is invalid: vote is not equal 1 or 2");
				throw new InvalidFeedbackException();
			}
		}
		logger.debug("Feedback is valid");
	}
	
	public void saveAll(List<RatingQuestion> ratingQuestionList) {
		ratingQuestionRepository.saveAll(ratingQuestionList);
	}
	
	public List<RatingQuestion> create(List<RatingObject> roList) {
		
		List<RatingQuestion> rqList = new ArrayList<RatingQuestion>();
		
		for (Iterator<?> roOneIterator = roList.iterator(); roOneIterator.hasNext();) {
			
			RatingObject roOne = (RatingObject) roOneIterator.next();
			
			for (Iterator<?> roTwoIterator = roList.iterator(); roTwoIterator.hasNext();) {
				
				RatingObject roTwo = (RatingObject) roTwoIterator.next();

				if(roOne.equals(roTwo)) break;
				
				RatingQuestion rqMale = new RatingQuestion();
				rqMale.setVotesOne(0);
				rqMale.setVotesTwo(0);
				rqMale.setCountVoted(0);
				rqMale.setGender(Gender.MALE);
				rqMale.setObjectOne(roOne);
				rqMale.setObjectTwo(roTwo);
				
				rqList.add(rqMale);
				logger.debug("Created rating question: " + rqMale.getGender() + ": " + rqMale.getObjectOne().toString() + " - " + rqMale.getObjectTwo().toString());
				
				RatingQuestion rqFemale = new RatingQuestion();
				rqFemale.setVotesOne(0);
				rqFemale.setVotesTwo(0);
				rqFemale.setCountVoted(0);
				rqFemale.setGender(Gender.FEMALE);
				rqFemale.setObjectOne(roOne);
				rqFemale.setObjectTwo(roTwo);

				rqList.add(rqFemale);
				logger.debug("Created rating question: " + rqFemale.getGender() + ": " + rqMale.getObjectOne().toString() + " - " + rqMale.getObjectTwo().toString());

			}
		}
		return rqList;
	}
	
	public int maxQuestionCountForNumberOfRatingObjects(int existingRatingItems) {
		
		int possibleQuestionCount = 0;
		for(int i=0; i<existingRatingItems; i++) {
			possibleQuestionCount+=i;
		}
		
		return possibleQuestionCount;
	}
}
