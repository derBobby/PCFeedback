package eu.planlos.pcfeedback.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import eu.planlos.pcfeedback.model.FreeText;

@Repository
public interface FreeTextRepository extends CrudRepository<FreeText, Long> {

}
