package eu.planlos.pcfeedback.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import eu.planlos.pcfeedback.model.Project;
import eu.planlos.pcfeedback.model.UiText;
import eu.planlos.pcfeedback.model.UiTextKey;

@Repository
public interface UiTextRepository extends CrudRepository<UiText, UiTextKey> {

	public int countByProjectAndTextIsNull(Project project);
	public UiText findByProjectAndUiTextKey(Project project, UiTextKey uiTextKey);
}
