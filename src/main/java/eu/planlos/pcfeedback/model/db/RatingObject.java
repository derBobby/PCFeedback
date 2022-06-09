package eu.planlos.pcfeedback.model.db;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "RatingObject", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"idRatingObject"})
})
public class RatingObject implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="idRatingObject", unique=true, nullable=false)
	private long idRatingObject;
	
	@Column(name="name", nullable=false)
	private String name;
	
	public RatingObject(String name) {
		this.name = name;
	}

	/*
	 * Functions
	 */
	@Override
	public String toString() {
		return name;
	}
	
    @Override
    public boolean equals(Object object) {

        if (object == this) {
        	return true;
        }
        	
        if (!(object instanceof RatingObject)) {
            return false;
        }
        RatingObject ratingObject = (RatingObject) object;
        return this.name.equals(ratingObject.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.idRatingObject);
    }
}