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
	private UiTextRepository utr;
	
	//TODO can this be broken?
	public void createText(UiTextKey uiTextKey, String description, String text) {
		UiText uiText = new UiText(uiTextKey, description, text);
		utr.save(uiText);
	}

	public String getText(UiTextKey uiTextKey) {
		UiText uit = utr.findById(uiTextKey).get();
		return uit.getText();
	}
	
	public void initializeUiText() {
		
		List<UiText> uiTextList = new ArrayList<>();

		List<UiTextKey> fieldList = Arrays.asList(UiTextKey.values());
		for(UiTextKey uiTextKey : fieldList) {
			uiTextList.add(new UiText(uiTextKey));
		}
		
		utr.saveAll(uiTextList);
	}	
	
	//TODO where to use?
	public boolean isFullyInitialized() {
		
		int neededSize = UiTextKey.values().length;
		int existingSize = ((List<UiText>) utr.findAll()).size();
		int notNullSize = utr.countByTextIsNull();
		
		if(notNullSize > 0) {
			return false;
		}
		if(neededSize > existingSize) {
			return false;
		}
		
		return true;
	}

	public List<UiText> getAllUiText() {
		return (List<UiText>) utr.findAll();
	}

	public void updateText(UiText uiText) throws UiTextException {
		Optional<UiText> optionalDdbUiText = utr.findById(uiText.getUiTextKey());
		if(! optionalDdbUiText.isPresent()) {
			throw new UiTextException("Konnte Text nicht speichern, weil das Element " + uiText.getUiTextKey().toString() + " nicht existiert");
		}
		UiText dbUiText = optionalDdbUiText.get();
		dbUiText.setText(uiText.getText());
		utr.save(dbUiText);
	}
}
