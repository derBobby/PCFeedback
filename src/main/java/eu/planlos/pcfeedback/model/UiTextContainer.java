package eu.planlos.pcfeedback.model;

public class UiTextContainer {

	private Long idUiText;
	private String text;
	private Long idProject;
	
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

	public Long getIdProject() {
		return idProject;
	}
	
	public void setIdProject(Long idProject) {
		this.idProject = idProject;
	}
}
