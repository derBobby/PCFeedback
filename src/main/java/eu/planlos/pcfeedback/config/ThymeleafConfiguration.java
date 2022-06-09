package eu.planlos.pcfeedback.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;

/**
 * Used for Security Annotations in HTML like <sec: ...>
 * @author derBobby
 *
 */
@Configuration
public class ThymeleafConfiguration {

    //TODO is this still necessary?
    @Bean
    public SpringSecurityDialect springSecurityDialect() {
        return new SpringSecurityDialect();
    }
}