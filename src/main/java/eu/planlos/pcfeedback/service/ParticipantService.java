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

import eu.planlos.pcfeedback.constants.ApplicationProfileHelper;
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
	private ParticipantRepository participantRepo;

	private Environment environment;

	public void saveEdited(Participant participant) throws ParticipantNotFoundException {
		
		if(participant.getIdParticipant() == null) {
			throw new ParticipantNotFoundException("Kann Teilnehmer nicht finden, da keine ID gesetzt ist.");
		}
		participantRepo.save(participant);
	}
	
	public void delete(Participant participant) throws ParticipantNotFoundException {
		
		if(participant.getIdParticipant() == null) {
			throw new ParticipantNotFoundException("Kann Teilnehmer nicht finden, da keine ID gesetzt ist.");
		}
		participantRepo.delete(participant);
	}
	
	public void save(Participant participant) throws ParticipantAlreadyExistingException {

		// Throws exception if participant is already existing
		exists(participant);
		
		LOG.debug("Saving participant: {}", participant.toString());
		try {
			participant.setParticipationDate();
			participantRepo.save(participant);
			
		} catch(ConstraintViolationException e) {
			LOG.debug("Participant was meanwhile created, wow, what are the chances!?");
		}
	}

	public boolean exists(Participant participant) throws ParticipantAlreadyExistingException {
		
		if (participantRepo.existsByFirstnameAndName(participant.getFirstname(), participant.getName())) {
			LOG.error("Participant exists by firstname and name");
			throw new ParticipantAlreadyExistingException("Vor- / Nachname bereits vergeben!");
		}

		if (needMail && participantRepo.existsByEmail(participant.getEmail())) {
			LOG.error("Participant exists by email");
			throw new ParticipantAlreadyExistingException("E-Mail bereits vergeben!");
		}

		if (needMobile && participantRepo.existsByMobile(participant.getMobile())) {
			LOG.error("Participant exists by mobile");
			throw new ParticipantAlreadyExistingException("Handynummer bereits vergeben!");
		}
		
		return false;
	}

	public Participant findByIdParticipant(Long idParticipant) throws ParticipantNotFoundException {
		
		Optional<Participant> optParticipant = participantRepo.findById(idParticipant);
		
		if(!optParticipant.isPresent()) {
			throw new ParticipantNotFoundException("Participant konnte anhand der id nicht gefunden werden.");
		}
		
		return optParticipant.get();
	}
	
	
	public List<Participant> getAllParticipants() {
		return (List<Participant>) participantRepo.findAll();
	}
	
	/**
	 * Method creates participant dependent on the active profiles.
	 * For DEV sample data is created, for non-DEV an empty participant is created
	 * @return Participant dependent on active profile
	 */
	public Participant createParticipantForForm() {
		
		Participant participant;
		
		List<String> profiles = Arrays.asList(environment.getActiveProfiles());
		if(profiles.contains(ApplicationProfileHelper.DEV_PROFILE)) {
			
			String text = ((Long) System.currentTimeMillis()).toString();
			participant = new Participant(text, text, text +"@example.com", text, Gender.MALE, false, false);

		} else {
			participant = new Participant();
		}

		return participant;
	}
	
	/**
	 * Method creates participant dependent on the active profiles.
	 * For DEV sample data is created, for non-DEV an empty participant is created
	 * @return Participant dependent on active profile
	 */
	public Participant createParticipantForDB(Gender gender) {
		
		String text = ((Long) System.currentTimeMillis()).toString();
		return new Participant(text, text, text +"@example.com", text, gender, false, false);
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

	/**
	 * Deletes all Participant objects from DB
	 */
	public void resetDB() {
		LOG.debug("RESET: Participant");
		participantRepo.deleteAll();		
	}

	public boolean isGenderSame(Participant newParticipant) throws ParticipantNotFoundException {

		if(newParticipant.getIdParticipant() == null) {
			throw new ParticipantNotFoundException("Kann Teilnehmer nicht finden, da keine ID gesetzt ist.");
		}
		
		Participant oldParticipant = participantRepo.findById(newParticipant.getIdParticipant()).get();
		
		Gender oldGender = oldParticipant.getGender();
		Gender newGender = newParticipant.getGender();
		
		return oldGender.equals(newGender);
	}
}
