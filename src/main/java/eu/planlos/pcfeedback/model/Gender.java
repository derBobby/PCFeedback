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
	
	public Gender getOppositeGender() {
		
		if(this.gender.equals(Gender.MALE)) {
			return Gender.FEMALE;
		}		
		return Gender.MALE;
	}
}
