package eu.planlos.pcfeedback.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.planlos.pcfeedback.model.db.Participant;
import eu.planlos.pcfeedback.model.db.ParticipationResult;
import eu.planlos.pcfeedback.model.db.Project;

@Repository
public interface ParticipationResultRepository extends CrudRepository<ParticipationResult, Long>{

	public ParticipationResult findByParticipant(Participant participant);

	@Transactional
	public void deleteByProject(Project project);

	public List<ParticipationResult> findAllByProject(Project project);
}
