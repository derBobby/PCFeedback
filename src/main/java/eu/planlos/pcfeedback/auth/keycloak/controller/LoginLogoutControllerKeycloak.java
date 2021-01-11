package eu.planlos.pcfeedback.auth.keycloak.controller;

import org.keycloak.constants.ServiceUrlConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import eu.planlos.pcfeedback.constants.ApplicationPathHelper;

@Profile("KEYCLOAK")
@Controller
public class LoginLogoutControllerKeycloak {

	@Value("${keycloak.realm}")
	private String keycloakRealm;
	
	@Value("${keycloak.auth-server-url}")
	private String authServer;
	
//	@GetMapping(path = ApplicationPathHelper.URL_LOGIN_FORM)
//	public ModelAndView login() {
//
//		ModelMap model = new ModelMap();
//		model.addAttribute("realm-name", keycloakRealm);
//
//		String redirectURL = String.format(
//				"%sauth%s",
//				authServer,
//				ServiceUrlConstants.AUTH_PATH,
//				"response_type=code&client_id="
//				pcfeedback-login
//				"redirect_uri="
//				);
//
//								
//		ICH
//		"/sso/login"
//		"&state=9e0b91f4-7a57-4930-9566-5cc72c6efa1b&login=true&scope=openid
//			
//		RedirectView rv = new RedirectView(redirectURL);
//		
//		return new ModelAndView(rv, model);
//	}
	
	
	@GetMapping(path = ApplicationPathHelper.URL_LOGOUT)
	public ModelAndView logout() {

		ModelMap model = new ModelMap();
		model.addAttribute("realm-name", keycloakRealm);

		String redirectURL = String.format("%s%s", authServer, ServiceUrlConstants.TOKEN_SERVICE_LOGOUT_PATH);

		RedirectView rv = new RedirectView(redirectURL);
		
		return new ModelAndView(rv, model);
	}
}
