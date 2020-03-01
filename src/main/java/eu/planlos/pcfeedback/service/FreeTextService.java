package eu.planlos.pcfeedback.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.planlos.pcfeedback.model.FreeText;
import eu.planlos.pcfeedback.model.Gender;
import eu.planlos.pcfeedback.repository.FreeTextRepository;

@Service
public class FreeTextService {

	private static final Logger LOG = LoggerFactory.getLogger(FreeTextService.class);
	
	@Autowired
	private FreeTextRepository ftRepo;
	
	public void saveFreeText(String text, Gender gender) {
		
		
		FreeText freeText = new FreeText();
		freeText.setText(text);
		freeText.setGender(gender);
		
		LOG.debug("Saving free text with length={}", text.length());
		ftRepo.save(freeText);
	}
	
	public List<FreeText> findAll() {
		
		return (List<FreeText>) ftRepo.findAll();
	}

	/**
	 * Deletes all FreeText objects from DB
	 */
	public void resetDB() {
		LOG.debug("RESET: FreeText");
		ftRepo.deleteAll();		
	}
}
