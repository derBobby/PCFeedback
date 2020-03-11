package eu.planlos.pcfeedback.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.planlos.pcfeedback.model.Gender;
import eu.planlos.pcfeedback.model.Project;
import eu.planlos.pcfeedback.model.UserAgent;
import eu.planlos.pcfeedback.repository.UserAgentRepository;

@Service
public class UserAgentService {

	private static final Logger LOG = LoggerFactory.getLogger(UserAgentService.class);
	
	@Autowired
	private UserAgentRepository uaRepo;
	
	public void saveUserAgent(Project project, String text, Gender gender) {
		uaRepo.save(new UserAgent(project, text, gender));
	}
	
	public List<UserAgent> findAllForProject(Project project) {
		return (List<UserAgent>) uaRepo.findAllByProject(project);
	}

	/**
	 * Deletes all UserAgent objects from DB
	 */
	public void resetDB() {
		LOG.debug("RESET: UserAgent");
		uaRepo.deleteAll();
	}
}
