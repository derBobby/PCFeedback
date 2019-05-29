package eu.planlos.pcfeedback.model;

public enum Gender {
	MALE(0, "männlich"), FEMALE(1, "weiblich");

	private long id;
	private String gender;

	private Gender(long id, String gender) {
		this.id = id;
		this.gender = gender;
	}
	
	//TODO necessary or is ordinal() enough?
	public long getId() {
		return this.id;
	}

	public String toString() {
		return this.gender;
	}
}
