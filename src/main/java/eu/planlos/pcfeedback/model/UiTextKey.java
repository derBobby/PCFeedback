package eu.planlos.pcfeedback.model;

public enum UiTextKey {
	
	MSG_HOME("msgHome"),
	MSG_FEEDBACKSTART("msgFeedbackstart"),
	MSG_FEEDBACKQUESTION("msgFeedbackquestion"),
	MSG_FEEDBACKEND("msgFeedbackend");
	
	private String uiTextField;
	
	private UiTextKey(String uiTextField) {
		this.uiTextField = uiTextField;
	}
	
	public String toString() {
		return this.uiTextField;
	}
}
