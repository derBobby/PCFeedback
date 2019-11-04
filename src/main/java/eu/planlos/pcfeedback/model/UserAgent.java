package eu.planlos.pcfeedback.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
public class UserAgent {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true, nullable=false)
	private Long idUserAgent;

	@Column(nullable=false)
	@NotBlank
	private String text;
	
	@Column(nullable=false)
	@NotNull
	private Gender gender;

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

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}
}
