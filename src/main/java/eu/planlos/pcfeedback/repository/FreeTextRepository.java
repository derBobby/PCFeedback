package eu.planlos.pcfeedback.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.planlos.pcfeedback.model.db.FreeText;
import eu.planlos.pcfeedback.model.db.Project;

@Repository
public interface FreeTextRepository extends CrudRepository<FreeText, Long> {
	public List<FreeText> findAllByProject(Project project);
	@Transactional
	public void deleteByProject(Project project);
}
