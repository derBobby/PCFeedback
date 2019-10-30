package eu.planlos.pcfeedback.model;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
public class ParticipationResult {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true, nullable=false)
	private long idParticipationResult;
	
	@OneToOne(fetch=FetchType.EAGER)//, cascade=CascadeType.ALL)
	@JoinColumn(name="participant", nullable=false)
	private Participant participant;
	
	@Column
    @ElementCollection(targetClass=Integer.class)
	private Map<Long, Integer> feedbackMap;

	public ParticipationResult() {
	}
	
	public ParticipationResult(Participant participant, Map<Long, Integer> feedbackMap) {
		this.participant = participant;
		this.feedbackMap = feedbackMap;
	}
	
	public Map<Long, Integer> getFeedbackMap() {
		return feedbackMap;
	}

	public Participant getParticipant() {
		return participant;
	}
}
