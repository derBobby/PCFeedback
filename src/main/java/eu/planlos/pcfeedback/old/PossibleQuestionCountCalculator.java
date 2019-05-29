package eu.planlos.pcfeedback.old;

public class PossibleQuestionCountCalculator {

	public Integer calculate(Integer existingRatingItems) {
		
		Integer possibleQuestionCount = 0;
		for(int i=0; i<existingRatingItems; i++) {
			possibleQuestionCount+=i;
		}
		
		return possibleQuestionCount;
	}
}
