package eu.planlos.pcfeedback.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class FeedbackContainer implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7206601709611371003L;
	
	private Map<Long, Integer> feedbackMap = new HashMap<>();

	public Map<Long, Integer> getFeedbackMap() {
		return feedbackMap;
	}

	public void setFeedbackMap(Map<Long, Integer> feedbackMap) {
		this.feedbackMap = feedbackMap;
	}
}
