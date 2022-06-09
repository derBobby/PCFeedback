package eu.planlos.pcfeedback.service;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import eu.planlos.pcfeedback.exceptions.ProjectAlreadyExistingException;
import eu.planlos.pcfeedback.model.db.Project;
import eu.planlos.pcfeedback.repository.ProjectRepository;
import eu.planlos.pcfeedback.util.ZonedDateTimeUtility;

@Slf4j
@Service
public class ProjectService {

	private ProjectRepository projectRepo;

	public ProjectService(ProjectRepository projectRepo) {
		this.projectRepo = projectRepo;
	}
	
	//TODO Test this
	public void save(Project project) throws ProjectAlreadyExistingException {
		try {
			projectRepo.save(project);
		} catch (DataIntegrityViolationException e) {
			log.debug(e.getMessage());
			throw new ProjectAlreadyExistingException(String.format("Projekt mit Namen '{}' existiert bereits", project.getProjectName()));
		}
	}

	public boolean exists(String projectName) {
		Project project = projectRepo.findByProjectName(projectName);
		return project != null ? true : false;
	}

	public Project findProject(String projectName) {
		return projectRepo.findByProjectName(projectName);
	}

	public List<Project> findAll() {
		return (List<Project>) projectRepo.findAll();
	}

	public void resetDB() {
		log.debug("RESET: Project");
		projectRepo.deleteAll();
	}

	public Project findProject(Long idProject) {
		return projectRepo.findByIdProject(idProject);
	}

	public List<Project> getActive() {
		ZonedDateTime now = ZonedDateTimeUtility.nowUTC();
		Instant nowInstant = now.toInstant();
		return projectRepo.findAllByActiveAndProjectStartInstantLessThanAndProjectEndInstantGreaterThan(true, nowInstant, nowInstant);
	}

	public void resetProject(Project project) {
		project.setActive(false);
		projectRepo.save(project);		
	}

	public void deleteProject(Project project) {
		projectRepo.delete(project);		
	}
}
