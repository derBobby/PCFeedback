package eu.planlos.pcfeedback.auth.keycloak;

import javax.annotation.PostConstruct;

import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakSecurityComponents;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

import eu.planlos.pcfeedback.constants.ApplicationPathHelper;

@Profile("KEYCLOAK")
@Configuration
@EnableWebSecurity
@ComponentScan(basePackageClasses = KeycloakSecurityComponents.class)
class SecurityConfigurationKeycloak extends KeycloakWebSecurityConfigurerAdapter {

	private static final Logger LOG = LoggerFactory.getLogger(SecurityConfigurationKeycloak.class);

	//TODO More elegant way? See RoleConfigurationKeycloak
	@Value("${eu.planlos.pcfeedback.auth.keycloak.role.admin}")
	private String ROLE_ADMIN;
	
	@PostConstruct
	private void init() {
		LOG.debug("Configuration loaded");
	}
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {

		LOG.debug("running configureGlobal()");
		KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
		keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
		auth.authenticationProvider(keycloakAuthenticationProvider);
	}

	@Bean
	public KeycloakSpringBootConfigResolver KeycloakConfigResolver() {
		LOG.debug("running KeycloakConfigResolver()");
		return new KeycloakSpringBootConfigResolver();
	}

	@Bean
	@Override
	protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
		LOG.debug("running sessionAuthenticationStrategy()");
		return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		LOG.debug("running configure()");
		
		super.configure(http);

		// H2-Console uses iFrame
		http.headers().frameOptions().disable();

		http.authorizeRequests()

				/*
				 * ADMIN
				 */
				.antMatchers(
						ApplicationPathHelper.URL_AREA_ADMIN + "**",
						ApplicationPathHelper.URL_AREA_ACTUATOR + "/**")
				.hasRole(ROLE_ADMIN)

				/*
				 * PUBLIC
				 */
				.antMatchers(
						"/webjars/**",
						"/css/**",
						"/img/**",
						"/favicon.ico",
						ApplicationPathHelper.URL_AREA_PUBLIC + "**")
				.permitAll();
	}
}