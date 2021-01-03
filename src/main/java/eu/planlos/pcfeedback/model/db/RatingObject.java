package eu.planlos.pcfeedback.model.db;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(
		uniqueConstraints={
			@UniqueConstraint(columnNames = {"idRatingObject"}),
})
public class RatingObject implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="idRatingObject", unique=true, nullable=false)
	private long idRatingObject;
	
	@Column(name="name", nullable=false)
	private String name;

	/**
	 * Required for JPA and reflection stuff.
	 */
	public RatingObject() {
	}
	
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
	
	public long getIdRatingObject() {
		return idRatingObject;
	}

	public void setIdRatingObject(long idRatingObject) {
		this.idRatingObject = idRatingObject;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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