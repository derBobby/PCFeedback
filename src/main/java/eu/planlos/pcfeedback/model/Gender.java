package eu.planlos.pcfeedback.model;

public enum Gender {
	MALE("m"), FEMALE("w");

	private String gender;

	Gender(String gender) {
		this.gender = gender;
	}

	@Override
	public String toString() {
		return this.gender;
	}
}
