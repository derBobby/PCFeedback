package eu.planlos.pcfeedback.auth.basic.service;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import eu.planlos.pcfeedback.config.RoleConfiguration;
import eu.planlos.pcfeedback.constants.ApplicationProfileHelper;

@Profile("!KC")
@Service
public class UserDetailsServiceImpl implements UserDetailsService, EnvironmentAware {

	private static final Logger LOG = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

	@Value("${eu.planlos.pcfeedback.auth.admin.user}")
	private String adminUser;

	@Value("${eu.planlos.pcfeedback.auth.admin.password}")
	private String adminPassword;
	
	private Environment environment;
	
	private RoleConfiguration roleConfiguration;
	
	@Autowired
	public UserDetailsServiceImpl(RoleConfiguration roleConfiguration) {
		this.roleConfiguration = roleConfiguration;
	}
	
	@Override
	/**
	*	@throws UsernameNotFoundException Thrown if the user provided is not the admin user
	*/
	public UserDetails loadUserByUsername(final String loginName) {

		LOG.debug("Loading simple auth user");
		
		final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		final List<GrantedAuthority> authoritiesList = new ArrayList<>();
		
		if(loginName.equals(adminUser)) {
			
			String roleString = String.format("ROLE_%s", roleConfiguration.getAdminRole());
			
			LOG.debug("Erstelle Benutzer aus Konfiguration: {} ({})", loginName, roleString);
			authoritiesList.add(new SimpleGrantedAuthority(roleString));
			return new User(loginName, passwordEncoder.encode(adminPassword), authoritiesList);
		}
		
		LOG.debug("Login fehlgeschlagen. Angegebener Benutzer: {}", loginName);
		throw new UsernameNotFoundException("Login fehlgeschlagen.");
	}
	
	@PostConstruct
	private void printCredentials() {
		/*
		 * URLs for DEV profile
		 */
		List<String> profiles = Arrays.asList(environment.getActiveProfiles());

		if (profiles.contains(ApplicationProfileHelper.DEV_PROFILE)
				|| profiles.contains(ApplicationProfileHelper.REV_PROFILE)) {
			LOG.debug("Setup admin user as user='{}' password='{}'", adminUser, adminPassword);
		}
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}
}
