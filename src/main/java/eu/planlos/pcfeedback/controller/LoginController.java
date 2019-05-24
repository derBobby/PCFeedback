package eu.planlos.pcfeedback.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import eu.planlos.pcfeedback.constants.ApplicationPath;
import eu.planlos.pcfeedback.model.LoginFormContainer;

@Controller
public class LoginController {
			
	@GetMapping(path = ApplicationPath.URL_LOGIN_FORM)
	public String loginpage(Model model, Authentication auth, HttpServletRequest request, @RequestParam(defaultValue = "false") boolean error) {

		prepareContent(model, auth, error);
		return ApplicationPath.RES_LOGIN_FORM;
	}
	
	private void prepareContent(Model model, Authentication auth, boolean error) {

		if(error) {
			model.addAttribute("error", "Login fehlgeschlagen!");
		}
		
		model.addAttribute("loginFormContainer", new LoginFormContainer());
		model.addAttribute("formAction", ApplicationPath.URL_LOGIN);
		
	}
}
