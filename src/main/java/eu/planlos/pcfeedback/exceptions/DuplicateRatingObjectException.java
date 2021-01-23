package eu.planlos.pcfeedback.exceptions;

@SuppressWarnings("serial")
public class DuplicateRatingObjectException extends Exception {
	public DuplicateRatingObjectException(String message) {
		super(message);
	}
}
