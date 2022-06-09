package eu.planlos.pcfeedback.service;

import com.github.javafaker.Faker;
import com.github.javafaker.FunnyName;
import eu.planlos.pcfeedback.constants.ApplicationProfiles;
import eu.planlos.pcfeedback.exceptions.ParticipantAlreadyExistingException;
import eu.planlos.pcfeedback.exceptions.ParticipantNotFoundException;
import eu.planlos.pcfeedback.model.Gender;
import eu.planlos.pcfeedback.model.db.Participant;
import eu.planlos.pcfeedback.model.db.Project;
import eu.planlos.pcfeedback.repository.ParticipantRepository;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class ParticipantService implements EnvironmentAware {

	private static final int WINNER_COUNT = 3;

	private ParticipantRepository participantRepo;

	private Environment environment;

	public ParticipantService(ParticipantRepository participantRepository) {
		this.participantRepo = participantRepository;
	}

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

		try {
			log.debug("Saving participant: {}", participant);
			participant.participationTimeNow();
			participantRepo.save(participant);
			log.debug("Saved participant: {}", participant);

		} catch(ConstraintViolationException e) {
			log.debug("Participant was meanwhile created, wow, what are the chances!?");
		}
	}

	public boolean exists(Participant participant) throws ParticipantAlreadyExistingException {

		log.debug("Checking if participant exists: {}", participant);
		Project project = participant.getProject();

		if (participantRepo.existsByProjectAndFirstnameAndName(project, participant.getFirstname(), participant.getName())) {
			log.error("Participant exists by firstname and name");
			Participant dbPart = participantRepo.findAllByProjectAndFirstnameAndName(project, participant.getFirstname(), participant.getName());
			throw new ParticipantAlreadyExistingException("Vor- / Nachname bereits vergeben!");
		}

		boolean needEmail = project.isNeedMail();
		boolean needMobile = project.isNeedMobile();

		if (needEmail && participantRepo.existsByProjectAndEmail(project, participant.getEmail())) {
			log.error("Participant exists by email");
			throw new ParticipantAlreadyExistingException("E-Mail bereits vergeben!");
		}

		if (needMobile && participantRepo.existsByProjectAndMobile(project, participant.getMobile())) {
			log.error("Participant exists by mobile");
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

		Participant participant = new Participant();
		participant.setProject(project);

		List<String> profiles = Arrays.asList(environment.getActiveProfiles());
		if(profiles.contains(ApplicationProfiles.DEV_PROFILE)) {
			log.debug("Filling participant with dummy data");
			fillDummyParticipant(participant);
		} else {
			log.debug("Setting statement acceptance to false");
			participant.setDataPrivacyStatementAccepted(false);
			participant.setPriceGameStatementAccepted(false);
		}

		return participant;
	}

	/**
	 * Method creates participant dependent on the active profiles.
	 * For DEV sample data is created, for non-DEV an empty participant is created
	 * @return Participant dependent on active profile
	 */
	public Participant createParticipantForDB(Project project, Gender gender) {

		Participant participant = new Participant();
		participant.setProject(project);
		participant.setGender(gender);

		fillDummyParticipant(participant);

		return participant;
	}

	//TODO Move to Object
	private void fillDummyParticipant(Participant participant) {
		
		String text = ((Long) System.currentTimeMillis()).toString();
		participant.setFirstname(text);
		participant.setName(text);
		participant.setEmail(text);
		participant.setMobile(text);
		participant.setDataPrivacyStatementAccepted(true);
		participant.setPriceGameStatementAccepted(true);
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
		log.debug("RESET: Participant");
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
