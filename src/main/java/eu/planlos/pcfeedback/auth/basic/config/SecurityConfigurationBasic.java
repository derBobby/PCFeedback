package eu.planlos.pcfeedback.auth.basic.config;

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

import eu.planlos.pcfeedback.auth.basic.service.LoginAccessDeniedHandler;
import eu.planlos.pcfeedback.auth.basic.service.UserDetailsServiceImpl;
import eu.planlos.pcfeedback.config.AuthConfiguration;
import eu.planlos.pcfeedback.constants.ApplicationPathHelper;

@Profile("!KC")
@EnableWebSecurity
public class SecurityConfigurationBasic extends WebSecurityConfigurerAdapter {

	private static final Logger LOG = LoggerFactory.getLogger(SecurityConfigurationBasic.class);
	
	private AuthConfiguration authConfiguration;
	
	private UserDetailsServiceImpl userDetailService;
	
	private LoginAccessDeniedHandler deniedHandler;
	
	@Autowired
	public SecurityConfigurationBasic(AuthConfiguration authConfiguration,	UserDetailsServiceImpl userDetailService, LoginAccessDeniedHandler deniedHandler) {
		this.authConfiguration = authConfiguration;
		this.userDetailService = userDetailService;
		this.deniedHandler = deniedHandler;
	}
	
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
						
					).hasRole(authConfiguration.getAdminRole())
				
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
				.logoutUrl(authConfiguration.getLogoutUrl())
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
