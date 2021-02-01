package eu.planlos.pcfeedback.exceptions;

@SuppressWarnings("serial")
public class ParticipantIsMissingEmailException extends Exception {
	public ParticipantIsMissingEmailException(String message) {
		super(message);
	}
}
