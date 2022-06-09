package eu.planlos.pcfeedback.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import eu.planlos.pcfeedback.model.db.Participant;
import eu.planlos.pcfeedback.model.db.Project;

public interface ParticipantRepository extends CrudRepository<Participant, Long>{

	public List<Participant> findAllByProject(Project project);
	public boolean existsByProjectAndFirstnameAndName(Project project, String firstname, String name);
	public boolean existsByProjectAndEmail(Project project, String email);
	public boolean existsByProjectAndMobile(Project project, String mobile);
	@Transactional
	public void deleteByProject(Project project);


	public Participant findAllByProjectAndFirstnameAndName(Project project, String firstname, String name);
}
