package eu.planlos.pcfeedback.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UiTextKey {

	MSG_PROJECTHOME("MSG_PROJECTHOME", "Begrüßung"),
	MSG_PRICEGAME("MSG_PRICEGAME", "Gewinnspielhinweise"),
	MSG_FEEDBACK_START("MSG_FEEDBACK_START", "Teilnehmerinfo"),
	MSG_FEEDBACK_QUESTION("MSG_FEEDBACK_QUESTION", "Fragestellung"),
	MSG_FEEDBACK_FREETEXT("MSG_FEEDBACK_FREETEXT", "Freitext"),
	MSG_FEEDBACK_END("MSG_FEEDBACK_END", "Ende");
	
	private String uiTextField;
	private String description;
	
	public String toString() {
		return this.uiTextField;
	}
	
	public String getDescription() {
		return this.description;
	}
}
