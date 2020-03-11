package eu.planlos.pcfeedback.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import eu.planlos.pcfeedback.model.Project;
import eu.planlos.pcfeedback.model.UserAgent;

@Repository
public interface UserAgentRepository extends CrudRepository<UserAgent, Long> {
	public List<UserAgent> findAllByProject(Project project);
}
