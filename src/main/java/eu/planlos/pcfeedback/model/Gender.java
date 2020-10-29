package eu.planlos.pcfeedback.model;

public enum Gender {
	MALE("männlich"), FEMALE("weiblich");

	private String gender;

	private Gender(String gender) {
		this.gender = gender;
	}

	public String toString() {
		return this.gender;
	}
}
