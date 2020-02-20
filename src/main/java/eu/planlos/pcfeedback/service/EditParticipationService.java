package eu.planlos.pcfeedback.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.planlos.pcfeedback.exceptions.InvalidFeedbackException;
import eu.planlos.pcfeedback.exceptions.ParticipantNotFoundException;
import eu.planlos.pcfeedback.model.Gender;
import eu.planlos.pcfeedback.model.Participant;
import eu.planlos.pcfeedback.model.ParticipationResult;
import eu.planlos.pcfeedback.model.RatingObject;
import eu.planlos.pcfeedback.model.RatingQuestion;
import eu.planlos.pcfeedback.repository.RatingQuestionRepository;

@Service
public class EditParticipationService {
	
	private static final Logger LOG = LoggerFactory.getLogger(EditParticipationService.class);
	
	@Autowired
	private ParticipantService pService;
	
	@Autowired
	private RatingQuestionService rqService;

	@Autowired
	private RatingQuestionRepository rqRepository;
	
	@Autowired
	private ParticipationResultService prService;
	
	/**
	 * Methods either saves Participant, if gender ist kept or does pretty much everything if gender is changed.
	 * Means removing votes from old RatingQuestion and adding votes to new RatingQuestion.
	 * Since RatingQuestion has member Gender.
	 * @param participant
	 * @return Returns true if gender is changed
	 * @throws ParticipantNotFoundException
	 */
	public boolean editParticipant(Participant participant) throws ParticipantNotFoundException {
		
		boolean genderChanged = pService.isGenderSame(participant);
		
		pService.saveEdited(participant);

		if(! genderChanged) {
			return false;
		}
		
		LOG.debug("Gender was changed, need to update a lot including RatingQuestion change (depending on gender)");
		ParticipationResult participationResult = prService.findByParticipant(participant);
		Map<Long, Integer> feedbackMap = participationResult.getFeedbackMap();
		Gender wantedGender = participant.getGender();
		
		Map<Long, Integer> newFeedbackMap = new HashMap<>();
		
		for(Map.Entry<Long, Integer> entry : feedbackMap.entrySet()) {
			Long idRatingQuestion = entry.getKey();
		    Integer votedObject = entry.getValue();
		    
		    RatingQuestion oldRatingQuestion = rqRepository.findById(idRatingQuestion).get();
		    RatingObject ratingObjectOne = oldRatingQuestion.getObjectOne();
		    RatingObject ratingObjectTwo = oldRatingQuestion.getObjectTwo();
		    
		    RatingQuestion newRatingQuestion = rqRepository.findByGenderAndObjectOneAndObjectTwo(wantedGender, ratingObjectOne, ratingObjectTwo);
		    
		    newFeedbackMap.put(newRatingQuestion.getIdRatingQuestion(), votedObject);
		}
		LOG.debug("Old RatingQuestions: {}", participationResult.printKeyList());
		participationResult.setFeedbackMap(newFeedbackMap);
		LOG.debug("New RatingQuestions: {}", participationResult.printKeyList());
		try {
			rqService.removeFeedback(feedbackMap);
			rqService.saveFeedback(newFeedbackMap);
			prService.saveParticipationResult(participationResult);
		} catch (InvalidFeedbackException e) {
			LOG.error("Kritischer Fehler beim Neuanlegen der Feedbackergebnisse nach Gender-Änderung.");
		}
		
		return true;
	}
}
