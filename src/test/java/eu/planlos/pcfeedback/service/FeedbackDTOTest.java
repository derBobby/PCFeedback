package eu.planlos.pcfeedback.service;

import eu.planlos.pcfeedback.exceptions.InvalidFeedbackException;
import eu.planlos.pcfeedback.exceptions.NoFeedbackException;
import eu.planlos.pcfeedback.model.FeedbackDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
class FeedbackDTOTest {

    private static final Map<Long, Integer> emptyMap = new HashMap<>();
    private static final Map<Long, Integer> nullMap = null;
    private static final Map<Long, Integer> twoEntryMap = new HashMap<>();

    @BeforeAll
    static void setup() {
        twoEntryMap.put(1L, 1);
        twoEntryMap.put(2L, 1);
    }

    @Test
    void mapIsNull_isInvalid() {
        FeedbackDTO fc = new FeedbackDTO(nullMap);
        assertThrows(NoFeedbackException.class, () -> fc.validate(1));
    }

    @Test
    void mapIsEmptyIncomplete_isInvalid() {
        FeedbackDTO fc = new FeedbackDTO(emptyMap);
        assertThrows(NoFeedbackException.class, () -> fc.validate(1));
    }

    @Test
    void mapIs2of3Incomplete_isInvalid() {
        FeedbackDTO fc = new FeedbackDTO(twoEntryMap);
        assertThrows(InvalidFeedbackException.class, () -> fc.validate(3));
    }
}