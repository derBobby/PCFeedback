package eu.planlos.pcfeedback.service;

import eu.planlos.pcfeedback.exceptions.InvalidFeedbackException;
import eu.planlos.pcfeedback.exceptions.NoFeedbackException;
import eu.planlos.pcfeedback.model.db.Project;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
class FeedbackValidationServiceTest {

    final Map<Long, Integer> emptyMap = new HashMap<>();
    final Map<Long, Integer> nullMap = null;

    @SneakyThrows
    @Test
    void mapIsNull_isInvalid() {
        FeedbackValidationService service = new FeedbackValidationService();
        Exception exception = assertThrows(NoFeedbackException.class, () -> service.isValidFeedback(null, nullMap));
    }

    @Test
    void mapIsEmpty_isInvalid() {
        FeedbackValidationService service = new FeedbackValidationService();
        Project mockProject = mock(Project.class);
        when(mockProject.getRatingQuestionCount()).thenReturn(0);
        when(mockProject.getRatingQuestionCount()).thenReturn(2);

        Exception exception = assertThrows(InvalidFeedbackException.class, () -> service.isValidFeedback(mockProject, emptyMap));
    }

}