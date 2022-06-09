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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "RatingQuestion", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"idRatingQuestion"}),
		@UniqueConstraint(columnNames = {"objectOne", "objectTwo", "gender"})
})
public class RatingQuestion implements Serializable {
	private static final long serialVersionUID = 1L;

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

	/*
	 *
	 */
	@Override
	public String toString() {
		return objectOne.getName() + "/" + objectTwo.getName() + "/" + gender;
	}

    @Override
    public boolean equals(Object object) {

        if (object == this) {
        	return true;
        }
        
        if (!(object instanceof RatingQuestion)) {
            return false;
        }
        RatingQuestion ratingQuestion = (RatingQuestion) object;
        return this.idRatingQuestion == ratingQuestion.getIdRatingQuestion();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.idRatingQuestion);
    }
}