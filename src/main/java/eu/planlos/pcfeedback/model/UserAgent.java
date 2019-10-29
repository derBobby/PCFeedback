package eu.planlos.pcfeedback.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

@Entity
public class UserAgent {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true, nullable=false)
	private Long idUserAgent;

	@Column(nullable=false)
	@NotBlank
	private String text;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Long getIdUserAgent() {
		return idUserAgent;
	}

	public void setIdUserAgent(Long idUserAgent) {
		this.idUserAgent = idUserAgent;
	}
}
