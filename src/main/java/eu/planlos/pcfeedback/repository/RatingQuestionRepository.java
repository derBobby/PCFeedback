package eu.planlos.pcfeedback.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import eu.planlos.pcfeedback.model.RatingQuestion;

@Repository
public interface RatingQuestionRepository extends CrudRepository<RatingQuestion, Long>{

	public List<RatingQuestion> findByIdRatingQuestion(List<Integer> questionIds);
	public int findFirstCountVotedByCountVotedGreaterThanAndGenderOrderByCountVotedAsc(int chosenCount, String gender);
	public List<Integer> findByGenderAndCountVoted(String gender, int lowestCountVoted);
	
	@Modifying
	@Query("UPDATE RatingQuestion R SET "
			+ "R.votesOne = R.votesOne + ?2,"
			+ "R.votesTwo = R.votesTwo + ?3,"
			+ "R.countVoted = R.countVoted + 1"
			+ "WHERE R.idRatingQuestion = ?1"
		)
	public void addVotes(long idRatingQuestion, int votesOne, int votesTwo);
}
