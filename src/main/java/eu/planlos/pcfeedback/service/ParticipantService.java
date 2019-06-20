package eu.planlos.pcfeedback.service;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import eu.planlos.pcfeedback.constants.ApplicationProfile;
import eu.planlos.pcfeedback.exceptions.ParticipantAlreadyExistingException;
import eu.planlos.pcfeedback.exceptions.ParticipantHasAlreadyCompletedFeedbackException;
import eu.planlos.pcfeedback.model.Gender;
import eu.planlos.pcfeedback.model.Participant;
import eu.planlos.pcfeedback.repository.ParticipantRepository;

@Service
public class ParticipantService implements EnvironmentAware {

	private static final Logger logger = LoggerFactory.getLogger(ParticipantService.class);

	@Autowired
	private ParticipantRepository participantRepository;

	private Environment environment;
	
	public void save(Participant participant) throws ParticipantAlreadyExistingException {

		// Throws exception if participant is already existing
		exists(participant);
		logger.debug("Participant does not exist, saving: " + participant.toString());
		participant.setParticipationDate();
		
		logger.debug("Saving participant: " + participant.toString());
		participantRepository.save(participant);
	}

	public boolean exists(Participant participant) throws ParticipantAlreadyExistingException {
		
		if (participantRepository.existsByPrenameAndName(participant.getPrename(), participant.getName())) {
			logger.debug("Participant exists by prename and name");
			throw new ParticipantAlreadyExistingException("Vor- / Nachname bereits vergeben!");
		}

		if (participantRepository.existsByEmail(participant.getEmail())) {
			logger.debug("Participant exists by email");
			throw new ParticipantAlreadyExistingException("E-Mail bereits vergeben!");
		}

		if (participantRepository.existsByMobile(participant.getMobile())) {
			logger.debug("Participant exists by mobile");
			throw new ParticipantAlreadyExistingException("Handynummer bereits vergeben!");
		}
		
		return false;
	}

	public List<Participant> getAllParticipants() {
		return (List<Participant>) participantRepository.findAll();
	}
	
	//TODO Creating sample objects in production code? Better use different Services for profiles?
	/**
	 * Method creates participant dependent on the active profiles.
	 * For DEV sample data is created, for non-DEV an empty participant is created
	 * @return Participant dependent on active profile
	 */
	public Participant createParticipantForForm() {
		
		List<String> profiles = Arrays.asList(environment.getActiveProfiles());
		if(profiles.contains(ApplicationProfile.DEV_PROFILE)) {
			
			String text = ((Long) System.currentTimeMillis()).toString();
			Participant participant = new Participant(text, text, text +"@example.com", text, Gender.MALE, false);
			return participant;
		}
		
		return new Participant();
	}
	
	/**
	 * Method creates participant dependent on the active profiles.
	 * For DEV sample data is created, for non-DEV an empty participant is created
	 * @return Participant dependent on active profile
	 */
	public Participant createParticipantForDB(Gender gender) {
		
		String text = ((Long) System.currentTimeMillis()).toString();
		Participant participant = new Participant(text, text, text +"@example.com", text, gender, false);
		return participant;
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;		
	}

	public void completeFeedback(Participant participant) throws ParticipantHasAlreadyCompletedFeedbackException {

		boolean hasFeedbackCompleter = participantRepository.existsByIdParticipantAndFeedbackCompleted(participant.getIdParticipant(), true);
		
		if(hasFeedbackCompleter) {
			throw new ParticipantHasAlreadyCompletedFeedbackException();
		}
		
		participant.setFeedbackCompleted(true);
		participantRepository.save(participant);
	}
}
