package eu.planlos.pcfeedback.service;


import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import eu.planlos.pcfeedback.constants.ApplicationRoleHelper;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private static final Logger LOG = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

	@Value("${eu.planlos.pcfeedback.adminuser}")
	private String adminUser;

	@Value("${eu.planlos.pcfeedback.adminpassword}")
	private String adminPassword;
	
	@Override
	/**
	*	@throws UsernameNotFoundException Thrown if the user provided is not the admin user
	*/
	public UserDetails loadUserByUsername(final String loginName) {

		final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		final List<GrantedAuthority> authoritiesList = new ArrayList<>();
		
		if(loginName.equals(adminUser)) {
			LOG.debug("Erstelle Benutzer aus Konfiguration: {} ({})", loginName, ApplicationRoleHelper.ROLE_ADMIN);
			authoritiesList.add(new SimpleGrantedAuthority(ApplicationRoleHelper.ROLE_ADMIN));
			return new User(loginName, passwordEncoder.encode(adminPassword), authoritiesList);
		}
		
		LOG.debug("Login fehlgeschlagen. Angegebener Benutzer: {}", loginName);
		throw new UsernameNotFoundException("Login fehlgeschlagen.");
	}
}
