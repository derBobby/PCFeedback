package eu.planlos.pcfeedback.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
import eu.planlos.pcfeedback.exceptions.ParticipantNotFoundException;
import eu.planlos.pcfeedback.model.Gender;
import eu.planlos.pcfeedback.model.Participant;
import eu.planlos.pcfeedback.repository.ParticipantRepository;

@Service
public class ParticipantService implements EnvironmentAware {

	private static final Logger LOG = LoggerFactory.getLogger(ParticipantService.class);

	@Value("${eu.planlos.pcfeedback.winner-count}")
	private int winnerCount;
	
	@Value("${eu.planlos.pcfeedback.need-mail}")
	private boolean needMail;
	
	@Value("${eu.planlos.pcfeedback.need-mobile}")
	private boolean needMobile;
	
	@Autowired
	private ParticipantRepository participantRepository;

	private Environment environment;

	public void saveEdited(Participant participant) throws ParticipantNotFoundException {
		
		if(participant.getIdParticipant() == null) {
			throw new ParticipantNotFoundException("Kann Teilnehmer nicht finden, da keine ID gesetzt ist.");
		}
		participantRepository.save(participant);
	}
	
	public void delete(Participant participant) throws ParticipantNotFoundException {
		
		if(participant.getIdParticipant() == null) {
			throw new ParticipantNotFoundException("Kann Teilnehmer nicht finden, da keine ID gesetzt ist.");
		}
		participantRepository.delete(participant);
	}
	
	public void save(Participant participant) throws ParticipantAlreadyExistingException {

		// Throws exception if participant is already existing
		exists(participant);
		LOG.debug("Participant does not exist, saving: " + participant.toString());
		participant.setParticipationDate();
		
		LOG.debug("Saving participant: " + participant.toString());
		try {
			
			participantRepository.save(participant);
			
		} catch(Exception e) {
			
			if(e.getCause() instanceof ConstraintViolationException) {
				LOG.error("Participant exists meanwhile, wow, what are the chances!?");
				// Using this method because it already throws an exception depending on the problem
				exists(participant);
			}
			throw e;	
		}
	}

	public boolean exists(Participant participant) throws ParticipantAlreadyExistingException {
		
		if (participantRepository.existsByFirstnameAndName(participant.getFirstname(), participant.getName())) {
			LOG.error("Participant exists by firstname and name");
			throw new ParticipantAlreadyExistingException("Vor- / Nachname bereits vergeben!");
		}

		if (needMail && participantRepository.existsByEmail(participant.getEmail())) {
			LOG.error("Participant exists by email");
			throw new ParticipantAlreadyExistingException("E-Mail bereits vergeben!");
		}

		if (needMobile && participantRepository.existsByMobile(participant.getMobile())) {
			LOG.error("Participant exists by mobile");
			throw new ParticipantAlreadyExistingException("Handynummer bereits vergeben!");
		}
		
		return false;
	}

	public Participant findByIdParticipant(Long idParticipant) throws ParticipantNotFoundException {
		
		Optional<Participant> optParticipant = participantRepository.findById(idParticipant);
		
		if(!optParticipant.isPresent()) {
			throw new ParticipantNotFoundException("Participant konnte anhand der id nicht gefunden werden.");
		}
		
		return optParticipant.get();
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

	public boolean isGenderChanged(Participant newParticipant) throws ParticipantNotFoundException {

		if(newParticipant.getIdParticipant() == null) {
			throw new ParticipantNotFoundException("Kann Teilnehmer nicht finden, da keine ID gesetzt ist.");
		}
		
		Participant oldParticipant = participantRepository.findById(newParticipant.getIdParticipant()).get();
		
		Gender oldGender = oldParticipant.getGender();
		Gender newGender = newParticipant.getGender();
		
		if(oldGender.equals(newGender)) {
			return false;
		}
		return true;
	}
}
