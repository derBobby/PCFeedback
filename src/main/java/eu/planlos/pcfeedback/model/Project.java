package eu.planlos.pcfeedback.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(
		uniqueConstraints={
			@UniqueConstraint(columnNames = {"idProject"}),
			@UniqueConstraint(columnNames = {"projectName"}),
})
public class Project implements Serializable {
	private static final long serialVersionUID = -8958091265283715683L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true, nullable=false)
	private Long idProject;
	
	@Column(unique=true, nullable=false)
	@NotBlank
	private String projectName;
	
	@Column(nullable=false)
	private boolean running;

	private Date saveDate;

	@DateTimeFormat(pattern = "dd.MM.yyyy HH:mm")
	private Date startDate;

	@DateTimeFormat(pattern = "dd.MM.yyyy HH:mm")
	private Date endDate;

	@OneToMany
	private List<RatingObject> ratingObjectList;

	public Project() {
	}

	public Project(String projectName) {
		this.projectName = projectName;
	}

	public Project(List<RatingObject> roList) {
		this.ratingObjectList = roList;
	}
	
	public Project(String projectName, List<RatingObject> ratingObjectList, boolean running, Date startDate, Date endDate) {
		this.ratingObjectList = ratingObjectList;
		this.projectName = projectName;
		this.running = running;
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	public Long getIdProject() {
		return idProject;
	}

	public void setIdProject(Long idProject) {
		this.idProject = idProject;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public Date getSaveDate() {
		return saveDate;
	}

	public void setSaveDate(Date saveDate) {
		this.saveDate = saveDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	/*
	 * Functions
	 */	
	public String toString() {
		return String.format("idProject=%s, name=%s", idProject, projectName);
	}

	public List<RatingObject> getRatingObjectList() {
		return this.ratingObjectList;
	}

	public void setRatingObjectList(List<RatingObject> ratingObjectList) {
		this.ratingObjectList = ratingObjectList;
	}

}
