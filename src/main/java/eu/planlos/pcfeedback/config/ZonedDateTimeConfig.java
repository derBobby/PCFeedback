package eu.planlos.pcfeedback.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import eu.planlos.pcfeedback.converter.StringToZonedDateTimeConverter;

@Configuration
public class ZonedDateTimeConfig implements WebMvcConfigurer {

	private static final Logger LOG = LoggerFactory.getLogger(ZonedDateTimeConfig.class);
	
    @Override
    public void addFormatters (FormatterRegistry registry) {
        registry.addConverter(new StringToZonedDateTimeConverter());
        LOG.debug("Configuration loaded: ZonedDateTimeConfig");
    }
}