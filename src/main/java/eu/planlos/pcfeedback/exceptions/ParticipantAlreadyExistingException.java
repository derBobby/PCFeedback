package eu.planlos.pcfeedback.exceptions;

@SuppressWarnings("serial")
public class ParticipantAlreadyExistingException extends Exception {
	public ParticipantAlreadyExistingException(String message) {
		super(message);
	}
}
