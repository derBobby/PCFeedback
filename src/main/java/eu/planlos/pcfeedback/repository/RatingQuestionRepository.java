package eu.planlos.pcfeedback.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.planlos.pcfeedback.model.Gender;
import eu.planlos.pcfeedback.model.db.Project;
import eu.planlos.pcfeedback.model.db.RatingObject;
import eu.planlos.pcfeedback.model.db.RatingQuestion;

@Repository
public interface RatingQuestionRepository extends CrudRepository<RatingQuestion, Long>{

	public List<RatingQuestion> findAllByIdRatingQuestionIn(List<Integer> questionIds);
	public RatingQuestion findFirstByProjectAndCountVotedGreaterThanAndGenderOrderByCountVotedAsc(Project project, int chosenCount, Gender gender);
	
	public List<RatingQuestion> findByProjectAndGenderAndCountVoted(Project project, Gender gender, int lowestVotedCount);
	
	@Modifying
	@Query("UPDATE RatingQuestion R SET "
			+ "R.votesOne = R.votesOne + 1, "
			+ "R.countVoted = R.countVoted + 1 "
			+ "WHERE R.idRatingQuestion = ?1 "
		)
	public int addVoteForRatingObjectOne(long idRatingQuestion);
	
	@Modifying
	@Query("UPDATE RatingQuestion R SET "
			+ "R.votesTwo = R.votesTwo + 1, "
			+ "R.countVoted = R.countVoted + 1 "
			+ "WHERE R.idRatingQuestion = ?1 "
		)
	public int addVoteForRatingObjectTwo(long idRatingQuestion);
	
	public List<RatingQuestion> findByProjectAndGender(Project project, Gender gender);
	public List<RatingQuestion> findByProject(Project project);
	
	// Edit participant methods
	
	@Modifying
	@Query("UPDATE RatingQuestion R SET "
			+ "R.votesOne = R.votesOne - 1, "
			+ "R.countVoted = R.countVoted - 1 "
			+ "WHERE R.idRatingQuestion = ?1 "
		)
	public void removeVoteForRatingObjectOne(Long idRatingQuestion);
	
	@Modifying
	@Query("UPDATE RatingQuestion R SET "
			+ "R.votesTwo = R.votesTwo - 1, "
			+ "R.countVoted = R.countVoted - 1 "
			+ "WHERE R.idRatingQuestion = ?1 "
		)
	public void removeVoteForRatingObjectTwo(Long idRatingQuestion);
	
	public RatingQuestion findByGenderAndObjectOneAndObjectTwo(Gender wantedGender, RatingObject ratingObjectOne,
			RatingObject ratingObjectTwo);
	
	public int countByProjectAndGender(Project project, Gender male);
	
	public RatingQuestion findByIdRatingQuestion(long idRatingQuestion);
	
	@Transactional
	public void deleteByProject(Project project);
}
