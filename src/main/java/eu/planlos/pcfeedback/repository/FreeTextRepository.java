package eu.planlos.pcfeedback.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import eu.planlos.pcfeedback.model.FreeText;
import eu.planlos.pcfeedback.model.Project;

@Repository
public interface FreeTextRepository extends CrudRepository<FreeText, Long> {
	public List<FreeText> findAllByProject(Project project);
}
