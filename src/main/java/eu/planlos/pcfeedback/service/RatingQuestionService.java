package eu.planlos.pcfeedback.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.planlos.pcfeedback.exceptions.RatingQuestionsNotExistentException;
import eu.planlos.pcfeedback.exceptions.WrongRatingQuestionCountExistingException;
import eu.planlos.pcfeedback.model.Gender;
import eu.planlos.pcfeedback.model.db.Project;
import eu.planlos.pcfeedback.model.db.RatingObject;
import eu.planlos.pcfeedback.model.db.RatingQuestion;
import eu.planlos.pcfeedback.repository.RatingQuestionRepository;

@Service
public class RatingQuestionService {

	private static final Logger LOG = LoggerFactory.getLogger(RatingQuestionService.class);

	public static final int OBJECT_ONE = 1;
	public static final int OBJECT_TWO = 2;
	
	@Autowired
	private RatingQuestionRepository rqRepository;
	
	/**
	 * Returns all rating questions for given gender
	 * @param gender
	 * @return List<RatingQuestion>
	 */
	public List<RatingQuestion> loadByProjectAndGender(Project project, Gender gender) {
		return rqRepository.findByProjectAndGender(project, gender);
	}
	
	/**
	 * Returns rating questions for the feedback process for fiven gender
	 * @param gender
	 * @return List<RatingQuestion>
	 * @throws RatingQuestionsNotExistentException
	 */
	public void addRatingQuestionsForProjectAndGenderToList(List<RatingQuestion> givenQuestions, Project project, Gender gender) throws RatingQuestionsNotExistentException {

		int totalQuestionCount = project.getRatingQuestionCount();
		
		int neededQuestionCount = totalQuestionCount - givenQuestions.size();
		
		LOG.debug("Needed ratingQuestion count is: {}", neededQuestionCount);
		
		LOG.debug("Get the lowest number a ratingQuestion is voted for gender: {}", gender.toString());
		int lowestVotedCount = getLowestCountRatingQuestionIsVoted(project, gender);
		
		LOG.debug("Start adding ratingQuestions to result set");
		while(givenQuestions.size() <= totalQuestionCount) {
			
			// Load IDs with minimum count of answers
			LOG.debug("Load all questions for lowest number voted: {}", lowestVotedCount);
			List<RatingQuestion> loadedQuestions = rqRepository.findByProjectAndGenderAndCountVoted(project, gender, lowestVotedCount);
			loadedQuestions.removeAll(givenQuestions);
			
			// If exact count is found
			if(loadedQuestions.size() == neededQuestionCount) {

				LOG.debug("Required count was loaded, add to result set and stop");
				givenQuestions.addAll(loadedQuestions);
				break;
			}
			
			//If more questions are available chose random ones
			if(loadedQuestions.size() > neededQuestionCount) {

				LOG.debug("Shuffle rating questions in list");
				Collections.shuffle(loadedQuestions);
				
				LOG.debug("More than required count was loaded, get random ratingQuestions of that list");
				List<RatingQuestion> shortenedList = loadedQuestions.subList(0, totalQuestionCount-givenQuestions.size());
				givenQuestions.addAll(shortenedList);
				break;
			}
						
			//If not enough questions are available
			if(loadedQuestions.size() < neededQuestionCount) {
				
				LOG.debug("Lesser than required count was loaded, get new lowest number voted");
				givenQuestions.addAll(loadedQuestions);
				lowestVotedCount = getLowestCountRatingQuestionIsVoted(project, gender, lowestVotedCount);		
			}
		}
	}

	private int getLowestCountRatingQuestionIsVoted(Project project, Gender gender) throws RatingQuestionsNotExistentException {
		return getLowestCountRatingQuestionIsVoted(project, gender, -1);
	}
	
	private int getLowestCountRatingQuestionIsVoted(Project project, Gender gender, int chosenCount) throws RatingQuestionsNotExistentException {

		// Get the count of the least voted question
		// so the least voted or least+1 voted questions can be loaded
		RatingQuestion lowestVotedCountQuestion = rqRepository.findFirstByProjectAndCountVotedGreaterThanAndGenderOrderByCountVotedAsc(project, chosenCount, gender);
		
		if(lowestVotedCountQuestion == null) {
			String message = String.format("No rating questions found with more votes than: %s", chosenCount);
			LOG.error(message);
			throw new RatingQuestionsNotExistentException(message);
		}
					
		return lowestVotedCountQuestion.getCountVoted();
	}
	
	//TODO MONGO Transactional working?
	@Transactional
	public void saveFeedback(Map<Long, Integer> feedbackMap) {
			
		LOG.debug("Save feedback to database");
		for(Long idRatingQuestion : feedbackMap.keySet()) {
			
			int voteFor = feedbackMap.get(idRatingQuestion);
			LOG.debug("Rating question {} got vote for rating object {}", idRatingQuestion, voteFor);
			
			if(voteFor == OBJECT_ONE) {
				rqRepository.addVoteForRatingObjectOne(idRatingQuestion);
				continue;
			}
			rqRepository.addVoteForRatingObjectTwo(idRatingQuestion);
		}
	}
	
	//TODO MONGO Transactional working?
	@Transactional
	public void removeFeedback(Map<Long, Integer> feedbackMap) {
		
		LOG.debug("Remove feedback from database");
		for(Long idRatingQuestion : feedbackMap.keySet()) {
			
			int voteFor = feedbackMap.get(idRatingQuestion);
			LOG.debug("Rating question {} gets vote for rating object {} removed", idRatingQuestion, voteFor);
			
			if(voteFor == OBJECT_ONE) {
				rqRepository.removeVoteForRatingObjectOne(idRatingQuestion);
				continue;
			}
			rqRepository.removeVoteForRatingObjectTwo(idRatingQuestion);
		}	
	}

	public void saveAll(List<RatingQuestion> rqList) {
		rqRepository.saveAll(rqList);
	}
	
	public List<RatingQuestion> create(Project project) {
		
		List<RatingQuestion> rqList = new ArrayList<>();
		List<RatingObject> roList = project.getRatingObjectList();
		for (RatingObject roOne : roList) {
					
			for (RatingObject roTwo : roList) {
				
				if(roOne.equals(roTwo)) {
					break;
				}
				
				// --- MALE ---
				RatingQuestion rqMale = new RatingQuestion();
				rqMale.setProject(project);
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
				rqFemale.setProject(project);
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

	//TODO what happens here, when??
	public List<RatingQuestion> reloadForInvalidFeedback(Project project, Gender gender, Map<Long, Integer> feedbackMap) throws RatingQuestionsNotExistentException {

		if(feedbackMap == null) {
			feedbackMap = new HashMap<>();
		}
		
		LOG.debug("Reloaded rating questions");
		List<RatingQuestion> reloadedList = (List<RatingQuestion>) rqRepository.findAllById(feedbackMap.keySet());
		
		List<RatingQuestion> rqList = new ArrayList<>();
		rqList.addAll(reloadedList);
		
		LOG.debug("Loading additional rating questions");
		addRatingQuestionsForProjectAndGenderToList(rqList, project, gender);
		
		return rqList;
	}

	public void resetDB() {
		List<RatingQuestion> rqList = (List<RatingQuestion>) rqRepository.findAll();
		for(RatingQuestion rq : rqList) {
			rq.setCountVoted(0);
			rq.setVotesOne(0);
			rq.setVotesTwo(0);
		}
		LOG.debug("RESET: RatingQuestion");
		rqRepository.saveAll(rqList);
	}
	
	/**
	 * Checks if sufficient rating questions exist
	 * @param proactive is true if method is called proactively and ERROR output is not necessary.
	 * @throws WrongRatingQuestionCountExistingException 
	 */
	public void checkEnoughRatingQuestions(Project project, boolean proactive) throws WrongRatingQuestionCountExistingException {
		
		int givenRatingObjectCount = project.getRatingObjectList().size();
		
		int neededQuestionCount = project.getRatingQuestionCount();
		int possibleQuestionCount = calcPossible(givenRatingObjectCount - 1);

		if(possibleQuestionCount < neededQuestionCount) {
			if(!proactive) {
				LOG.error("# ~~~~~~~~ Not correct count of rating questions available! ~~~~~~~~ #");
			}
			throw new WrongRatingQuestionCountExistingException(String.format("Die Bewertungsobjekte reichen nur für %s von nötigen %s Paare.", possibleQuestionCount, neededQuestionCount));
		}
	}
	
	private int calcPossible(int given) {
		if(given == 1) {
			return 1;
		}
		return given + calcPossible(given - 1);
	}

	public RatingQuestion findByIdRatingQuestion(long idRatingQuestion) {
		return rqRepository.findByIdRatingQuestion(idRatingQuestion);
	}

	public void resetProject(Project project) {
		rqRepository.deleteByProject(project);
	}
	
	public int getRatingQuestionCountFor(Project project) {
		return rqRepository.countByProjectAndGender(project, Gender.MALE);
	}
}
