package eu.planlos.pcfeedback.auth.keycloak.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import eu.planlos.pcfeedback.constants.ApplicationPathHelper;

@Profile("KC")
@Controller
public class LoginControllerKeycloak {

	private static final Logger LOG = LoggerFactory.getLogger(LoginControllerKeycloak.class);
	
	@Value("${keycloak.realm}")
	private String keycloakRealm;
	
	@Value("${keycloak.auth-server-url}")
	private String authServer;
	
	@GetMapping(path = ApplicationPathHelper.URL_LOGIN_FORM)
	public String login() {		
		LOG.debug("Login requested -> Forwarding to secured page");
		return String.format("redirect:%s", ApplicationPathHelper.URL_ADMIN_PROJECTS);
	}
}
