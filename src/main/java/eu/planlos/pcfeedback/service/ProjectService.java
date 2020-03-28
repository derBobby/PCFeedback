package eu.planlos.pcfeedback.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import eu.planlos.pcfeedback.exceptions.ProjectAlreadyExistingException;
import eu.planlos.pcfeedback.model.Project;
import eu.planlos.pcfeedback.repository.ProjectRepository;

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
			throw new ProjectAlreadyExistingException();
		}
	}

	public boolean exists(String projectName) {
		Project project = projectRepo.findByName(projectName);
		return project != null ? true : false;
	}

	public Project findProject(String projectName) {
		return projectRepo.findByName(projectName);
	}

	public List<Project> findAll() {
		return (List<Project>) projectRepo.findAll();
	}

	public void resetDB() {
		LOG.debug("RESET: Project");
		projectRepo.deleteAll();
		return;
	}
}
