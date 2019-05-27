package eu.planlos.pcfeedback.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import eu.planlos.pcfeedback.model.RatingQuestion;

@Repository
public interface RatingQuestionRepository extends CrudRepository<RatingQuestion, Long>{

	public List<RatingQuestion> findAllByIdRatingQuestion(List<Integer> questionIds);
	public int findMinCountVotedByCountVotedGreaterThanAndGender(int chosenCount, String gender);
	public List<Integer> loadByGenderAndCountVoted(String gender, int lowestCountVoted);

}
