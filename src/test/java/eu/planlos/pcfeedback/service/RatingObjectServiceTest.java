package eu.planlos.pcfeedback.service;

import eu.planlos.pcfeedback.exceptions.DuplicateRatingObjectException;
import eu.planlos.pcfeedback.model.db.RatingObject;
import eu.planlos.pcfeedback.repository.RatingObjectRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RatingObjectServiceTest {

    @SneakyThrows
    @Test
    void listValid_saved() {

        List<RatingObject> roList = createList();
        RatingObjectRepository mockRepo = mock(RatingObjectRepository.class);

        RatingObjectService service = new RatingObjectService(mockRepo);

        service.validateAndSaveList(roList);

        verify(mockRepo).saveAll(any());
    }

    @Test
    void containsDuplicate_throwsException() {
        List<RatingObject> roList = createList();
        RatingObject duplicateObject = new RatingObject();
        duplicateObject.setName("RO #1");
        roList.add(duplicateObject);

        RatingObjectRepository mockRepo = mock(RatingObjectRepository.class);

        RatingObjectService service = new RatingObjectService(mockRepo);

        assertThrows(DuplicateRatingObjectException.class, () -> service.validateAndSaveList(roList));
    }


    private List<RatingObject> createList() {
        List<RatingObject> roList = new ArrayList<>();
        for (int iterator = 1; iterator<=3; iterator++) {
            RatingObject ro = new RatingObject(String.format("RO #%s", iterator));
            roList.add(ro);
        }
        return roList;
    }
}