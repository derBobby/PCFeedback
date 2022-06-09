package eu.planlos.pcfeedback.service;

import eu.planlos.pcfeedback.exceptions.InvalidFeedbackException;
import eu.planlos.pcfeedback.exceptions.RatingQuestionsNotExistentException;
import eu.planlos.pcfeedback.exceptions.WrongRatingQuestionCountExistingException;
import eu.planlos.pcfeedback.model.FeedbackDTO;
import eu.planlos.pcfeedback.model.Gender;
import eu.planlos.pcfeedback.model.db.Project;
import eu.planlos.pcfeedback.model.db.RatingObject;
import eu.planlos.pcfeedback.model.db.RatingQuestion;
import eu.planlos.pcfeedback.repository.RatingQuestionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class RatingQuestionService {

	private final RatingQuestionRepository rqRepository;
	
	public RatingQuestionService(RatingQuestionRepository rqRepository) {
		this.rqRepository = rqRepository;
	}
	
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
				
		log.debug("Needed ratingQuestion count for project is: {}", totalQuestionCount);
		
		log.debug("Get the lowest number a ratingQuestion is voted for gender: {}", gender.toString());
		int lowestVotedCount = getLowestCountRatingQuestionIsVoted(project, gender);
		
		log.debug("Start adding ratingQuestions to result set");
		while(givenQuestions.size() <= totalQuestionCount) {
			
			int remainingQuestionCount = totalQuestionCount - givenQuestions.size();
			log.debug("Question count remaining to load is '{}'", remainingQuestionCount);
			
			// Load IDs with minimum count of answers
			log.debug("Load all questions for lowest number voted: {}", lowestVotedCount);
			List<RatingQuestion> loadedQuestions = rqRepository.findByProjectAndGenderAndCountVoted(project, gender, lowestVotedCount);
			loadedQuestions.removeAll(givenQuestions);
			
			// If exact count is found
			if(loadedQuestions.size() == remainingQuestionCount) {

				log.debug("Required count was loaded, add to result set and stop");
				givenQuestions.addAll(loadedQuestions);
				break;
			}
			
			//If more questions are available chose random ones
			if(loadedQuestions.size() > remainingQuestionCount) {

				log.debug("Shuffle rating questions in list");
				Collections.shuffle(loadedQuestions);
				
				log.debug("More than required count was loaded, get random ratingQuestions of that list");
				List<RatingQuestion> shortenedList = loadedQuestions.subList(0, totalQuestionCount-givenQuestions.size());
				givenQuestions.addAll(shortenedList);
				break;
			}
						
			//If not enough questions are available
			if(loadedQuestions.size() < remainingQuestionCount) {
				
				log.debug("Lesser than required count was loaded, get new lowest number voted");
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
			String message = String.format("FATAL - No rating questions found with more votes than: %s", chosenCount);
			log.error(message);
			throw new RatingQuestionsNotExistentException(message);
		}
					
		return lowestVotedCountQuestion.getCountVoted();
	}
	
	@Transactional
	public void saveFeedback(Map<Long, Integer> feedbackMap) {
			
		log.debug("Save feedback to database");
		for(Long idRatingQuestion : feedbackMap.keySet()) {
			
			int voteFor = feedbackMap.get(idRatingQuestion);
			log.debug("Rating question {} got vote for rating object {}", idRatingQuestion, voteFor);
			
			if(voteFor == FeedbackDTO.OBJECT_ONE) {
				rqRepository.addVoteForRatingObjectOne(idRatingQuestion);
				continue;
			}
			rqRepository.addVoteForRatingObjectTwo(idRatingQuestion);
		}
	}
	
	@Transactional
	public void removeFeedback(Map<Long, Integer> feedbackMap) {
		
		log.debug("Remove feedback from database");
		for(Long idRatingQuestion : feedbackMap.keySet()) {
			
			int voteFor = feedbackMap.get(idRatingQuestion);
			log.debug("Rating question {} gets vote for rating object {} removed", idRatingQuestion, voteFor);
			
			if(voteFor == FeedbackDTO.OBJECT_ONE) {
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
				log.debug("Created rating question: {}: {} - {}",
						Gender.MALE,
						roOne,
						roTwo);

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
				log.debug("Created rating question: {}: {} - {}",
						Gender.FEMALE,
						roOne,
						roTwo);

			}
		}
		return rqList;
	}

	public List<RatingQuestion> reloadForInvalidFeedback(Project project, Gender gender, Map<Long, Integer> feedbackMap) throws RatingQuestionsNotExistentException, InvalidFeedbackException {

		if(feedbackMap == null) {
			throw new InvalidFeedbackException("No feedback map was given.");
		}
		
		log.debug("Reloaded rating questions");
		List<RatingQuestion> reloadedList = (List<RatingQuestion>) rqRepository.findAllById(feedbackMap.keySet());
		
		List<RatingQuestion> rqList = new ArrayList<>();
		rqList.addAll(reloadedList);
		
		log.debug("Loading additional rating questions");
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
		log.debug("RESET: RatingQuestion");
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
				log.error("# ~~~~~~~~ Not correct count of rating questions available! ~~~~~~~~ #");
			}
			throw new WrongRatingQuestionCountExistingException(String.format("Die Bewertungsobjekte reichen nur für %s von nötigen %s Paare.", possibleQuestionCount, neededQuestionCount));
		}
	}
	
	/**
	 * Calculates the possible rating question count
	 * for a given numer of rating objects 
	 * @param given
	 * @return
	 */
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

	public List<RatingQuestion> loadByProject(Project project) {
		return rqRepository.findByProject(project);
	}

	public RatingQuestion findByGenderAndObjectOneAndObjectTwo(Gender wantedGender, RatingObject ratingObjectOne,
			RatingObject ratingObjectTwo) {
		return rqRepository.findByGenderAndObjectOneAndObjectTwo(wantedGender, ratingObjectOne, ratingObjectTwo);
	}
}
