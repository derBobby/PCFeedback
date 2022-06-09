package eu.planlos.pcfeedback.util.csv;

import eu.planlos.pcfeedback.model.Gender;
import eu.planlos.pcfeedback.model.db.ParticipationResult;
import lombok.extern.slf4j.Slf4j;

import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FreeTextRecordCSVExporter implements ICSVExporter {
	
	private static final String[] FILE_FREETEXT_HEADER = { "M/W","Text" };
	
	@Override
	public List<Object> createRecord(Object object) throws InvalidObjectException {
		
		if(!(object instanceof ParticipationResult)) {
			throw new InvalidObjectException("Invalid object was given.");
		}
		
		ParticipationResult participationResult = (ParticipationResult) object;
		
		Long idParticipationResult = participationResult.getIdParticipationResult();
		String freeText = participationResult.getFreeText();
		Gender gender = participationResult.getParticipant().getGender();
		
		List<Object> freeTextRecord = new ArrayList<>();
		freeTextRecord.add(gender.toString());
		freeTextRecord.add(freeText);

		log.debug(
				"Created free text record: id={}", idParticipationResult);
		
		return freeTextRecord;
	}

	@Override
	public String[] getHeader() {
		return FILE_FREETEXT_HEADER;
	}

}
