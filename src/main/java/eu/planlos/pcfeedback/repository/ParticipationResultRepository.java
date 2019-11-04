package eu.planlos.pcfeedback.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import eu.planlos.pcfeedback.model.Participant;
import eu.planlos.pcfeedback.model.ParticipationResult;

@Repository
public interface ParticipationResultRepository extends CrudRepository<ParticipationResult, Long>{

	public ParticipationResult findByParticipant(Participant participant);
}
