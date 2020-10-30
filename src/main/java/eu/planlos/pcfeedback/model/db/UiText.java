package eu.planlos.pcfeedback.model.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import eu.planlos.pcfeedback.model.UiTextKey;

@Entity
@Table(
		uniqueConstraints={
				@UniqueConstraint(columnNames = {"project", "uiTextKey"}),
	})
public class UiText {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique=true, nullable=false)
	private Long idUiText; 
	
	@Column(unique=false, nullable=false)
	private UiTextKey uiTextKey;

	private String description;
		
	@Column(unique = false, columnDefinition="LONGTEXT")
	private String text;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@NotNull
	@JoinColumn(name="project", nullable=false)
	private Project project;


	public UiText() {
	}
	
	public UiText(Project project, UiTextKey uiTextKey, String text) {
		this.project = project;
		this.uiTextKey = uiTextKey;
		this.description = uiTextKey.getDescription();
		this.text = text;
	}
	
	public UiText(Project project, UiTextKey uiTextKey) {
		this.project = project;
		this.uiTextKey = uiTextKey;
		this.description = uiTextKey.getDescription();
		this.text = null;
	}
	
	public UiTextKey getUiTextKey() {
		return this.uiTextKey;
	}

	public void setUiTextKey(UiTextKey uiTextKey) {
		this.uiTextKey = uiTextKey;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Long getIdUiText() {
		return idUiText;
	}

	public void setIdUiText(Long idUiText) {
		this.idUiText = idUiText;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
