package eu.planlos.pcfeedback.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(
		uniqueConstraints={
			@UniqueConstraint(columnNames = {"idParticipant"}),
			@UniqueConstraint(columnNames = {"firstname", "name"}),
			@UniqueConstraint(columnNames = {"email"}),
			@UniqueConstraint(columnNames = {"mobile"}),
})
public class Participant implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true, nullable=false)
	private long idParticipant;

	@Column(nullable=false)
	@NotBlank
	private String firstname;
	
	@Column(nullable=false)
	@NotBlank
	private String name;

	@Column(nullable=true)
	private String email;
	
	@Column(nullable=false)
	@NotBlank
	private String mobile;
	
	@Column(nullable=false)
	@NotNull
	private Gender gender;
	
	@Column(nullable=false)
	private LocalDateTime participationDate;
	
	private static final String TIME_ZONE = "Europe/Berlin";
	
	public Participant() {
		setParticipationDate();
	}

	public Participant(String firstname, String name, String email, String mobile, Gender gender) {
		setFirstname(firstname);
		setName(name);
		setEmail(email);
		setMobile(mobile);
		setGender(gender);
		setParticipationDate();
	}
	
	public long getIdParticipant() {
		return idParticipant;
	}

	public void setIdParticipant(long idParticipant) {
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

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getMobile() {
		return mobile;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public LocalDateTime getParticipationDate() {
		return participationDate;
	}

	public String getformattedParticipationDateString() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		return participationDate.format(formatter);
	}

	public void setParticipationDate() {
		ZoneId timeZone = ZoneId.of(TIME_ZONE);
		this.participationDate = LocalDateTime.now(timeZone);
	}
	
	/*
	 * Functions
	 */	
	public String toString() {
		return firstname + " " + name + " (" + gender + ")";
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