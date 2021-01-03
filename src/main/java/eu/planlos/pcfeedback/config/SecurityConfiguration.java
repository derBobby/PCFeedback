package eu.planlos.pcfeedback.config;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import eu.planlos.pcfeedback.constants.ApplicationPathHelper;
import eu.planlos.pcfeedback.web.security.LoginAccessDeniedHandler;
import eu.planlos.pcfeedback.web.security.UserDetailsServiceImpl;

@Profile("!KEYCLOAK")
//Schalter für SimpleAuthentication
//@EnableGlobalMethodSecurity(securedEnabled = true)
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	private static final Logger LOG = LoggerFactory.getLogger(SecurityConfiguration.class);
	
	@Autowired
	private UserDetailsServiceImpl userDetailService;

//	@Autowired
//	private LoginAuthenticationSuccessHandler successHandler;
	
	@Autowired
	private LoginAccessDeniedHandler deniedHandler;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		// H2-Console uses iFrame
		http.headers().frameOptions().disable();

		//
		http.csrf().disable()
			
			/*
			 * Session config
			 */
			.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
				
			.and().authorizeRequests()
				
				/*
				 * ADMIN
				 */
				.antMatchers(
						ApplicationPathHelper.URL_AREA_ADMIN + "**",
						ApplicationPathHelper.URL_AREA_ACTUATOR + "/**"
						
					).hasAnyAuthority(RoleConfiguration.ROLE_ADMIN)
				
				/*
				 * PUBLIC
				 */
				.antMatchers(
						"/webjars/**",
						"/css/**",
						"/img/**",
						"/favicon.ico",
						ApplicationPathHelper.URL_AREA_PUBLIC + "**"
						
					).permitAll()
					
			
			/*
			 * Login procedure
			 */
			.and().formLogin()
				
				// Overrides the default created login form site
				.loginPage(ApplicationPathHelper.URL_LOGIN_FORM)
				
				// Names URL on which Spring should listen itself
				.loginProcessingUrl(ApplicationPathHelper.URL_LOGIN)
				
				// NOT USED - Controller for successfull login
				//.successHandler(successHandler)
				
				// Redirects to given page 
				.defaultSuccessUrl(ApplicationPathHelper.URL_ADMIN_PROJECTS, false)
				
				// Which site to load after login error
				.failureUrl(ApplicationPathHelper.URL_LOGIN_FORM)
										
			/*
			 * Logout procedure
			 */
			.and().logout()
				.logoutUrl(ApplicationPathHelper.URL_LOGOUT)
				.logoutSuccessUrl(ApplicationPathHelper.URL_HOME)
				.invalidateHttpSession(true)
				.clearAuthentication(true)
				
			/*
			 * Exception handling
			 */
			.and().exceptionHandling()
			
				// Use either own handler or
				.accessDeniedHandler(deniedHandler);
				// ... use this default handler
				// .accessDeniedPage(ApplicationPath.URL_ERROR)
				//;
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		auth
			.userDetailsService(userDetailService)
			.passwordEncoder(bCryptPasswordEncoder);
	}
	
	@PostConstruct
	private void init() {
		LOG.debug("Configuration loaded");
	}

}
