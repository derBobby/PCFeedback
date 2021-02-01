package eu.planlos.pcfeedback.exceptions;

@SuppressWarnings("serial")
public class ProjectAlreadyExistingException extends Exception {
	public ProjectAlreadyExistingException(String message) {
		super(message);
	}
}
