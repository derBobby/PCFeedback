package eu.planlos.pcfeedback.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import eu.planlos.pcfeedback.model.Participant;
import eu.planlos.pcfeedback.model.Project;

public interface ParticipantRepository extends CrudRepository<Participant, Long>{

	public List<Participant> findAllByProject(Project project);
	public boolean existsByProjectAndFirstnameAndName(Project project, String firstname, String name);
	public boolean existsByProjectAndEmail(Project project, String email);
	public boolean existsByProjectAndMobile(Project project, String mobile);
}
