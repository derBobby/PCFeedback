package eu.planlos.pcfeedback.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import eu.planlos.pcfeedback.model.Project;

@Repository
public interface ProjectRepository extends CrudRepository<Project, Long>{
	public Project findByProjectName(String projectName);
	public List<Project> findAll();
	public Project findByIdProject(Long idProject);
	public List<Project> findAllByActiveAndStartDateLessThanAndEndDateGreaterThan(boolean b, Date date, Date date2);
}
