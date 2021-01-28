package eu.planlos.pcfeedback.auth.keycloak.config;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakSecurityComponents;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.keycloak.adapters.springsecurity.filter.KeycloakSecurityContextRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;

import eu.planlos.pcfeedback.config.AuthConfiguration;
import eu.planlos.pcfeedback.constants.ApplicationPathHelper;
import eu.planlos.pcfeedback.constants.ApplicationProfileHelper;

@Profile("KC")
@Configuration
@EnableWebSecurity
@ComponentScan(basePackageClasses = KeycloakSecurityComponents.class)
class SecurityConfigurationKeycloak extends KeycloakWebSecurityConfigurerAdapter implements EnvironmentAware {

	private static final Logger LOG = LoggerFactory.getLogger(SecurityConfigurationKeycloak.class);

	@Autowired
	private AuthConfiguration authConfig;

	private Environment environment;
	
	@PostConstruct
	private void init() {
		LOG.debug("Configuration loaded");
	}
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {

		LOG.debug("running configureGlobal()");
		KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
		// SimpleAuthorityMapper is used to remove the ROLE_* conventions defined by
		// Java so we can use only admin or user instead of ROLE_ADMIN and ROLE_USER
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

        /*
         * Enable iFrame for H2-console
         */
        List<String> profiles = Arrays.asList(environment.getActiveProfiles());
        if(profiles.contains(ApplicationProfileHelper.DEV_PROFILE)) {
        	// H2-Console uses iFrame
        	http.headers().frameOptions().disable();
        }

//		super.configure(http);

        http
        	.csrf()
        		.requireCsrfProtectionMatcher(keycloakCsrfRequestMatcher())
        	.and()
        		.sessionManagement()
				.sessionAuthenticationStrategy(sessionAuthenticationStrategy())
			.and()
				.addFilterBefore(keycloakPreAuthActionsFilter(), LogoutFilter.class)
				.addFilterBefore(keycloakAuthenticationProcessingFilter(), LogoutFilter.class)
				.addFilterAfter(keycloakSecurityContextRequestFilter(), SecurityContextHolderAwareRequestFilter.class)
				.addFilterAfter(keycloakAuthenticatedActionsRequestFilter(), KeycloakSecurityContextRequestFilter.class)
				.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint())
			/*
			 * LOGOUT
			 */
			.and()
				.logout()
					.addLogoutHandler(keycloakLogoutHandler())
					.logoutUrl(authConfig.getLogoutUrl()).permitAll()
					.logoutSuccessUrl("/")

			.and()
				.authorizeRequests()

				/*
				 * ADMIN
				 */
				.antMatchers(
						ApplicationPathHelper.URL_AREA_ADMIN + "**",
						ApplicationPathHelper.URL_AREA_ACTUATOR + "/**"
					)
				.hasRole(
						authConfig.getAdminRole()
					)

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

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}
}