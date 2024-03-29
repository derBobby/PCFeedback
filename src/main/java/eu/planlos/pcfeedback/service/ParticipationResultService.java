package eu.planlos.pcfeedback.service;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.planlos.pcfeedback.model.db.Participant;
import eu.planlos.pcfeedback.model.db.ParticipationResult;
import eu.planlos.pcfeedback.model.db.Project;
import eu.planlos.pcfeedback.repository.ParticipationResultRepository;

@Slf4j
@Service
public class ParticipationResultService {

	private ParticipationResultRepository prRepo;

	public ParticipationResultService(ParticipationResultRepository prRepo) {
		this.prRepo = prRepo;
	}
	
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
		log.debug("RESET: ParticipantResult");
		prRepo.deleteAll();
	}

	public void resetProject(Project project) {
		prRepo.deleteByProject(project);		
	}

	public List<ParticipationResult> findAllByProject(Project project) {
		return prRepo.findAllByProject(project);
	}
}
