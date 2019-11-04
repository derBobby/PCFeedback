package eu.planlos.pcfeedback.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.planlos.pcfeedback.model.Participant;
import eu.planlos.pcfeedback.model.ParticipationResult;
import eu.planlos.pcfeedback.repository.ParticipationResultRepository;

@Service
public class ParticipationResultService {

	@Autowired
	private ParticipationResultRepository prRepo;

	public void saveParticipationResult(Participant participant, Map<Long, Integer> feedbackMap) {
		
		ParticipationResult pr = new ParticipationResult(participant, feedbackMap);
		prRepo.save(pr);
	}
	
	public ParticipationResult findByParticipant(Participant participant) {
		return prRepo.findByParticipant(participant);
	}
}
