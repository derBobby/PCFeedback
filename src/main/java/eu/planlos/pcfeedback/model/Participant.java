package eu.planlos.pcfeedback.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;

@Entity
@Table(
		uniqueConstraints={
			@UniqueConstraint(columnNames = {"idParticipant"}),
			@UniqueConstraint(columnNames = {"prename", "name"}),
			@UniqueConstraint(columnNames = {"email"}),
			@UniqueConstraint(columnNames = {"mobile"}),
})
public class Participant implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true, nullable=false)
	private long idParticipant;

	@Column(nullable=false)
	@NotBlank
	private String prename;
	
	@Column(nullable=false)
	@NotBlank
	private String name;

	@Column(nullable=false)
	@NotBlank
	private String email;
	
	@Column(nullable=false)
	@NotBlank
	private String mobile;
	
	@Column(nullable=false)
	@NotBlank
	private Gender gender;
	
	@Column(nullable=false)
	@NotBlank
	private String participationDate;

	public long getIdParticipant() {
		return idParticipant;
	}

	public void setIdParticipant(long idParticipant) {
		this.idParticipant = idParticipant;
	}
	
	public String getPrename() {
		return prename;
	}

	public void setPrename(String prename) {
		this.prename = prename;
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

	public String getParticipationDate() {
		return participationDate;
	}

	public void setParticipationDate(String participationDate) {
		this.participationDate = participationDate;
	}
	
	/*
	 * Functions
	 */	
	public String toString() {
		return prename + " " + name + "(" + gender + ")";
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