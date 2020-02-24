package eu.planlos.pcfeedback.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.planlos.pcfeedback.model.FreeText;
import eu.planlos.pcfeedback.model.Gender;
import eu.planlos.pcfeedback.repository.FreeTextRepository;

@Service
public class FreeTextService {

	@Autowired
	private FreeTextRepository ftRepo;
	
	public void saveFreeText(String text, Gender gender) {
		
		FreeText freeText = new FreeText();
		freeText.setText(text);
		freeText.setGender(gender);
		
		ftRepo.save(freeText);
	}
	
	public List<FreeText> findAll() {
		
		return (List<FreeText>) ftRepo.findAll();
	}

	public void resetDB() {
		ftRepo.deleteAll();		
	}
}
