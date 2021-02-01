package eu.planlos.pcfeedback.exceptions;

@SuppressWarnings("serial")
public class RatingQuestionsNotExistentException extends Exception {
	public RatingQuestionsNotExistentException(String message) {
		super(message);
	}
}
