package eu.planlos.pcfeedback.service;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import eu.planlos.pcfeedback.exceptions.ProjectAlreadyExistingException;
import eu.planlos.pcfeedback.model.db.Project;
import eu.planlos.pcfeedback.repository.ProjectRepository;
import eu.planlos.pcfeedback.util.ZonedDateTimeUtility;

@Service
public class ProjectService {

	private static final Logger LOG = LoggerFactory.getLogger(ProjectService.class);

	@Autowired
	private ProjectRepository projectRepo;

	//TODO Test this
	public void save(Project project) throws ProjectAlreadyExistingException {
		try {
			projectRepo.save(project);
		} catch (DataIntegrityViolationException e) {
			LOG.debug(e.getMessage());
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
		LOG.debug("RESET: Project");
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

	public boolean isOnline(Project project) {

		if (!project.isActive()) {
			LOG.debug("Project '{}' not active", project.getProjectName());
			return false;
		}

		ZonedDateTime now = ZonedDateTimeUtility.nowCET();
		ZonedDateTime projectStart = project.getProjectStart();
		ZonedDateTime projectEnd = project.getProjectEnd();
		
		if (now.isBefore(projectStart)) {
			LOG.debug("'{}' before '{}'",
					ZonedDateTimeUtility.nice(now),
					ZonedDateTimeUtility.nice(projectStart));
			return false;
		}
		if (now.isAfter(projectEnd)) {
			LOG.debug("'{}' after '{}'",
					ZonedDateTimeUtility.nice(now),
					ZonedDateTimeUtility.nice(projectEnd));
			return false;
		}

		LOG.debug("Start='{}'   <   Now='{}'   <   End='{}'",
				projectStart.toString(),
				now.toString(),
				projectEnd.toString());

		return true;
	}

	public void resetProject(Project project) {
		project.setActive(false);
		projectRepo.save(project);		
	}

	public void deleteProject(Project project) {
		projectRepo.delete(project);		
	}
}
