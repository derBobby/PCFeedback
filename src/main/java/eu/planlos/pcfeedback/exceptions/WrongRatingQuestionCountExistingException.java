package eu.planlos.pcfeedback.exceptions;

@SuppressWarnings("serial")
public class WrongRatingQuestionCountExistingException extends Exception {
	public WrongRatingQuestionCountExistingException(String message) {
		super(message);
	}
}
