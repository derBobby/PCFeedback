package eu.planlos.pcfeedback.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.planlos.pcfeedback.model.Participant;
import eu.planlos.pcfeedback.model.ParticipationResult;
import eu.planlos.pcfeedback.repository.ParticipationResultRepository;

@Service
public class ParticipationResultService {

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

	public void resetDB() {
		prRepo.deleteAll();
	}
}
