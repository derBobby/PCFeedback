package eu.planlos.pcfeedback.model.db;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import eu.planlos.pcfeedback.model.Gender;
import eu.planlos.pcfeedback.util.ZonedDateTimeHelper;

@Entity
@Table(
		uniqueConstraints={
			@UniqueConstraint(columnNames = {"idParticipant"}),
			@UniqueConstraint(columnNames = {"firstname", "name", "project"}),
			@UniqueConstraint(columnNames = {"email"}),
			@UniqueConstraint(columnNames = {"mobile"}),
})
public class Participant implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true, nullable=false)
	private Long idParticipant;

	@Column(nullable=false)
	@NotBlank
	private String firstname;
	
	@Column(nullable=false)
	@NotBlank
	private String name;

	@Column(nullable=true)
	private String email;
	
	@Column(nullable=true)
	private String mobile;
	
	@Column(nullable=false)
	@NotNull
	private Gender gender;
	
	@Column(nullable=false)
	@NotNull
	private boolean priceGameStatementAccepted;
	
	@Column(nullable=false)
	@NotNull
	private boolean dataPrivacyStatementAccepted;
	
	@Column(nullable=false)
	private Instant participationTime;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@NotNull
	@JoinColumn(name="project", nullable=false)
	private Project project;

	public Participant() {
	}
	
	public Participant(Project project) {
		setParticipationTime();
	}

	public Participant(String firstname, String name, String email, String mobile, Gender gender, boolean priceGameStatementAccepted, boolean dataPrivacyStatementAccepted) {
		this.firstname = firstname;
		this.name = name;
		this.email = email;
		this.mobile = mobile;
		this.gender = gender;
		this.priceGameStatementAccepted = priceGameStatementAccepted;
		this.dataPrivacyStatementAccepted = dataPrivacyStatementAccepted;
		this.participationTime = Instant.now();
	}

	public Participant(Project project, String firstname, String name, String email, String mobile, Gender gender, boolean priceGameStatementAccepted, boolean dataPrivacyStatementAccepted) {
		this.project = project;
		this.firstname = firstname;
		this.name = name;
		this.email = email;
		this.mobile = mobile;
		this.gender = gender;
		this.priceGameStatementAccepted = priceGameStatementAccepted;
		this.dataPrivacyStatementAccepted = dataPrivacyStatementAccepted;
		this.participationTime = Instant.now();
	}
	
	public Long getIdParticipant() {
		return idParticipant;
	}

	public void setIdParticipant(Long idParticipant) {
		this.idParticipant = idParticipant;
	}
	
	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public boolean isDataPrivacyStatementAccepted() {
		return dataPrivacyStatementAccepted;
	}

	public void setDataPrivacyStatementAccepted(boolean dataPrivacyStatementAccepted) {
		this.dataPrivacyStatementAccepted = dataPrivacyStatementAccepted;
	}

	public boolean isPriceGameStatementAccepted() {
		return priceGameStatementAccepted;
	}

	public void setPriceGameStatementAccepted(boolean priceGameStatementAccepted) {
		this.priceGameStatementAccepted = priceGameStatementAccepted;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Instant getParticipationTime() {
		return participationTime;
	}

	public String getformattedParticipationTimeString() {
		return ZonedDateTimeHelper.niceCET(participationTime);
	}

	public final void setParticipationTime() {
		this.participationTime = Instant.now();
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}
	
	/*
	 * Functions
	 */	
	public String toString() {
		return String.format("idParticipant=%s, project=%s, firstname=%s, name=%s, gender=%s", idParticipant, project.getProjectName(), firstname, name, gender.toString());
	}
	
    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof Participant)) {
            return false;
        }
        Participant ro = (Participant) o;
        return this.idParticipant == ro.getIdParticipant();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.idParticipant);
    }
}