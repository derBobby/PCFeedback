package eu.planlos.pcfeedback.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import eu.planlos.pcfeedback.converter.StringToGenderConverter;

@Configuration
public class GenderConverterConfig implements WebMvcConfigurer {

	private static final Logger LOG = LoggerFactory.getLogger(GenderConverterConfig.class);
	
    @Override
    public void addFormatters (FormatterRegistry registry) {
        registry.addConverter(new StringToGenderConverter());
        LOG.debug("Configuration loaded: GenderConverterConfig");
    }
}