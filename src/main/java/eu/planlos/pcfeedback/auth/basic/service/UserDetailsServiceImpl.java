package eu.planlos.pcfeedback.auth.basic.service;


import eu.planlos.pcfeedback.config.AuthConfiguration;
import eu.planlos.pcfeedback.constants.ApplicationProfiles;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService, EnvironmentAware {

	private final AuthConfiguration authConfiguration;

	@Value("${eu.planlos.pcfeedback.auth.admin.user}")
	private String adminUser;

	@Value("${eu.planlos.pcfeedback.auth.admin.password}")
	private String adminPassword;
	
	private Environment environment;

	public UserDetailsServiceImpl(AuthConfiguration authConfiguration) {
		this.authConfiguration = authConfiguration;
	}
	
	@Override
	/**
	*	@throws UsernameNotFoundException Thrown if the user provided is not the admin user
	*/
	public UserDetails loadUserByUsername(final String loginName) {

		log.debug("Loading simple auth user");
		
		final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		final List<GrantedAuthority> authoritiesList = new ArrayList<>();
		
		if(loginName.equals(adminUser)) {
			String roleString = String.format("ROLE_%s", authConfiguration.getAdminRole());
			
			log.debug("Erstelle Benutzer aus Konfiguration: {} ({})", loginName, roleString);
			authoritiesList.add(new SimpleGrantedAuthority(roleString));
			return new User(loginName, passwordEncoder.encode(adminPassword), authoritiesList);
		}
		log.debug("Login fehlgeschlagen. Angegebener Benutzer: {}", loginName);
		throw new UsernameNotFoundException("Login fehlgeschlagen.");
	}
	
	@PostConstruct
	private void printCredentials() {
		/*
		 * URLs for DEV profile
		 */
		List<String> profiles = Arrays.asList(environment.getActiveProfiles());

		if (profiles.contains(ApplicationProfiles.DEV_PROFILE)
				|| profiles.contains(ApplicationProfiles.REV_PROFILE)) {
			log.debug("Setup admin user as user='{}' password='{}'", adminUser, adminPassword);
		}
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}
}