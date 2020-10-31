package eu.planlos.pcfeedback.service;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import eu.planlos.pcfeedback.exceptions.ProjectAlreadyExistingException;
import eu.planlos.pcfeedback.model.db.Project;
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
			throw new ProjectAlreadyExistingException(String.format("Projekt mit Namen {} existiert bereits", project.getProjectName()));
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
		return;
	}

	public Project findProject(Long idProject) {
		return projectRepo.findByIdProject(idProject);
	}

	public List<Project> getActive() {
		Date date = new Date();
		return projectRepo.findAllByActiveAndStartDateLessThanAndEndDateGreaterThan(true, date, date);
	}

	public boolean isOnline(Project project) {
		
		if(! project.isActive()) {
			return false;
		}
		
		Date now = new Date();
		if(now.before(project.getStartDate())) {
			return false;
		}
		if(now.after(project.getEndDate())) {
			return false;
		}
			
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
