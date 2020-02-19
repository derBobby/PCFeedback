package eu.planlos.pcfeedback.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import eu.planlos.pcfeedback.constants.ApplicationPath;
import eu.planlos.pcfeedback.constants.ApplicationRole;
import eu.planlos.pcfeedback.service.LoginAccessDeniedHandler;
import eu.planlos.pcfeedback.service.UserDetailsServiceImpl;

//Schalter für SimpleAuthentication
//@EnableGlobalMethodSecurity(securedEnabled = true)
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

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
						ApplicationPath.URL_AREA_ADMIN + "**",
						ApplicationPath.URL_AREA_ACTUATOR + "/**"
					).hasAnyAuthority(
						ApplicationRole.ROLE_ADMIN
					)
				
				/*
				 * PUBLIC
				 */
				.antMatchers(
						"/webjars/**",
						"/css/**",
						"/img/**",
						"/favicon.ico",
						ApplicationPath.URL_AREA_PUBLIC + "**"
					).permitAll()
					
			
			/*
			 * Login procedure
			 */
			.and().formLogin()
				
				// Overrides the default created login form site
				.loginPage(ApplicationPath.URL_LOGIN_FORM)
				
				// Names URL on which Spring should listen itself
				.loginProcessingUrl(ApplicationPath.URL_LOGIN)
				
				// NOT USED - Controller for successfull login
				//.successHandler(successHandler)
				
				// Redirects to given page 
				.defaultSuccessUrl(ApplicationPath.URL_ADMIN_SHOWFEEDBACK, false)
				
				// Which site to load after login error
				.failureUrl(ApplicationPath.URL_LOGIN_FORM)
										
			/*
			 * Logout procedure
			 */
			.and().logout()
				.logoutUrl(ApplicationPath.URL_LOGOUT)
				.logoutSuccessUrl(ApplicationPath.URL_HOME)
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
				;
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		auth
			.userDetailsService(userDetailService)
			.passwordEncoder(bCryptPasswordEncoder);
	}

}
