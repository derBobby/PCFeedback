package eu.planlos.pcfeedback.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import eu.planlos.pcfeedback.constants.ApplicationProfileHelper;
import eu.planlos.pcfeedback.exceptions.ParticipantAlreadyExistingException;
import eu.planlos.pcfeedback.exceptions.ParticipantNotFoundException;
import eu.planlos.pcfeedback.model.Gender;
import eu.planlos.pcfeedback.model.db.Participant;
import eu.planlos.pcfeedback.model.db.Project;
import eu.planlos.pcfeedback.repository.ParticipantRepository;

@Service
public class ParticipantService implements EnvironmentAware {

	private static final Logger LOG = LoggerFactory.getLogger(ParticipantService.class);

	private static final int WINNER_COUNT = 3;
	
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
			participant.setParticipationTime();
			participantRepo.save(participant);
			
		} catch(ConstraintViolationException e) {
			LOG.debug("Participant was meanwhile created, wow, what are the chances!?");
		}
	}

	public boolean exists(Participant participant) throws ParticipantAlreadyExistingException {
		
		Project project = participant.getProject();
		
		boolean needEmail = project.getNeedMail();
		boolean needMobile = project.getNeedMobile();
		
		if (participantRepo.existsByProjectAndFirstnameAndName(project, participant.getFirstname(), participant.getName())) {
			LOG.error("Participant exists by firstname and name");
			throw new ParticipantAlreadyExistingException("Vor- / Nachname bereits vergeben!");
		}

		if (needEmail && participantRepo.existsByProjectAndEmail(project, participant.getEmail())) {
			LOG.error("Participant exists by email");
			throw new ParticipantAlreadyExistingException("E-Mail bereits vergeben!");
		}

		if (needMobile && participantRepo.existsByProjectAndMobile(project, participant.getMobile())) {
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
	
	
	public List<Participant> getAllParticipantsForProject(Project project) {
		return (List<Participant>) participantRepo.findAllByProject(project);
	}
	
	/**
	 * Method creates participant dependent on the active profiles.
	 * For DEV sample data is created, for non-DEV an empty participant is created
	 *
	 * @param project
	 * @return Participant dependent on active profile
	 */
	public Participant createParticipantForForm(Project project) {
		
		Participant participant;
		
		List<String> profiles = Arrays.asList(environment.getActiveProfiles());
		if(profiles.contains(ApplicationProfileHelper.DEV_PROFILE)) {
			
			String text = ((Long) System.currentTimeMillis()).toString();
			participant = new Participant(project, text, text, text +"@example.com", text, Gender.MALE, true, true);

		} else {
			participant = new Participant(project);
		}

		return participant;
	}
	
	/**
	 * Method creates participant dependent on the active profiles.
	 * For DEV sample data is created, for non-DEV an empty participant is created
	 * @return Participant dependent on active profile
	 */
	public Participant createParticipantForDB(Project project, Gender gender) {
		
		String text = ((Long) System.currentTimeMillis()).toString();
		return new Participant(project, text, text, text +"@example.com", text, gender, false, false);
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;		
	}

	public List<Participant> getRandomWinnerParticipantsForProject(Project project) {

		List<Participant> allParticipants = getAllParticipantsForProject(project);
		
		Collections.shuffle(allParticipants);
		
		int pCount = allParticipants.size();
		int keepCount = pCount < WINNER_COUNT ? pCount : WINNER_COUNT;
		
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

	public void resetProject(Project project) {
		participantRepo.deleteByProject(project);
		
	}
}
