package eu.planlos.pcfeedback.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import eu.planlos.pcfeedback.model.UiText;
import eu.planlos.pcfeedback.model.UiTextKey;

@Repository
public interface UiTextRepository extends CrudRepository<UiText, UiTextKey> {

	public int countByTextIsNull();
}