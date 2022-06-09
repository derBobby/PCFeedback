package eu.planlos.pcfeedback.service;

import eu.planlos.pcfeedback.exceptions.DuplicateRatingObjectException;
import eu.planlos.pcfeedback.model.db.RatingObject;
import eu.planlos.pcfeedback.repository.RatingObjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class RatingObjectService {

	private final RatingObjectRepository ratingObjectRepository;

	public RatingObjectService(RatingObjectRepository ratingObjectRepository) {
		this.ratingObjectRepository = ratingObjectRepository;
	}
	
	public List<RatingObject> validateAndSaveList(List<RatingObject> ratingObjectList) throws DuplicateRatingObjectException {

		List<RatingObject> saveList = new ArrayList<>();
		
		for(RatingObject ro : ratingObjectList) {
			
			log.debug("Validate and save RatingObject {}", ro.getName());
			
			if(saveList.contains(ro) ) {
				throw new DuplicateRatingObjectException(String.format("Name des Bewertungsobjekt '%s' ist schon vergeben", ro.getName()));
			}
			saveList.add(ro);
		}
		
		return (List<RatingObject>) ratingObjectRepository.saveAll(saveList);
	}

	public void deleteAll(List<RatingObject> roList) {
		ratingObjectRepository.deleteAll(roList);
	}
}
