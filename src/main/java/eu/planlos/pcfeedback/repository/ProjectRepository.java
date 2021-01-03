package eu.planlos.pcfeedback.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import eu.planlos.pcfeedback.model.db.Project;

@Repository
public interface ProjectRepository extends CrudRepository<Project, Long>{
	public Project findByProjectName(String projectName);
	public Project findByIdProject(Long idProject);
	public List<Project> findAllByActiveAndProjectStartInstantLessThanAndProjectEndInstantGreaterThan(boolean active, Instant now, Instant now2);
}
