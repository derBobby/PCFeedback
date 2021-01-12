package eu.planlos.pcfeedback.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import eu.planlos.pcfeedback.converter.StringToZonedDateTimeConverter;

@Configuration
public class ZonedDateTimeConverterConfig implements WebMvcConfigurer {

	private static final Logger LOG = LoggerFactory.getLogger(ZonedDateTimeConverterConfig.class);

	// finde ich gut, habe ich für ObjectId auch im EM verwendet. Müsste man für die Ganzen startAsString und endAsString - Blödsinn maö nachziehen
    // den Klasssennamen finde ich aber unglücklich, da du hier eigentlich zentral alle Einstellungen für das WebMVC hinterlegen solltest.
    @Override
    public void addFormatters (FormatterRegistry registry) {
        registry.addConverter(new StringToZonedDateTimeConverter());
        LOG.debug("Configuration loaded: ZonedDateTimeConverterConfig");
    }
}