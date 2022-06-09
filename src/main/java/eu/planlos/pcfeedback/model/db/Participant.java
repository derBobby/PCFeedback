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
import eu.planlos.pcfeedback.util.ZonedDateTimeUtility;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Participant", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"idParticipant"}),
		@UniqueConstraint(columnNames = {"firstname", "name", "project"}),
		@UniqueConstraint(columnNames = {"email"}),
		@UniqueConstraint(columnNames = {"mobile"})
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
	
	@Column(nullable = true)
	private String userAgent;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@NotNull
	@JoinColumn(name="project", nullable=false)
	private Project project;

	public Participant(Project project) {
		this.project = project;
	}
	
	public String getformattedParticipationTimeString() {
		return ZonedDateTimeUtility.niceCET(participationTime);
	}

	public final void participationTimeNow() {
		this.participationTime = Instant.now();
	}

	/*
	 * Functions
	 */
	@Override
	public String toString() {
		String projectName = null;
		if(project != null) {
			projectName = project.getProjectName();
		}
		String genderName = null;
		if(gender != null) {
			genderName = gender.toString();
		}
		
		return String.format("idParticipant=%s, project=%s, firstname=%s, name=%s, gender=%s", idParticipant, projectName, firstname, name, genderName);
	}
	
    @Override
    public boolean equals(Object object) {

        if (object == this) {
        	return true;
        }
        
        if (!(object instanceof Participant)) {
            return false;
        }
        
        Participant participant = (Participant) object;
        return this.idParticipant == participant.getIdParticipant();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.idParticipant);
    }
}