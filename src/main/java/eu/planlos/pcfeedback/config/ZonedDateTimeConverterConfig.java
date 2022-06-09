package eu.planlos.pcfeedback.config;

import eu.planlos.pcfeedback.util.ZonedDateTimeUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Configuration
public class ZonedDateTimeConverterConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters (FormatterRegistry registry) {
        registry.addConverter(new StringToZonedDateTimeConverter());
        log.debug("Configuration loaded: ZonedDateTimeConverterConfig");
    }

    public class StringToZonedDateTimeConverter implements Converter<String, ZonedDateTime> {

        @Override
        public ZonedDateTime convert(String source) {

            log.debug("Date to convert '{}'", source);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ZonedDateTimeUtility.UI_FORMAT);
            LocalDateTime localDateTime = LocalDateTime.parse(source, formatter);

            return ZonedDateTime.of(localDateTime, ZoneId.of(ZonedDateTimeUtility.CET));
        }

    }
}