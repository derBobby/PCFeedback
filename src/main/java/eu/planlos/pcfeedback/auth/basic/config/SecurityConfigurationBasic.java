package eu.planlos.pcfeedback.auth.basic.config;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import eu.planlos.pcfeedback.auth.basic.service.LoginAccessDeniedHandler;
import eu.planlos.pcfeedback.auth.basic.service.UserDetailsServiceImpl;
import eu.planlos.pcfeedback.config.AuthConfiguration;
import eu.planlos.pcfeedback.constants.ApplicationPaths;

@Slf4j
@EnableWebSecurity
public class SecurityConfigurationBasic extends WebSecurityConfigurerAdapter {

	private final AuthConfiguration authConfiguration;
	
	private final UserDetailsServiceImpl userDetailService;
	
	private final LoginAccessDeniedHandler deniedHandler;

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
						ApplicationPaths.URL_AREA_ADMIN + "**",
						ApplicationPaths.URL_AREA_ACTUATOR + "/**"
						
					).hasRole(authConfiguration.getAdminRole())
				
				/*
				 * PUBLIC
				 */
				.antMatchers(
						"/webjars/**",
						"/css/**",
						"/img/**",
						"/favicon.ico",
						ApplicationPaths.URL_AREA_PUBLIC + "**"
						
					).permitAll()
					
			
			/*
			 * Login procedure
			 */
			.and().formLogin()
				
				// Overrides the default created login form site
				.loginPage(ApplicationPaths.URL_LOGIN_FORM)
				
				// Names URL on which Spring should listen itself
				.loginProcessingUrl(ApplicationPaths.URL_LOGIN)
				
				// NOT USED - Controller for successfull login
				//.successHandler(successHandler)
				
				// Redirects to given page 
				.defaultSuccessUrl(ApplicationPaths.URL_ADMIN_PROJECTS, false)
				
				// Which site to load after login error
				.failureUrl(ApplicationPaths.URL_LOGIN_FORM)
										
			/*
			 * Logout procedure
			 */
			.and().logout()
				.logoutUrl(authConfiguration.getLogoutUrl())
				.logoutSuccessUrl(ApplicationPaths.URL_HOME)
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
		log.debug("Configuration loaded");
	}

}
