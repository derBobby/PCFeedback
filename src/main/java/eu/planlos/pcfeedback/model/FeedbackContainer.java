package eu.planlos.pcfeedback.model;

import java.util.List;

public class FeedbackContainer {

	private List<RatingQuestion> ratingQuestionList = null;

	public FeedbackContainer() {
	}
	
	public FeedbackContainer(List<RatingQuestion> ratingQuestionList) {
		this.ratingQuestionList = ratingQuestionList;
	}
	
	public void setRatingQuestionList(List<RatingQuestion> ratingQuestionList) {
		this.ratingQuestionList = ratingQuestionList;
	}
	
	public List<RatingQuestion> getRatingQuestionList() {
		return ratingQuestionList;
	}
	
}
