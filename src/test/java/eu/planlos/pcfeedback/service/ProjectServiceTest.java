package eu.planlos.pcfeedback.service;

import eu.planlos.pcfeedback.model.db.Project;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ProjectServiceTest {

    @Test
    void projectNotActive_notOnline() {
        ProjectService service = new ProjectService(null);

        Project project = new Project();
        project.setActive(false);

        assertTrue(project.isNowOffline());
    }

    @Test
    void projectActiveButTooLate_notOnline() {
        ProjectService service = new ProjectService(null);

        ZonedDateTime pastStart = ZonedDateTime.now(ZoneId.of("CET")).minusDays(2);
        ZonedDateTime pastEnd = ZonedDateTime.now(ZoneId.of("CET")).minusDays(1);

        Project project = new Project();
        project.setActive(true);
        project.setProjectStart(pastStart);
        project.setProjectEnd(pastEnd);

        assertFalse(project.isNowOnline());
    }

    @Test
    void projectActiveButTooEarly_notOnline() {
        ProjectService service = new ProjectService(null);

        ZonedDateTime pastStart = ZonedDateTime.now(ZoneId.of("CET")).plusDays(1);
        ZonedDateTime pastEnd = ZonedDateTime.now(ZoneId.of("CET")).plusDays(2);

        Project project = new Project();
        project.setActive(true);
        project.setProjectStart(pastStart);
        project.setProjectEnd(pastEnd);

        assertFalse(project.isNowOnline());
    }

    @Test
    void projectActiveInTime_online() {
        ProjectService service = new ProjectService(null);

        ZonedDateTime pastStart = ZonedDateTime.now(ZoneId.of("CET")).minusDays(1);
        ZonedDateTime pastEnd = ZonedDateTime.now(ZoneId.of("CET")).plusDays(1);

        Project project = new Project();
        project.setActive(true);
        project.setProjectStart(pastStart);
        project.setProjectEnd(pastEnd);

        assertTrue(project.isNowOnline());
    }
}