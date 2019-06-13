package eu.planlos.pcfeedback.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.planlos.pcfeedback.exceptions.ParticipantAlreadyExistsException;
import eu.planlos.pcfeedback.model.Participant;
import eu.planlos.pcfeedback.repository.ParticipantRepository;

@Service
public class ParticipantService {

	private static final Logger logger = LoggerFactory.getLogger(ParticipantService.class);

	@Autowired
	private ParticipantRepository participantRepository;

	public void save(Participant participant) throws ParticipantAlreadyExistsException {

		// Throws exception if participant is already existing
		exists(participant);
		logger.debug("Participant does not exist, saving: " + participant.toString());
		participant.setParticipationDate();
		
		logger.debug("Saving participant: " + participant.toString());
		participantRepository.save(participant);
	}

	public boolean exists(Participant participant) throws ParticipantAlreadyExistsException {
		
		if (participantRepository.existsByPrenameAndName(participant.getPrename(), participant.getName())) {
			logger.debug("Participant exists by prename and name");
			throw new ParticipantAlreadyExistsException("Vor- / Nachname bereits vergeben!");
		}

		if (participantRepository.existsByEmail(participant.getEmail())) {
			logger.debug("Participant exists by email");
			throw new ParticipantAlreadyExistsException("E-Mail bereits vergeben!");
		}

		if (participantRepository.existsByMobile(participant.getMobile())) {
			logger.debug("Participant exists by mobile");
			throw new ParticipantAlreadyExistsException("Handynummer bereits vergeben!");
		}
		
		return false;
	}

	public List<Participant> getAllParticipants() {
		return (List<Participant>) participantRepository.findAll();
	}
}
