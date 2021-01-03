package eu.planlos.pcfeedback.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.planlos.pcfeedback.model.db.RatingObject;
import eu.planlos.pcfeedback.model.db.RatingQuestion;

public class RatingQuestionEvaluator {

	private static final Logger LOG = LoggerFactory.getLogger(RatingQuestionEvaluator.class);
	
	/*
	 * Rating happens in the following steps:
	 * 
	 * 1)	Take every RQ and rate it within. Rating for each object in the question is
	 * 			2 * "given votes" / "vote count of question"
	 * 
	 * 2)	For each RO sum this ratings
	 * 
	 * 3)	Order the sums for each RO descending
	 * 
	 */
	public Map<RatingObject, BigDecimal> rateWithGender(List<RatingQuestion> rqList) {
		
		Map<RatingObject, BigDecimal> targetRatingMap = new HashMap<RatingObject, BigDecimal>();
		LinkedHashMap<RatingObject, BigDecimal> sortedRatingMap = new LinkedHashMap<>();
		
		// 1)
		rate(rqList);
		
		// 2)
		fillResultMapFor(targetRatingMap, rqList);
		
		// 3)
		order(targetRatingMap, sortedRatingMap);
		//LinkedHashMap preserve the ordering of elements in which they are inserted
		
		return sortedRatingMap;
	}
	
	/*
	 * Rating happens in the following steps:
	 * 
	 * 0)	Aggregate male and female rating questions
	 * 1-3)	identical to rateWithoutGender
	 * 
	 */
	public Map<RatingObject, BigDecimal> rateWithoutGender(List<RatingQuestion> rqListMale, List<RatingQuestion> rqListFemale) {
		
		List<RatingQuestion> rqListAll = new ArrayList<>();

		Map<RatingObject, BigDecimal> targetRatingMap = new HashMap<RatingObject, BigDecimal>();
		LinkedHashMap<RatingObject, BigDecimal> sortedRatingMap = new LinkedHashMap<>();

		// 0)
		aggregateGenders(rqListAll, rqListMale, rqListFemale);

		// 1)
		rate(rqListAll);
		
		// 2)
		fillResultMapFor(targetRatingMap, rqListAll);
		
		// 3)
		order(targetRatingMap, sortedRatingMap);
		//LinkedHashMap preserve the ordering of elements in which they are inserted
		
		return sortedRatingMap;
	}
		
	public void aggregateGenders(List<RatingQuestion> rqListAll, List<RatingQuestion> rqListMale, List<RatingQuestion> rqListFemale) {
	
		for(RatingQuestion rqMale : rqListMale) {
			aggregateGendersInnerLoop(rqListAll, rqListFemale, rqMale);
		}
		
		LOG.debug("Questions female-male have been aggregated for genders");
	}

	private void aggregateGendersInnerLoop(List<RatingQuestion> rqList, List<RatingQuestion> rqListFemale, RatingQuestion rqMale) {
		
		RatingObject roOneMale = rqMale.getObjectOne();
		RatingObject roTwoMale = rqMale.getObjectTwo(); 
		
		for(RatingQuestion rqFemale : rqListFemale) {

			RatingObject roOneFemale = rqFemale.getObjectOne();
			RatingObject roTwoFemale = rqFemale.getObjectTwo();
			
			if(roOneMale.equals(roOneFemale) && roTwoMale.equals(roTwoFemale)) {
			
				int votesOne = rqMale.getVotesOne() + rqFemale.getVotesOne();
				int votesTwo = rqMale.getVotesTwo() + rqFemale.getVotesTwo();
				int countVoted = rqMale.getCountVoted() + rqFemale.getCountVoted();
				
				RatingQuestion newRQ = new RatingQuestion();
				newRQ.setObjectOne(roOneMale);
				newRQ.setObjectTwo(roTwoMale);
				newRQ.setVotesOne(votesOne);
				newRQ.setVotesTwo(votesTwo);
				newRQ.setCountVoted(countVoted);
				
				rqList.add(newRQ);
				
				return;
			}
		}
		
		LOG.error("No matching female-male question pair found. This should never happen");
	}

	private void rate(List<RatingQuestion> rqList) {
		
		for(RatingQuestion rq : rqList) {
			
			int voteCountInt = rq.getCountVoted();
			if(voteCountInt == 0) {
				BigDecimal zeroDecimal = BigDecimal.ZERO;
				rq.setRatingForObjectOne(zeroDecimal);
				rq.setRatingForObjectTwo(zeroDecimal);
				continue;
			}
			
			// multiplied with rating of object
			BigDecimal ratingOne = new BigDecimal(rq.getVotesOne());
			BigDecimal ratingTwo = new BigDecimal(rq.getVotesTwo());
			// divided by voteCount
			BigDecimal voteCount = new BigDecimal(voteCountInt);

			ratingOne = ratingOne.divide(voteCount, 2, RoundingMode.HALF_EVEN);
			rq.setRatingForObjectOne(ratingOne);
			
			ratingTwo = ratingTwo.divide(voteCount, 2, RoundingMode.HALF_EVEN);
			rq.setRatingForObjectTwo(ratingTwo);
			
			LOG.debug("Result is '{}'={} '{}'={}", rq.getObjectOne(), ratingOne, rq.getObjectTwo(), ratingTwo);
		}
	}

	private void fillResultMapFor(Map<RatingObject, BigDecimal> targetRatingMap, List<RatingQuestion> rqList) {
				
		for(RatingQuestion rq : rqList) {
			
			// Object One
			RatingObject roOne = rq.getObjectOne();
			BigDecimal ratingOne = rq.getRatingForObjectOne();
			
			addRating(targetRatingMap, roOne, ratingOne);

			// Object Two
			RatingObject roTwo = rq.getObjectTwo();
			BigDecimal ratingTwo = rq.getRatingForObjectTwo();
			
			addRating(targetRatingMap, roTwo, ratingTwo);
		}
	}

	private void addRating(Map<RatingObject, BigDecimal> targetRatingMap, RatingObject ratingObject,
			BigDecimal rating) {

		// Case: The map doesn't contain the RO
		BigDecimal baseToAddTo = BigDecimal.ZERO;
		if (targetRatingMap.containsKey(ratingObject)) {
			// Case: The map contains the RO
			baseToAddTo = targetRatingMap.get(ratingObject);
		}

		BigDecimal addedValueOne = baseToAddTo.add(rating);
		targetRatingMap.put(ratingObject, addedValueOne);
	}

	private void order(Map<RatingObject, BigDecimal> unsortedMap, Map<RatingObject, BigDecimal> sortedMap) {

		unsortedMap.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));
	}
}
