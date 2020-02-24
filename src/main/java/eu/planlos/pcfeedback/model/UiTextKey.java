package eu.planlos.pcfeedback.model;

public enum UiTextKey {

	MSG_HOME("MSG_HOME"),
	MSG_FEEDBACK_START("MSG_FEEDBACK_START"),
	MSG_FEEDBACK_QUESTION("MSG_FEEDBACK_QUESTION"),
	MSG_FEEDBACK_FREETEXT("MSG_FEEDBACK_FREETEXT"),
	MSG_FEEDBACK_END("MSG_FEEDBACK_END");
	
	private String uiTextField;
	
	private UiTextKey(String uiTextField) {
		this.uiTextField = uiTextField;
	}
	
	public String toString() {
		return this.uiTextField;
	}
}
