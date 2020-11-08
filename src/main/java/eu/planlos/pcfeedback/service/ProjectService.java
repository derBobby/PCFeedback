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
import eu.planlos.pcfeedback.util.ZonedDateTimeHelper;

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
		return projectRepo.findAll();
	}

	public void resetDB() {
		LOG.debug("RESET: Project");
		projectRepo.deleteAll();
		return;
	}

	public Project findProject(Long idProject) {
		return projectRepo.findByIdProject(idProject);
	}

	public List<Project> getActive() {
		ZonedDateTime now = ZonedDateTimeHelper.nowUTC();
		Instant nowInstant = now.toInstant();
		return projectRepo.findAllByActiveAndStartInstantLessThanAndEndInstantGreaterThan(true, nowInstant, nowInstant);
	}

	public boolean isOnline(Project project) {

		ZonedDateTime now = ZonedDateTimeHelper.nowCET();

		ZonedDateTime projectStart = project.getStartZonedDateTime();
		ZonedDateTime projectEnd = project.getEndZonedDateTime();

		if (!project.isActive()) {
			LOG.debug("Project '{}' not active", project.getProjectName());
			return false;
		}

		if (now.isBefore(projectStart)) {
			LOG.debug("'{}' before '{}'",
					ZonedDateTimeHelper.nice(now),
					ZonedDateTimeHelper.nice(projectStart));
			return false;
		}
		if (now.isAfter(projectEnd)) {
			LOG.debug("'{}' after '{}'",
					ZonedDateTimeHelper.nice(now),
					ZonedDateTimeHelper.nice(projectEnd));
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
