package eu.planlos.pcfeedback.model.db;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;

import org.springframework.format.annotation.DateTimeFormat;

import eu.planlos.pcfeedback.util.ZonedDateTimeUtility;

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

	@Column(nullable = false)
	private boolean needMobile;
	
	@Column(nullable = false)
	private boolean needMail;

	@Column(nullable = false)
	private boolean pricegame;
	
	@Column(nullable = false)
	private boolean askFreetext;
	
	@Column(nullable=false)
	private boolean active;
	
	@Column(nullable=false)
	@DecimalMin("1")
	private int ratingQuestionCount;
	
	private boolean online;

	@Column
	private Instant projectSaveInstant;

	@Column
	private Instant projectStartInstant;

	@Column
	private Instant projectEndInstant;
	
	@Column
	private String notificationMail;

	@Transient
	@DateTimeFormat(pattern = "dd.MM.YYYY HH:mm")
	private ZonedDateTime projectSave;
	
	@Transient
	@DateTimeFormat(pattern = "dd.MM.YYYY HH:mm")
	private ZonedDateTime projectStart;
	
	@Transient
	@DateTimeFormat(pattern = "dd.MM.YYYY HH:mm")
	private ZonedDateTime projectEnd;

	@OneToMany(fetch = FetchType.EAGER)
	private List<RatingObject> ratingObjectList;

	public Project(List<RatingObject> roList) {
		this.ratingObjectList = roList;
	}
	
	public Project(String projectName, List<RatingObject> ratingObjectList, boolean needMail, boolean needMobile, boolean active, ZonedDateTime projectStart, ZonedDateTime projectEnd, int ratingQuestionCount) {
		this.ratingObjectList = ratingObjectList;
		
		this.projectName = projectName;
		this.needMail = needMail;
		this.needMobile = needMobile;
		this.active = active;

		this.projectStart = projectStart;
		this.projectStartInstant = projectStart.toInstant();
		
		this.projectEnd = projectEnd;
		this.projectEndInstant = projectEnd.toInstant();
		
		this.ratingQuestionCount = ratingQuestionCount;
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

	public boolean getNeedMobile() {
		return this.needMobile;
	}
	
	public void setNeedMobile(boolean needMobile) {
		this.needMobile = needMobile;
	}

	public boolean getNeedMail() {
		return this.needMail;
	}
	
	public void setNeedMail(boolean needMail) {
		this.needMail = needMail;
	}
	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public ZonedDateTime getProjectStart() {
		// Should always be set
		if (projectStartInstant != null) {
			this.projectStart = ZonedDateTime.ofInstant(projectStartInstant, ZoneId.of(ZonedDateTimeUtility.CET));
			return projectStart;
		}
		// except for new unsaved projects
		return null;
	}

	// Absolute necessary for Binding from UI to objects
	public void setProjectStart(ZonedDateTime projectStart) {
		this.projectStart = projectStart;
		this.projectStartInstant = projectStart.toInstant();
	}
	
	public ZonedDateTime getProjectEnd() {
		// Should always be set
		if (projectEndInstant != null) {
			this.projectEnd = ZonedDateTime.ofInstant(projectEndInstant, ZoneId.of(ZonedDateTimeUtility.CET));
			return projectEnd;
		}
		// except for new unsaved projects
		return null;
	}
	
	// Absolute necessary for Binding from UI to objects
	public void setProjectEnd(ZonedDateTime projectEnd) {
		this.projectEnd = projectEnd;
		this.projectEndInstant = projectEnd.toInstant();
	}
	
	public ZonedDateTime getProjectSave() {
		// Should always be set
		if (projectSaveInstant != null) {
			this.projectSave = ZonedDateTime.ofInstant(projectSaveInstant, ZoneId.of(ZonedDateTimeUtility.CET));
			return projectSave;
		}
		// except for new unsaved projects
		return null;
	}
	
	// Absolute necessary for Binding from UI to objects
	public void setProjectSave(ZonedDateTime projectSave) {
		setProjectSaveUpdateInstant(projectSave);
	}
	
	public void setProjectSaveUpdateInstant(ZonedDateTime projectSave) {
		this.projectSave = projectSave;
		this.projectSaveInstant = projectSave.toInstant();
	}

	/*
	 * Functions
	 */
	@Override
	public String toString() {
		return String.format("idProject='%s', name='%s'", idProject, projectName);
	}

	public List<RatingObject> getRatingObjectList() {
		return this.ratingObjectList;
	}

	public void setRatingObjectList(List<RatingObject> ratingObjectList) {
		this.ratingObjectList = ratingObjectList;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	public boolean isPricegame() {
		return pricegame;
	}

	// Redundant for isPricegame for Thymeleaf compatibility 
	public boolean getPricegame() {
		return pricegame;
	}

	public void setPricegame(boolean pricegame) {
		this.pricegame = pricegame;
	}

	public boolean getAskFreetext() {
		return askFreetext;
	}

	public void setAskFreetext(boolean askFreetext) {
		this.askFreetext = askFreetext;
	}

	public int getRatingQuestionCount() {
		return ratingQuestionCount;
	}

	public void setRatingQuestionCount(int ratingQuestionCount) {
		this.ratingQuestionCount = ratingQuestionCount;
	}

	public String getNotificationMail() {
		return notificationMail;
	}

	public void setNotificationMail(String notificationMail) {
		this.notificationMail = notificationMail;
	}
}
