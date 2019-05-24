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
import org.springframework.stereotype.Service;

import eu.planlos.pcfeedback.constants.ApplicationRole;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

	@Value("${eu.planlos.pcfeedback.adminuser}")
	private String adminUser;

	@Value("${eu.planlos.pcfeedback.adminpassword}")
	private String adminPassword;
	
	@Override
	public UserDetails loadUserByUsername(String loginName) throws UsernameNotFoundException {
		
		if(loginName.equals(adminUser)) {
			logger.debug("Login fehlgeschlagen. Angegebener Benutzer: " + loginName);
			throw new UsernameNotFoundException("Login fehlgeschlagen.");
		}
		
		List<GrantedAuthority> authoritiesList = new ArrayList<>();
		authoritiesList.add(new SimpleGrantedAuthority(ApplicationRole.ROLE_ADMIN));
		
		logger.debug("Erstelle Benutzer aus Konfiguration: " + loginName + " (" + ApplicationRole.ROLE_ADMIN + ")");
		
		return new User(loginName, adminPassword, authoritiesList);
	}
}
