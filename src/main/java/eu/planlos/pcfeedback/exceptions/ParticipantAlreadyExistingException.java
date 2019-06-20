package eu.planlos.pcfeedback.exceptions;

public class ParticipantAlreadyExistingException extends Exception {

	private static final long serialVersionUID = -1440919294196532059L;
	
	public ParticipantAlreadyExistingException(String message) {
		super(message);
	}
}
