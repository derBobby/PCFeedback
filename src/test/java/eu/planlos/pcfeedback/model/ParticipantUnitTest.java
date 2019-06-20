package eu.planlos.pcfeedback.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.planlos.pcfeedback.constants.ApplicationConfig;

public class ParticipantUnitTest {

	private static final String PRENAME = "Peter";
	private static final String NAME = "Parker";
	private static final String EMAIL = "spiderman@marvel.com";
	private static final String MOBILE = "0123456789";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public final void femaleParticipant_returnsFemaleGender() {

		Participant participant = new Participant(PRENAME, NAME, EMAIL, MOBILE, Gender.FEMALE, false);
		assertEquals(participant.getGender(), Gender.FEMALE);
	}

	@Test
	public final void maleParticipant_returnsMaleGender() {

		Participant participant = new Participant(PRENAME, NAME, EMAIL, MOBILE, Gender.MALE, false);
		assertEquals(participant.getGender(), Gender.MALE);
	}
	
	@Test
	public final void participantToString_containsPrenameNameAndGender() {
		Participant participant = new Participant(PRENAME, NAME, EMAIL, MOBILE, Gender.MALE, false);
		assertTrue(participant.toString().contains(PRENAME));
		assertTrue(participant.toString().contains(NAME));
		assertTrue(participant.toString().contains(Gender.MALE.toString()));
	}
	
	@Test
	public final void participantCreated_participationProperlyCreated() {
		
		try {
			ZoneId timeZone = ZoneId.of(ApplicationConfig.TIME_ZONE);
			
			LocalDateTime dt1 = LocalDateTime.now(timeZone);
			Thread.sleep(1);

			Participant participant = new Participant(PRENAME, NAME, EMAIL, MOBILE, Gender.MALE, false);
			LocalDateTime participationDate = participant.getParticipationDate();
						
			Thread.sleep(1);
			LocalDateTime dt2 = LocalDateTime.now(timeZone);
			
			assertTrue(dt1.isBefore(participationDate));
			assertTrue(dt2.isAfter(participationDate));
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public final void participantCreated_formatsCorrect() {
		
		Participant participant = new Participant(PRENAME, NAME, EMAIL, MOBILE, Gender.MALE, false);
		String formattedParticipationDate = participant.getformattedParticipationDateString();

		assertTrue(formattedParticipationDate.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"));
	}

}
