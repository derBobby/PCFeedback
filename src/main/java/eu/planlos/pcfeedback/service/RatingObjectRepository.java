package eu.planlos.pcfeedback.service;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import eu.planlos.pcfeedback.model.RatingObject;

@Repository
public interface RatingObjectRepository extends CrudRepository<RatingObject, Long>{
}
