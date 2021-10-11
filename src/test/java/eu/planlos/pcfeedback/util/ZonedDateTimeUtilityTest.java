package eu.planlos.pcfeedback.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class ZonedDateTimeUtilityTest {

    private static final String ZERO_DATE = "01.01.1970 01:00:00";
    private static final String NON_ZERO_DATE = "01.01.1970 01:00:01";

    @Test
    void zeroEpoch_niceCET() {
        Instant instant = Instant.ofEpochMilli(0);

        String formatted = ZonedDateTimeUtility.niceCET(instant);
        printFormatted(formatted);

        assert (formatted.equals(ZERO_DATE));
    }

    @Test
    void nonzeroEpoch_niceCET() {
        Instant instant = Instant.ofEpochMilli(1000);

        String formatted = ZonedDateTimeUtility.niceCET(instant);
        printFormatted(formatted);

        assert (formatted.equals(NON_ZERO_DATE));
    }

    @Test
    void nice() {
        ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(1000), ZoneId.of("CET"));
        System.out.println(zdt.toString());

        String formatted = ZonedDateTimeUtility.nice(zdt);

        assert (formatted.equals(NON_ZERO_DATE));
    }

    private void printFormatted(String formatted) {
        log.debug("Formatted String: {}", formatted);
    }
}