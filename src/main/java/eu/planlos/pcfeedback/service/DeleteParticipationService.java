package eu.planlos.pcfeedback.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.planlos.pcfeedback.exceptions.ParticipantNotFoundException;
import eu.planlos.pcfeedback.model.Participant;
import eu.planlos.pcfeedback.model.ParticipationResult;

@Service
public class DeleteParticipationService {
	
	private static final Logger LOG = LoggerFactory.getLogger(DeleteParticipationService.class);
	
	@Autowired
	private ParticipantService ps;
	
	@Autowired
	private RatingQuestionService rqs;
	
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
	public void deleteParticipant(Participant participant) throws ParticipantNotFoundException {
		
		ParticipationResult participationResult = prs.findByParticipant(participant);
		Map<Long, Integer> feedbackMap = participationResult.getFeedbackMap();
		
		LOG.debug("Removing participants personal feedback entry");
		rqs.removeFeedback(feedbackMap);
		
		LOG.debug("Deleting...");
		prs.deleteParticipationResult(participationResult);
		
		LOG.debug("Removing participant");
		ps.delete(participant);
	}
}
