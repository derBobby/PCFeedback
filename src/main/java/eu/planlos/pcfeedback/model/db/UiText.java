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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "UiText", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"project", "uiTextKey"})
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
		this.text = "";
	}
}
