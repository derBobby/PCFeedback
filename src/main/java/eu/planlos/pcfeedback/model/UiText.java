package eu.planlos.pcfeedback.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class UiText {

	@Id
	@Column(unique=true, nullable=false)
	private UiTextKey uiTextKey;
	
	@Column(unique = false)
	private String description;
	
	@Column(unique = false)
	private String text;

	public UiText() {
	}
	
	public UiText(UiTextKey uiTextKey, String description, String text) {
		this.uiTextKey = uiTextKey;
		this.description = description;
		this.text = text;
	}
	
	public UiText(UiTextKey uiTextKey) {
		this.uiTextKey = uiTextKey;
		this.description = null;
		this.text = null;
	}
	
	public UiTextKey getUiTextKey() {
		return this.uiTextKey;
	}

	public void setUiTextKey(UiTextKey uiTextKey) {
		this.uiTextKey = uiTextKey;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
