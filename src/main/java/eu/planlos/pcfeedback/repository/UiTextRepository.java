package eu.planlos.pcfeedback.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.planlos.pcfeedback.model.UiTextKey;
import eu.planlos.pcfeedback.model.db.Project;
import eu.planlos.pcfeedback.model.db.UiText;

@Repository
public interface UiTextRepository extends CrudRepository<UiText, UiTextKey> {

	public int countByProjectAndTextIsNull(Project project);
	public UiText findByProjectAndUiTextKey(Project project, UiTextKey uiTextKey);
	public List<UiText> findAllByProject(Project project);
	public Optional<UiText> findByIdUiText(Long idUiText);
	@Transactional
	public void deleteByProject(Project project);
}
