package eu.planlos.pcfeedback.model;

public enum UiTextKey {

	MSG_PROJECTHOME("MSG_PROJECTHOME", "Begrüßung"),
	MSG_PRICEGAME("MSG_PRICEGAME", "Gewinnspielhinweise"),
	MSG_FEEDBACK_START("MSG_FEEDBACK_START", "Formular"),
	MSG_FEEDBACK_QUESTION("MSG_FEEDBACK_QUESTION", "Fragestellung"),
	MSG_FEEDBACK_FREETEXT("MSG_FEEDBACK_FREETEXT", "Freitext"),
	MSG_FEEDBACK_END("MSG_FEEDBACK_END", "Ende");
	
	private String uiTextField;
	private String description;
	
	private UiTextKey(String uiTextField, String description) {
		this.uiTextField = uiTextField;
		this.description = description;
	}
	
	public String toString() {
		return this.uiTextField;
	}
	
	public String getDescription() {
		return this.description;
	}
}
