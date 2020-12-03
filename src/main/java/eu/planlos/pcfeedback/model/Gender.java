package eu.planlos.pcfeedback.model;

public enum Gender {
	MALE("m"), FEMALE("w");

	private String gender;

	private Gender(String gender) {
		this.gender = gender;
	}

	public String toString() {
		return this.gender;
	}
}
