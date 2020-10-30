package eu.planlos.pcfeedback.model.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import eu.planlos.pcfeedback.model.Gender;

@Entity
public class UserAgent {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private Long idUserAgent;

	@Column(nullable = false)
	@NotBlank
	private String text;

	@Column(nullable = false)
	@NotNull
	private Gender gender;

	@ManyToOne(fetch = FetchType.EAGER)
	@NotNull
	@JoinColumn(name = "project", nullable = false)
	private Project project;

	public UserAgent() {
	}
	
	public UserAgent(Project project, String text, Gender gender) {
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

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}
}