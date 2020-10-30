package eu.planlos.pcfeedback.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.planlos.pcfeedback.model.Gender;
import eu.planlos.pcfeedback.model.db.FreeText;
import eu.planlos.pcfeedback.model.db.Project;
import eu.planlos.pcfeedback.repository.FreeTextRepository;

@Service
public class FreeTextService {

	private static final Logger LOG = LoggerFactory.getLogger(FreeTextService.class);
	
	@Autowired
	private FreeTextRepository ftRepo;
	
	public void createAndSaveFreeText(Project project, String text, Gender gender) {
		
		LOG.debug("Saving free text with length={}", text.length());
		ftRepo.save(new FreeText(project, text, gender));
	}
	
	public List<FreeText> findAllByProject(Project project) {
		
		return (List<FreeText>) ftRepo.findAllByProject(project);
	}

	/**
	 * Deletes all FreeText objects from DB
	 */
	public void resetDB() {
		LOG.debug("RESET: FreeText");
		ftRepo.deleteAll();		
	}
	
	public void resetProject(Project project) {
		ftRepo.deleteByProject(project);
	}
}
