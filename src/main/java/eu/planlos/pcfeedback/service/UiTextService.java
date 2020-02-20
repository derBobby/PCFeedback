package eu.planlos.pcfeedback.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.planlos.pcfeedback.exceptions.UiTextException;
import eu.planlos.pcfeedback.model.UiText;
import eu.planlos.pcfeedback.model.UiTextKey;
import eu.planlos.pcfeedback.repository.UiTextRepository;

@Service
public class UiTextService {
		
	@Autowired
	private UiTextRepository uiTextRepo;
	
	public void createText(UiTextKey uiTextKey, String description, String text) {
		UiText uiText = new UiText(uiTextKey, description, text);
		uiTextRepo.save(uiText);
	}

	public String getText(UiTextKey uiTextKey) {
		UiText uit = uiTextRepo.findById(uiTextKey).get();
		return uit.getText();
	}
	
	public void initializeUiText() {
		
		List<UiText> uiTextList = new ArrayList<>();

		List<UiTextKey> fieldList = Arrays.asList(UiTextKey.values());
		for(UiTextKey uiTextKey : fieldList) {
			uiTextList.add(new UiText(uiTextKey));
		}
		
		uiTextRepo.saveAll(uiTextList);
	}	
	
	public boolean isFullyInitialized() {
		
		boolean result = true;
		
		if(uiTextRepo.countByTextIsNull() > 0) {
			result = false;
		}
	
		return result;
	}

	public List<UiText> getAllUiText() {
		return (List<UiText>) uiTextRepo.findAll();
	}

	public void updateText(UiText uiText) throws UiTextException {
		Optional<UiText> optionalDdbUiText = uiTextRepo.findById(uiText.getUiTextKey());
		if(! optionalDdbUiText.isPresent()) {
			throw new UiTextException("Konnte Text nicht speichern, weil das Element " + uiText.getUiTextKey().toString() + " nicht existiert");
		}
		UiText dbUiText = optionalDdbUiText.get();
		dbUiText.setText(uiText.getText());
		uiTextRepo.save(dbUiText);
	}
}
