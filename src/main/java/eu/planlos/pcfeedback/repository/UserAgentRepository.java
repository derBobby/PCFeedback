package eu.planlos.pcfeedback.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.planlos.pcfeedback.model.db.Project;
import eu.planlos.pcfeedback.model.db.UserAgent;

@Repository
public interface UserAgentRepository extends CrudRepository<UserAgent, Long> {
	public List<UserAgent> findAllByProject(Project project);

	@Transactional
	public void deleteByProject(Project project);
}
