package eu.planlos.pcfeedback.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class UiText {

//	@Id
//	@GeneratedValue(strategy=GenerationType.IDENTITY)
//	@Column(unique=true, nullable=false)
//	private Long idUiTextMapper;

	@Id
	@Column(unique=true, nullable=false)
	private UiTextKey uiTextKey;
	
	@Column(unique = false)
	private String text;

	public UiText() {
	}
	
	public UiText(UiTextKey uiTextKey, String text) {
		this.uiTextKey = uiTextKey;
		this.text = text;
	}

	public String getText() {
		return this.text;
	}
	
	public UiTextKey getTextKey() {
		return this.uiTextKey;
	}
}
