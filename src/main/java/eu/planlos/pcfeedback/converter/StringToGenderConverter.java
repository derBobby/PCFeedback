package eu.planlos.pcfeedback.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

import eu.planlos.pcfeedback.model.Gender;

public class StringToGenderConverter implements Converter<String, Gender> {

	private static final Logger LOG = LoggerFactory.getLogger(StringToGenderConverter.class);
	
	@Override
	public Gender convert(String source) {

		LOG.debug("Gender to convert '{}'", source);
		if(source.equals(Gender.FEMALE.toString())) {
			return Gender.FEMALE;
		}
		
		if(source.equals(Gender.MALE.toString())) {
			return Gender.MALE;
		}
		
		return null;
	}
}
