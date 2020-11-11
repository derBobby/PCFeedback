package eu.planlos.pcfeedback.model.db;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import eu.planlos.pcfeedback.model.Gender;

@Entity
@Table(
		uniqueConstraints={
				@UniqueConstraint(columnNames = {"idRatingQuestion"}),
				@UniqueConstraint(columnNames = {"objectOne", "objectTwo", "gender"}),
})
public class RatingQuestion implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * Required for JPA and reflection stuff.
	 */
	public RatingQuestion() {
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="idRatingQuestion", unique=true, nullable=false)
	private long idRatingQuestion;
	
	@Column(name="gender", nullable=false)
	private Gender gender;

	@Column(name="votesOne", nullable=false)
	private int votesOne;
	
	@Column(name="votesTwo", nullable=false)
	private int votesTwo;
	
	@Column(name="countVoted", nullable=false)
	private int countVoted;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@NotNull
	@JoinColumn(name="project", nullable=false)
	private Project project;

	@Transient
	private BigDecimal ratingForObjectOne;
	
	@Transient
	private BigDecimal ratingForObjectTwo;
	
	/*
	 * Connection
	 */	
	@ManyToOne(fetch=FetchType.EAGER)//, cascade=CascadeType.ALL)
	@JoinColumn(name="objectOne", nullable=false)
	private RatingObject objectOne;
	
	@ManyToOne(fetch=FetchType.EAGER)//, cascade=CascadeType.ALL)
	@JoinColumn(name="objectTwo", nullable=false)
	private RatingObject objectTwo;

	public long getIdRatingQuestion() {
		return idRatingQuestion;
	}

	public void setIdRatingQuestion(long idRatingQuestion) {
		this.idRatingQuestion = idRatingQuestion;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public int getVotesOne() {
		return votesOne;
	}

	public void setVotesOne(int votesOne) {
		this.votesOne = votesOne;
	}
	
	public void increaseVotesOne(int additional) {
		this.votesOne+=additional;
	}

	public int getVotesTwo() {
		return votesTwo;
	}

	public void setVotesTwo(int votesTwo) {
		this.votesTwo = votesTwo;
	}
	
	public void increaseVotesTwo(int additional) {
		this.votesTwo+=additional;
	}

	public int getCountVoted() {
		return countVoted;
	}

	public void setCountVoted(int countVoted) {
		this.countVoted = countVoted;
	}
	
	public void increaseCountVoted(int countVoted) {
		this.countVoted+=countVoted;
	}

	public RatingObject getObjectOne() {
		return objectOne;
	}

	public void setObjectOne(RatingObject objectOne) {
		this.objectOne = objectOne;
	}

	public RatingObject getObjectTwo() {
		return objectTwo;
	}

	public void setObjectTwo(RatingObject objectTwo) {
		this.objectTwo = objectTwo;
	}

	public BigDecimal getRatingForObjectOne() {
		return ratingForObjectOne;
	}

	public void setRatingForObjectOne(BigDecimal ratingForObjectOne) {
		this.ratingForObjectOne = ratingForObjectOne;
	}

	public BigDecimal getRatingForObjectTwo() {
		return ratingForObjectTwo;
	}

	public void setRatingForObjectTwo(BigDecimal ratingForObjectTwo) {
		this.ratingForObjectTwo = ratingForObjectTwo;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	/*
	 * METHODS
	 */
	@Override
	public String toString() {
		return objectOne.getName() + "/" + objectTwo.getName() + "/" + gender;
	}

    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof RatingQuestion)) {
            return false;
        }
        RatingQuestion ro = (RatingQuestion) o;
        return this.idRatingQuestion == ro.getIdRatingQuestion();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.idRatingQuestion);
    }
}