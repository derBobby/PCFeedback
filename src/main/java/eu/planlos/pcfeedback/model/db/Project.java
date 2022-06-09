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

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;

import eu.planlos.pcfeedback.util.ZonedDateTimeUtility;

@NoArgsConstructor
@Getter
@Setter
@Slf4j
@Entity
@Table(name = "Project", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"idProject"}),
		@UniqueConstraint(columnNames = {"projectName"})
})
public class Project implements Serializable {
	private static final long serialVersionUID = -8958091265283715683L;

	@ToString.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique=true, nullable=false)
	private Long idProject;
	
	@ToString.Include
	@Column(unique=true, nullable=false)
	@NotBlank
	private String projectName;

	@ToString.Include
	@Column(nullable = false)
	private boolean needMobile;
	
	@ToString.Include
	@Column(nullable = false)
	private boolean needMail;

	@ToString.Include
	@Column(nullable = false)
	private boolean pricegame;
	
	@ToString.Include
	@Column(nullable = false)
	private boolean askFreetext;
	
	@ToString.Include
	@Column(nullable=false)
	private boolean active;
	
	@ToString.Include
	@Column(nullable=false)
	@DecimalMin("1")
	private int ratingQuestionCount;
	
	@ToString.Include
	private Instant projectSaveInstant;

	@ToString.Include
	private Instant projectStartInstant;

	@ToString.Include
	private Instant projectEndInstant;
	
	@ToString.Include
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
	private List<RatingObject> ratingObjectList = new java.util.ArrayList<>();
	
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

	public boolean isNowOffline() {
		return ! isNowOnline();
	}

	public boolean isNowOnline() {

		if (!active) {
			log.debug("Project '{}' not active", projectName);
			return false;
		}

		ZonedDateTime now = ZonedDateTimeUtility.nowCET();
		ZonedDateTime projectStart = getProjectStart();
		ZonedDateTime projectEnd = getProjectEnd();

		if (now.isBefore(projectStart)) {
			log.debug("'{}' before '{}'",
					ZonedDateTimeUtility.nice(now),
					ZonedDateTimeUtility.nice(projectStart));
			return false;
		}
		if (now.isAfter(projectEnd)) {
			log.debug("'{}' after '{}'",
					ZonedDateTimeUtility.nice(now),
					ZonedDateTimeUtility.nice(projectEnd));
			return false;
		}

		log.debug("Start='{}'   <   Now='{}'   <   End='{}'",
				projectStart.toString(),
				now.toString(),
				projectEnd.toString());

		return true;
	}
}
