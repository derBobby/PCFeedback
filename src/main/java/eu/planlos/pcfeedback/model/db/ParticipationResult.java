package eu.planlos.pcfeedback.model.db;

import java.util.Map;
import java.util.TreeMap;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

@Entity
public class ParticipationResult {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true, nullable=false)
	private long idParticipationResult;
	
	@OneToOne(fetch=FetchType.EAGER)//, cascade=CascadeType.ALL)
	@JoinColumn(name="participant", nullable=false)
	private Participant participant;
	
	@Column(nullable = true)
	private String freeText;
	
	@Column
    @ElementCollection(targetClass=Integer.class)
	private Map<Long, Integer> feedbackMap;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@NotNull
	@JoinColumn(name="project", nullable=false)
	private Project project;

	public ParticipationResult() {
	}
	
	public ParticipationResult(Project project, Participant participant, Map<Long, Integer> feedbackMap, String freeText) {
		this.project = project;
		this.participant = participant;
		this.feedbackMap = feedbackMap;
		this.freeText = freeText;
	}
	
	public long getIdParticipationResult() {
		return idParticipationResult;
	}

	public void setIdParticipationResult(long idParticipationResult) {
		this.idParticipationResult = idParticipationResult;
	}

	public void setFeedbackMap(Map<Long, Integer> feedbackMap) {
		this.feedbackMap = feedbackMap;		
	}
	
	public Map<Long, Integer> getFeedbackMap() {
		return feedbackMap;
	}

	public Participant getParticipant() {
		return participant;
	}
	
	public String getFreeText() {
		return freeText;
	}

	public void setFreeText(String freeText) {
		this.freeText = freeText;
	}

	public String printKeyList() {
		
		Map<Long, Integer> sortedMap = new TreeMap<>(feedbackMap);
		
		StringBuilder keyList = new StringBuilder("");
		
		for(Long key : sortedMap.keySet()) {
			keyList.append(key);
			keyList.append(",");
		}
		//TODO test this
		return keyList.toString();
	}
}
