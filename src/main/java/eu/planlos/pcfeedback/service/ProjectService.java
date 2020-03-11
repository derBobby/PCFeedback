package eu.planlos.pcfeedback.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.planlos.pcfeedback.model.Project;
import eu.planlos.pcfeedback.repository.ProjectRepository;

@Service
public class ProjectService {

	private static final Logger LOG = LoggerFactory.getLogger(ProjectService.class);

	@Autowired
	private ProjectRepository projectRepo;

	public void save(Project project) {
		projectRepo.save(project);
	}

	public boolean exists(String projectName) {
		Project project = projectRepo.findByName(projectName);
		return project != null ? true : false;
	}

	public Project findProject(String projectName) {
		return projectRepo.findByName(projectName);
	}

	public void resetDB() {
		LOG.debug("RESET: Project");
		projectRepo.deleteAll();
		return;
	}
}
