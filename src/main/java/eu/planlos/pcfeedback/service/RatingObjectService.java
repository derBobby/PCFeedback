package eu.planlos.pcfeedback.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.planlos.pcfeedback.exceptions.DuplicateRatingObjectException;
import eu.planlos.pcfeedback.model.RatingObject;
import eu.planlos.pcfeedback.repository.RatingObjectRepository;

@Service
public class RatingObjectService {

	private static final Logger LOG = LoggerFactory.getLogger(RatingObjectService.class);
	
	@Autowired
	private RatingObjectRepository ratingObjectRepository;

	public List<RatingObject> validateUniqueAndSaveList(List<RatingObject> ratingObjectList) throws DuplicateRatingObjectException {
		
		List<RatingObject> saveList = new ArrayList<>();
		
		for(RatingObject ro : ratingObjectList) {
			
			LOG.debug("Processing RatingObject {}", ro.getName());
			
			if(saveList.contains(ro) ) {
				throw new DuplicateRatingObjectException(String.format("Name des Bewertungsobjekt '%s' ist schon vergeben", ro.getName()));
			}
			saveList.add(ro);
		}
		
		return (List<RatingObject>) ratingObjectRepository.saveAll(saveList);
	}
}
