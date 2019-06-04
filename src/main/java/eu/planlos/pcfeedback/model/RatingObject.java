package eu.planlos.pcfeedback.model;

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
			@UniqueConstraint(columnNames = {"name"}),
})
public class RatingObject implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * Required for JPA and reflection stuff.
	 */
	public RatingObject() {
	}

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
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof RatingObject)) {
            return false;
        }
        RatingObject ro = (RatingObject) o;
        return this.idRatingObject == ro.getIdRatingObject();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.idRatingObject);
    }
}