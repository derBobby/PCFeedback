package eu.planlos.pcfeedback.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import eu.planlos.pcfeedback.model.Gender;
import eu.planlos.pcfeedback.model.RatingQuestion;

@Repository
public interface RatingQuestionRepository extends CrudRepository<RatingQuestion, Long>{

	public List<RatingQuestion> findByIdRatingQuestion(List<Integer> questionIds);
	public RatingQuestion findFirstByCountVotedGreaterThanAndGenderOrderByCountVotedAsc(int chosenCount, Gender gender);
	
//	@Query("SELECT R "
//			+ "FROM RatingQuestion R "
//			+ "WHERE R.countVoted = ?1 "
//			+ "AND R.gender = ?2 "
//		)
	public List<RatingQuestion> findByGenderAndCountVoted(Gender gender, int lowestVotedCount);
	
	@Modifying
	@Query("UPDATE RatingQuestion R SET "
			+ "R.votesOne = R.votesOne + 1, "
			+ "R.countVoted = R.countVoted + 1 "
			+ "WHERE R.idRatingQuestion = ?1 "
		)
	public int addVoteForRatingObjectTwo(long idRatingQuestion);
	
	@Modifying
	@Query("UPDATE RatingQuestion R SET "
			+ "R.votesTwo = R.votesTwo + 1, "
			+ "R.countVoted = R.countVoted + 1 "
			+ "WHERE R.idRatingQuestion = ?1 "
		)
	public int addVoteForRatingObjectOne(long idRatingQuestion);
	
	public List<RatingQuestion> findByGender(Gender gender);
}
