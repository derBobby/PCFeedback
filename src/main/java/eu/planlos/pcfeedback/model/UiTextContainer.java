package eu.planlos.pcfeedback.model;

public class UiTextContainer {

	private Long idUiText;
	private String text;
	private String projectName;
	
	public Long getIdUiText() {
		return idUiText;
	}

	public void setIdUiText(Long idUiText) {
		this.idUiText = idUiText;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getProjectName() {
		return projectName;
	}
	
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
}
