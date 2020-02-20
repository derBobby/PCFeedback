package eu.planlos.pcfeedback.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.planlos.pcfeedback.exceptions.InvalidFeedbackException;
import eu.planlos.pcfeedback.exceptions.RatingQuestionsNotExistentException;
import eu.planlos.pcfeedback.model.Gender;
import eu.planlos.pcfeedback.model.RatingObject;
import eu.planlos.pcfeedback.model.RatingQuestion;
import eu.planlos.pcfeedback.repository.RatingQuestionRepository;

@Service
public class RatingQuestionService {

	private static final Logger LOG = LoggerFactory.getLogger(RatingQuestionService.class);

	private static final int OBJECT_ONE = 1;
	private static final int OBJECT_TWO = 2;

	@Value("${eu.planlos.pcfeedback.question-count}")
	public int neededQuestionCount;
	
	@Autowired
	private RatingQuestionRepository rqRepository;
	
	/**
	 * Returns all rating questions for given gender
	 * @param gender
	 * @return List<RatingQuestion>
	 */
	public List<RatingQuestion> loadByGender(Gender gender) {
		return rqRepository.findByGender(gender);
	}
	
	/**
	 * Returns rating questions for the feedback process for fiven gender
	 * @param gender
	 * @return List<RatingQuestion>
	 * @throws RatingQuestionsNotExistentException
	 */
	public void addRatingQuestionsForGenderToList(List<RatingQuestion> givenQuestions, Gender gender) throws RatingQuestionsNotExistentException {

		LOG.debug("Needed ratingQuestion count is: " + neededQuestionCount);
		
		LOG.debug("Get the lowest number a ratingQuestion is voted for gender: " + gender.toString());
		int lowestVotedCount = getLowestCountRatingQuestionIsVoted(gender);
		
		LOG.debug("Start adding ratingQuestions to result set");
		while(givenQuestions.size() <= neededQuestionCount) {
			
			// Load IDs with minimum count of answers
			LOG.debug("Load all questions for lowest number voted: " + lowestVotedCount);
			List<RatingQuestion> loadedQuestions = rqRepository.findByGenderAndCountVoted(gender, lowestVotedCount);
			loadedQuestions.removeAll(givenQuestions);
			
			LOG.debug("Shuffle rating questions in list");
			Collections.shuffle(loadedQuestions);

			// If exact amount is found
			if(loadedQuestions.size() == neededQuestionCount) {

				LOG.debug("Required count was loaded, add to result set and stop");
				givenQuestions.addAll(loadedQuestions);
				break;
			}
			
			//If more questions are available chose random ones
			if(loadedQuestions.size() > neededQuestionCount) {

				LOG.debug("More than required count was loaded, get random ratingQuestions of that list");
				List<RatingQuestion> shortenedList = loadedQuestions.subList(0, neededQuestionCount-givenQuestions.size());
				givenQuestions.addAll(shortenedList);
				break;
			}
						
			//If not enough questions are available
			if(loadedQuestions.size() < neededQuestionCount) {
				
				LOG.debug("Lesser than required count was loaded, get new lowest number voted");
				givenQuestions.addAll(loadedQuestions);
				lowestVotedCount = getLowestCountRatingQuestionIsVoted(gender, lowestVotedCount);		
			}
		}
		LOG.debug("End of adding ratingQuestions to result set");
	}

	private int getLowestCountRatingQuestionIsVoted(Gender gender) throws RatingQuestionsNotExistentException {
		return getLowestCountRatingQuestionIsVoted(gender, -1);
	}
	
	private int getLowestCountRatingQuestionIsVoted(Gender gender, int chosenCount) throws RatingQuestionsNotExistentException {

		// Get the count of the least voted question
		// so the least voted or least+1 voted questions can be loaded
		RatingQuestion lowestVotedCountQuestion = rqRepository.findFirstByCountVotedGreaterThanAndGenderOrderByCountVotedAsc(chosenCount, gender);
		
		if(lowestVotedCountQuestion == null) {
			LOG.error("No rating questions found with more votes than: " + chosenCount);
			throw new RatingQuestionsNotExistentException("No rating questions found with more votes than: " + chosenCount);
		}
					
		return lowestVotedCountQuestion.getCountVoted();
	}
	
	//TODO does this work? :D
	@Transactional
	public void saveFeedback(Map<Long, Integer> feedbackMap) throws InvalidFeedbackException {

		// Makes sure voteFor will be 1 or 2 
		LOG.debug("Check if feedback is valid");
		if(!isValidFeedback(feedbackMap)) {
			throw new InvalidFeedbackException("Feedback ist ungültig!");
		}
			
		LOG.debug("Save feedback to database");
		for(Long idRatingQuestion : feedbackMap.keySet()) {
			
			int voteFor = feedbackMap.get(idRatingQuestion);
			LOG.debug("Rating question " + idRatingQuestion + " got vote for rating object \"" + voteFor + "\"");
			
			if(voteFor == OBJECT_ONE) {
				rqRepository.addVoteForRatingObjectOne(idRatingQuestion);
				continue;
			}
			rqRepository.addVoteForRatingObjectTwo(idRatingQuestion);
		}
	}
	
	//TODO does this work? :D
	@Transactional
	public void removeFeedback(Map<Long, Integer> feedbackMap) {
		
		LOG.debug("Remove feedback from database");
		for(Long idRatingQuestion : feedbackMap.keySet()) {
			
			int voteFor = feedbackMap.get(idRatingQuestion);
			LOG.debug("Rating question " + idRatingQuestion + " gets vote for rating object \"" + voteFor + "\" removed");
			
			if(voteFor == OBJECT_ONE) {
				rqRepository.removeVoteForRatingObjectOne(idRatingQuestion);
				continue;
			}
			rqRepository.removeVoteForRatingObjectTwo(idRatingQuestion);
		}	
	}

	private boolean isValidFeedback(Map<Long, Integer> feedbackMap) {
		
		int givenQuestionCount = feedbackMap.size();
		boolean result = true;
		
		//has needed count
		if(givenQuestionCount == neededQuestionCount) {
			
			for(Long idRatingQuestion : feedbackMap.keySet()) {
				
				Integer voteFor = feedbackMap.get(idRatingQuestion);
	
				if(voteFor == null || (voteFor != OBJECT_ONE && voteFor != OBJECT_TWO)) {
					LOG.error("Feedback invalid. voteFor={}", voteFor);
					result = false;
					break;
				}
			}
			
		} else {
			LOG.error("Feedback invalid: questions needed/given={}/{}", neededQuestionCount, givenQuestionCount);
			result = false;
		}
		
		return result;
	}
	
	public void saveAll(List<RatingQuestion> rqList) {
		rqRepository.saveAll(rqList);
	}
	
	public List<RatingQuestion> create(List<RatingObject> roList) {
		
		List<RatingQuestion> rqList = new ArrayList<>();
		
		for (RatingObject roOne : roList) {
					
			for (RatingObject roTwo : roList) {
				
				if(roOne.equals(roTwo)) {
					break;
				}
				
				// --- MALE ---
				RatingQuestion rqMale = new RatingQuestion();
				rqMale.setVotesOne(0);
				rqMale.setVotesTwo(0);
				rqMale.setCountVoted(0);
				rqMale.setGender(Gender.MALE);
				rqMale.setObjectOne(roOne);
				rqMale.setObjectTwo(roTwo);
				
				rqList.add(rqMale);
				LOG.debug("Created rating question: {}: {} - {}",
						Gender.MALE,
						roOne.toString(),
						roTwo.toString());

				// --- FEMALE ---
				RatingQuestion rqFemale = new RatingQuestion();
				rqFemale.setVotesOne(0);
				rqFemale.setVotesTwo(0);
				rqFemale.setCountVoted(0);
				rqFemale.setGender(Gender.FEMALE);
				rqFemale.setObjectOne(roOne);
				rqFemale.setObjectTwo(roTwo);

				rqList.add(rqFemale);
				LOG.debug("Created rating question: {}: {} - {}",
						Gender.FEMALE,
						roOne.toString(),
						roTwo.toString());

			}
		}
		return rqList;
	}

	public List<RatingQuestion> reloadForInvalidFeedback(Gender gender, Map<Long, Integer> feedbackMap) throws RatingQuestionsNotExistentException {

		LOG.debug("Reloaded rating questions");
		List<RatingQuestion> reloadedList = (List<RatingQuestion>) rqRepository.findAllById(feedbackMap.keySet());
		
		List<RatingQuestion> rqList = new ArrayList<>();
		rqList.addAll(reloadedList);
		
		LOG.debug("Loading additional rating questions");
		addRatingQuestionsForGenderToList(rqList, gender);
		
		return rqList;
	}

	public void resetDB() {
		List<RatingQuestion> rqList = (List<RatingQuestion>) rqRepository.findAll();
		for(RatingQuestion rq : rqList) {
			rq.setCountVoted(0);
			rq.setVotesOne(0);
			rq.setVotesTwo(0);
		}
		rqRepository.saveAll(rqList);
	}
}
