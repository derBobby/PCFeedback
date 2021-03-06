package eu.planlos.pcfeedback.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.planlos.pcfeedback.exceptions.UiTextException;
import eu.planlos.pcfeedback.model.UiTextKey;
import eu.planlos.pcfeedback.model.db.Project;
import eu.planlos.pcfeedback.model.db.UiText;
import eu.planlos.pcfeedback.repository.UiTextRepository;

@Service
public class UiTextService {
	
	private static final Logger LOG = LoggerFactory.getLogger(UiTextService.class);
	
	private UiTextRepository uiTextRepo;
	
	@Autowired
	public UiTextService(UiTextRepository uiTextRepo) {
		this.uiTextRepo = uiTextRepo;
	}
	
	public void updateText(Project project, UiTextKey uiTextKey, String text) {
		
		UiText uiText = uiTextRepo.findByProjectAndUiTextKey(project, uiTextKey);
		uiText.setText(text);
		LOG.debug("Updating UiText for: project={}, uiTextKey={}", project.getProjectName(), uiTextKey.name());
		uiTextRepo.save(uiText);
	}

	public UiText getUiText(Project project, UiTextKey uiTextKey) {
		LOG.debug("Searching UiText for: project={}, uiTextKey={}", project.getProjectName(), uiTextKey.name());
		return uiTextRepo.findByProjectAndUiTextKey(project, uiTextKey);
	}
	
	public void initializeUiText(Project project) {
		
		List<UiText> uiTextList = new ArrayList<>();

		List<UiTextKey> fieldList = Arrays.asList(UiTextKey.values());
		for(UiTextKey uiTextKey : fieldList) {
			uiTextList.add(new UiText(project, uiTextKey));
		}
		
		uiTextRepo.saveAll(uiTextList);
	}	

	public List<UiText> getUiTextForProject(Project project) {
		return (List<UiText>) uiTextRepo.findAllByProject(project);
	}

	/**
	 * Updates the text of given UiText, identified by idUiText.
	 * @param uiText 
	 * @throws UiTextException
	 */
	public void updateText(Long idUiText, String text) throws UiTextException {
		Optional<UiText> optionalDdbUiText = uiTextRepo.findByIdUiText(idUiText);
		if(! optionalDdbUiText.isPresent()) {
			throw new UiTextException(String.format("Konnte Text nicht speichern, weil kein UiText mit id=%s gefunden wurde", idUiText));
		}
		UiText dbUiText = optionalDdbUiText.get();
		dbUiText.setText(text);
		uiTextRepo.save(dbUiText);
	}
	
	
	/**
	 * Checks if sufficient UiTexts exist
	 * @param proactive is true if method is called proactively and ERROR output is not necessary.
	 * @throws UiTextException 
	 */
	public void checkEnoughUiTexts(Project project, boolean proactive) throws UiTextException {
		if(uiTextRepo.countByProjectAndTextIsNull(project) > 0) {
			if(!proactive) {
				LOG.error("# ~~~~~~~~ Not enough rating questions initialized! ~~~~~~~~ #");
			}
			throw new UiTextException("Es wurden nicht für jedes UiTextField ein Element initialisiert oder der zugehörige Text fehlt.");
		}
	}
	
	public void deleteByProject(Project project) {
		uiTextRepo.deleteByProject(project);
	}
}
