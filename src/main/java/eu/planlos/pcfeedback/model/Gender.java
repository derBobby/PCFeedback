package eu.planlos.pcfeedback.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public enum Gender {
	MALE("männlich"), FEMALE("weiblich");

	@Id
	@SuppressWarnings("unused")
	private long idGender;
	private String gender;

	private Gender(String gender) {
		this.idGender = ordinal();
		this.gender = gender;
	}

	public String toString() {
		return this.gender;
	}
}
