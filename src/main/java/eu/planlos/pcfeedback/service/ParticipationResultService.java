package eu.planlos.pcfeedback.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.planlos.pcfeedback.model.Participant;
import eu.planlos.pcfeedback.model.ParticipationResult;
import eu.planlos.pcfeedback.repository.ParticipationResultRepository;

@Service
public class ParticipationResultService {

	private static final Logger LOG = LoggerFactory.getLogger(ParticipationResultService.class);

	@Autowired
	private ParticipationResultRepository prRepo;
	
	public ParticipationResult findByParticipant(Participant participant) {
		return prRepo.findByParticipant(participant);
	}

	public void saveParticipationResult(ParticipationResult participationResult) {
		prRepo.save(participationResult);
	}

	public void deleteParticipationResult(ParticipationResult participationResult) {
		prRepo.delete(participationResult);
	}

	/**
	 * Deletes all ParticipantResult objects from DB
	 */
	public void resetDB() {
		LOG.debug("RESET: ParticipantResult");
		prRepo.deleteAll();
	}
}
