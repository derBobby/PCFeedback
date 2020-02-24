package eu.planlos.pcfeedback.model;

public enum UiTextKey {

	MSG_HOME("MSG_HOME"),
	MSG_FEEDBACKSTART("MSG_FEEDBACKSTART"),
	MSG_FEEDBACKQUESTION("MSG_FEEDBACKQUESTION"),
	MSG_FEEDBACKFREETEXT("MSG_FEEDBACKFREETEXT"),
	MSG_FEEDBACKEND("MSG_FEEDBACKEND");
	
	private String uiTextField;
	
	private UiTextKey(String uiTextField) {
		this.uiTextField = uiTextField;
	}
	
	public String toString() {
		return this.uiTextField;
	}
}
