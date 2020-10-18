package eu.planlos.pcfeedback.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.planlos.pcfeedback.model.RatingObject;
import eu.planlos.pcfeedback.repository.RatingObjectRepository;

@Service
public class RatingObjectService {

	@Autowired
	private RatingObjectRepository ratingObjectRepository;

	public List<RatingObject> saveAll(List<RatingObject> ratingObjectList) {
		return (List<RatingObject>) ratingObjectRepository.saveAll(ratingObjectList);
	}
	
	public RatingObject findByIdRatingObject(Long idRatingObject) {
		return ratingObjectRepository.findById(idRatingObject).get();
	}
}
