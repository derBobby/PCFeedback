package eu.planlos.pcfeedback.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class FreeText {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true, nullable=false)
	private Long idFreeText;

	@Column(nullable=false)
	@Lob
	@NotNull
	private String text;
	
	@Column(nullable=false)
	@NotNull
	private Gender gender;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@NotNull
	@JoinColumn(name="project", nullable=false)
	private Project project;

	public FreeText() {
	}

	public FreeText(Project project, String text, Gender gender) {
		this.project = project;
		this.text = text;
		this.gender = gender;
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public Long getIdFreeText() {
		return idFreeText;
	}

	public void setIdFreeText(Long idFreeText) {
		this.idFreeText = idFreeText;
	}

}
