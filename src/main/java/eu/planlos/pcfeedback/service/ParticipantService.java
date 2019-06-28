package eu.planlos.pcfeedback.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import eu.planlos.pcfeedback.constants.ApplicationProfile;
import eu.planlos.pcfeedback.exceptions.ParticipantAlreadyExistingException;
import eu.planlos.pcfeedback.model.Gender;
import eu.planlos.pcfeedback.model.Participant;
import eu.planlos.pcfeedback.repository.ParticipantRepository;

@Service
public class ParticipantService implements EnvironmentAware {

	private static final Logger logger = LoggerFactory.getLogger(ParticipantService.class);

	@Value("${eu.planlos.pcfeedback.winner-count}")
	private int winnerCount;
	
	@Value("${eu.planlos.pcfeedback.need-mail}")
	private boolean needMail;
	
	@Autowired
	private ParticipantRepository participantRepository;

	private Environment environment;
	
	public void save(Participant participant) throws ParticipantAlreadyExistingException {

		// Throws exception if participant is already existing
		exists(participant);
		logger.debug("Participant does not exist, saving: " + participant.toString());
		participant.setParticipationDate();
		
		logger.debug("Saving participant: " + participant.toString());
		try {
			
			participantRepository.save(participant);
			
		} catch(Exception e) {
			
			if(e.getCause() instanceof ConstraintViolationException) {
				logger.error("Participant exists meanwhile, wow, what are the chances!?");
				// Using this method because it already throws an exception depending on the problem
				exists(participant);
			}
			throw e;	
		}
	}

	public boolean exists(Participant participant) throws ParticipantAlreadyExistingException {
		
		if (participantRepository.existsByFirstnameAndName(participant.getFirstname(), participant.getName())) {
			logger.error("Participant exists by firstname and name");
			throw new ParticipantAlreadyExistingException("Vor- / Nachname bereits vergeben!");
		}

		if (needMail && participantRepository.existsByEmail(participant.getEmail())) {
			logger.error("Participant exists by email");
			throw new ParticipantAlreadyExistingException("E-Mail bereits vergeben!");
		}

		if (participantRepository.existsByMobile(participant.getMobile())) {
			logger.error("Participant exists by mobile");
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
			Participant participant = new Participant(text, text, text +"@example.com", text, Gender.MALE);
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
		Participant participant = new Participant(text, text, text +"@example.com", text, gender);
		return participant;
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;		
	}

	public List<Participant> getRandomWinnerParticipants() {

		List<Participant> allParticipants = getAllParticipants();
		
		Collections.shuffle(allParticipants);
		
		int pCount = allParticipants.size();
		int keepCount = pCount < winnerCount ? pCount : winnerCount;
		
		allParticipants.subList(keepCount, allParticipants.size()).clear();
		
		return allParticipants;
	}

	public void resetDB() {
		participantRepository.deleteAll();		
	}
}
