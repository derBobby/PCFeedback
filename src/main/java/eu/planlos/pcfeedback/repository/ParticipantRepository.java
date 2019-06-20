package eu.planlos.pcfeedback.repository;

import org.springframework.data.repository.CrudRepository;

import eu.planlos.pcfeedback.model.Participant;

public interface ParticipantRepository extends CrudRepository<Participant, Long>{

	public boolean existsByPrenameAndName(String prename, String name);
	public boolean existsByEmail(String email);
	public boolean existsByMobile(String mobile);
	public boolean existsByIdParticipantAndFeedbackCompleted(long idParticipant, boolean b);
}
