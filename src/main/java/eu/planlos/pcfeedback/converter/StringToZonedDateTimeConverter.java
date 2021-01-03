package eu.planlos.pcfeedback.converter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

import eu.planlos.pcfeedback.util.ZonedDateTimeUtility;

public class StringToZonedDateTimeConverter implements Converter<String, ZonedDateTime> {

	private static final Logger LOG = LoggerFactory.getLogger(StringToZonedDateTimeConverter.class);
	
	@Override
	public ZonedDateTime convert(String source) {

		LOG.debug("Date to convert '{}'", source);
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ZonedDateTimeUtility.UI_FORMAT);
		LocalDateTime localDateTime = LocalDateTime.parse(source, formatter);

		return ZonedDateTime.of(localDateTime, ZoneId.of(ZonedDateTimeUtility.CET));
	}

}
