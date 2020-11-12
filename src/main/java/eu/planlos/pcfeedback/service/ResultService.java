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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.planlos.pcfeedback.model.Gender;
import eu.planlos.pcfeedback.model.db.Project;
import eu.planlos.pcfeedback.model.db.RatingObject;
import eu.planlos.pcfeedback.model.db.RatingQuestion;

@Service
public class ResultService {

	private static final Logger LOG = LoggerFactory.getLogger(ResultService.class);

	@Autowired
	private RatingQuestionService rqs;
	
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
	public Map<RatingObject, BigDecimal> rate(Project project, Gender gender) {
		
		List<RatingQuestion> rqList = new ArrayList<>();
		Map<RatingObject, BigDecimal> targetRatingMap = new HashMap<RatingObject, BigDecimal>();
		LinkedHashMap<RatingObject, BigDecimal> sortedRatingMap = new LinkedHashMap<>();

		LOG.debug("Loading ratingQuestions to rate them, gender={}", gender);
		rqList.addAll(rqs.loadByProjectAndGender(project, gender));

		// 1)
		rate(rqList);
		
		// 2)
		fillResultMapFor(targetRatingMap, rqList);
		
		// 3)
		order(targetRatingMap, sortedRatingMap);
		//LinkedHashMap preserve the ordering of elements in which they are inserted
		
		return sortedRatingMap;
	}
	
	public Map<RatingObject, BigDecimal> combine(Map<RatingObject, BigDecimal> maleMap, Map<RatingObject, BigDecimal> femaleMap) {
		
		Map<RatingObject, BigDecimal> overallMap = new HashMap<>();
		
		for(RatingObject ro : maleMap.keySet()) {
			BigDecimal total = new BigDecimal(0);
			
			BigDecimal maleRating = maleMap.get(ro);
			BigDecimal femaleRating = femaleMap.get(ro);
			
			total = total.add(maleRating);
			total = total.add(femaleRating);
			
			overallMap.put(ro, total);			
		}
		LinkedHashMap<RatingObject, BigDecimal> sortedMap = new LinkedHashMap<>();
		order(overallMap, sortedMap);
		
		return sortedMap;
	}

	private void order(Map<RatingObject, BigDecimal> unsortedMap, LinkedHashMap<RatingObject, BigDecimal> sortedMap) {

		unsortedMap.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));
	}

	private void rate(List<RatingQuestion> rqList) {
		
		for(RatingQuestion rq : rqList) {
			
			int voteCountInt = rq.getCountVoted();
			if(voteCountInt == 0) {
				BigDecimal zeroDecimal = new BigDecimal(0);
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
		BigDecimal baseToAddTo = new BigDecimal(0);
		if (targetRatingMap.containsKey(ratingObject)) {
			// Case: The map contains the RO
			baseToAddTo = targetRatingMap.get(ratingObject);
		}

		BigDecimal addedValueOne = baseToAddTo.add(rating);
		targetRatingMap.put(ratingObject, addedValueOne);
	}
}
