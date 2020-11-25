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
import javax.validation.constraints.NotBlank;

import org.springframework.format.annotation.DateTimeFormat;

import eu.planlos.pcfeedback.util.ZonedDateTimeHelper;

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
	private boolean isPricegame;
	
	@Column(nullable = false)
	private boolean askFreetext;
	
	@Column(nullable=false)
	private boolean active;
	
	@Column(nullable=false)
	private int ratingQuestionCount;
	
	private boolean online;

	@Column
	private Instant saveInstant;

	@Column
	private Instant startInstant;

	@Column
	private Instant endInstant;
	
	@Column
	private String notificationMail;

	@Transient
	@DateTimeFormat(pattern = "dd.MM.YYYY HH:mm")
	private ZonedDateTime saveZonedDateTime;
	
	@Transient
	@DateTimeFormat(pattern = "dd.MM.YYYY HH:mm")
	private ZonedDateTime startZonedDateTime;
	
	@Transient
	@DateTimeFormat(pattern = "dd.MM.YYYY HH:mm")
	private ZonedDateTime endZonedDateTime;

	@OneToMany(fetch = FetchType.EAGER)
	private List<RatingObject> ratingObjectList;

	public Project() {
	}

	public Project(List<RatingObject> roList) {
		this.ratingObjectList = roList;
	}
	
	public Project(String projectName, List<RatingObject> ratingObjectList, boolean needMail, boolean needMobile, boolean active, ZonedDateTime startZonedDateTime, ZonedDateTime endZonedDateTime, int ratingQuestionCount) {
		this.ratingObjectList = ratingObjectList;
		
		this.projectName = projectName;
		this.needMail = needMail;
		this.needMobile = needMobile;
		this.active = active;
		setStartZonedDateTimeUpdateInstant(startZonedDateTime);
		setEndZonedDateTimeUpdateInstant(endZonedDateTime);
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

	public ZonedDateTime getStartZonedDateTime() {
		// Should always be set
		if (startInstant != null) {
			this.startZonedDateTime = ZonedDateTime.ofInstant(startInstant, ZoneId.of(ZonedDateTimeHelper.CET));
			return startZonedDateTime;
		}
		// except for new unsaved projects
		return null;
	}

	// Absolute necessary for Binding from UI to objects
	public void setStartZonedDateTime(ZonedDateTime startZonedDateTime) {
		setStartZonedDateTimeUpdateInstant(startZonedDateTime);
	}
	
	public void setStartZonedDateTimeUpdateInstant(ZonedDateTime startZonedDateTime) {
		this.startZonedDateTime = startZonedDateTime;
		this.startInstant = startZonedDateTime.toInstant();
	}
	
	public ZonedDateTime getEndZonedDateTime() {
		// Should always be set
		if (endInstant != null) {
			this.endZonedDateTime = ZonedDateTime.ofInstant(endInstant, ZoneId.of(ZonedDateTimeHelper.CET));
			return endZonedDateTime;
		}
		// except for new unsaved projects
		return null;
	}
	
	// Absolute necessary for Binding from UI to objects
	public void setEndZonedDateTime(ZonedDateTime startZonedDateTime) {
		setEndZonedDateTimeUpdateInstant(startZonedDateTime);
	}
	
	public void setEndZonedDateTimeUpdateInstant(ZonedDateTime endZonedDateTime) {
		this.endZonedDateTime = endZonedDateTime;
		this.endInstant = endZonedDateTime.toInstant();
	}
	
	public ZonedDateTime getSaveZonedDateTime() {
		// Should always be set
		if (saveInstant != null) {
			this.saveZonedDateTime = ZonedDateTime.ofInstant(saveInstant, ZoneId.of(ZonedDateTimeHelper.CET));
			return saveZonedDateTime;
		}
		// except for new unsaved projects
		return null;
	}
	
	// Absolute necessary for Binding from UI to objects
	public void setSaveZonedDateTime(ZonedDateTime saveZonedDateTime) {
		setSaveZonedDateTimeUpdateInstant(saveZonedDateTime);
	}
	
	public void setSaveZonedDateTimeUpdateInstant(ZonedDateTime saveZonedDateTime) {
		this.saveZonedDateTime = saveZonedDateTime;
		this.saveInstant = saveZonedDateTime.toInstant();
	}

	/*
	 * Functions
	 */	
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
		return isPricegame;
	}

	// Redundant for isPricegame for Thymeleaf compatibility 
	public boolean getIsPricegame() {
		return isPricegame;
	}

	public void setIsPricegame(boolean isPricegame) {
		this.isPricegame = isPricegame;
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
