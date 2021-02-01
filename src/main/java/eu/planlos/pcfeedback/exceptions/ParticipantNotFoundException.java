package eu.planlos.pcfeedback.exceptions;

@SuppressWarnings("serial")
public class ParticipantNotFoundException extends Exception {
	public ParticipantNotFoundException(String message) {
		super(message);
	}
}
