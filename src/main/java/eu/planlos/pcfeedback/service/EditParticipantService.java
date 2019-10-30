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
public class EditParticipantService {
	
	private static final Logger logger = LoggerFactory.getLogger(EditParticipantService.class);
	
	@Autowired
	private ParticipantService ps;
	
	@Autowired
	private RatingQuestionService rqs;

	@Autowired
	private RatingQuestionRepository rqr;
	
	@Autowired
	private ParticipationResultService prs;
	
	/**
	 * Methods either saves Participant, if gender ist kept or does pretty much everything if gender is changed.
	 * Means removing votes from old RatingQuestion and adding votes to new RatingQuestion.
	 * Since RatingQuestion has member Gender.
	 * @param participant
	 * @return Returns true if gender is changed
	 * @throws ParticipantNotFoundException
	 */
	public boolean saveParticipantAndCorrectRatingQuestions(Participant participant) throws ParticipantNotFoundException {
		
		boolean genderChanged = ps.isGenderChanged(participant);
		
		ps.saveEdited(participant);

		if(! genderChanged) {
			return false;
		}
		
		ParticipationResult participationResult = prs.findByParticipant(participant);
		Map<Long, Integer> feedbackMap = participationResult.getFeedbackMap();
		Gender wantedGender = participant.getGender();
		
		Map<Long, Integer> newFeedbackMap = new HashMap<>();
		
		for(Map.Entry<Long, Integer> entry : feedbackMap.entrySet()) {
			Long idRatingQuestion = entry.getKey();
		    Integer votedObject = entry.getValue();
		    
		    RatingQuestion oldRatingQuestion = rqr.findById(idRatingQuestion).get();
		    RatingObject ratingObjectOne = oldRatingQuestion.getObjectOne();
		    RatingObject ratingObjectTwo = oldRatingQuestion.getObjectTwo();
		    
		    RatingQuestion newRatingQuestion = rqr.findByGenderAndObjectOneAndObjectTwo(wantedGender, ratingObjectOne, ratingObjectTwo);
		    
		    newFeedbackMap.put(newRatingQuestion.getIdRatingQuestion(), votedObject);
		}
		
		try {
			rqs.removeFeedback(feedbackMap);
			rqs.saveFeedback(newFeedbackMap);
		} catch (InvalidFeedbackException e) {
			logger.error("Kritischer Fehler beim Neuanlegen der Feedbackergebnisse nach Gender-Änderung.");
			e.printStackTrace();
		}
		
		return true;
	}
}