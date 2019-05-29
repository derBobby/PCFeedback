package eu.planlos.pcfeedback.exceptions;

public class ParticipantAlreadyExistsException extends Exception {

	private static final long serialVersionUID = -1440919294196532059L;
	
	public ParticipantAlreadyExistsException(String message) {
		super(message);
	}
}
