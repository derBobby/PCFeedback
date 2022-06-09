package eu.planlos.pcfeedback.util.csv;

import eu.planlos.pcfeedback.model.Gender;
import eu.planlos.pcfeedback.model.db.Participant;
import lombok.extern.slf4j.Slf4j;

import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ParticipantCSVExporter implements ICSVExporter {

	private static final String[] FILE_PARTICIPANT_HEADER = { "Vorname","Nachname","Geschlecht","Teilnahmezeitpunkt" };

	@Override
	public List<Object> createRecord(Object object) throws InvalidObjectException {
		
		if(!(object instanceof Participant)) {
			throw new InvalidObjectException("Invalid object was given.");
		}
		
		Participant participant = (Participant) object;
		
		long idParticipant = participant.getIdParticipant();
		String firstname = participant.getFirstname();
		String name = participant.getName();
		Gender gender = participant.getGender();
		String participationDate = participant.getformattedParticipationTimeString();

		List<Object> participantRecord = new ArrayList<>();
		participantRecord.add(firstname);
		participantRecord.add(name);
		participantRecord.add(gender.toString());
		participantRecord.add(participationDate);

		log.debug(
				"Created participant record: id={} firstname={} name={} gender={} participationDate={}",
				idParticipant, firstname, name, gender.toString(), participationDate);

		return participantRecord;
	}

	@Override
	public String[] getHeader() {
		return FILE_PARTICIPANT_HEADER;
	}

}
