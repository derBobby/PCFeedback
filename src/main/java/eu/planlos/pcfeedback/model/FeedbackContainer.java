package eu.planlos.pcfeedback.model;

import java.util.HashMap;
import java.util.Map;

public class FeedbackContainer {

	//TODO implement this instead of the session?
	private Participant participant;
	
	private Map<Long, Integer> feedbackMap = new HashMap<>();

	public Map<Long, Integer> getFeedbackMap() {
		return feedbackMap;
	}

	public void setFeedbackMap(Map<Long, Integer> feedbackMap) {
		this.feedbackMap = feedbackMap;
	}
}
