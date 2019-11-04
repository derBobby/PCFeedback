package eu.planlos.pcfeedback.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import eu.planlos.pcfeedback.model.UserAgent;

@Repository
public interface UserAgentRepository extends CrudRepository<UserAgent, Long> {

}
