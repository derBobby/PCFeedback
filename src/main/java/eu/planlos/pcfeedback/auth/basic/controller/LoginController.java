package eu.planlos.pcfeedback.auth.basic.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import eu.planlos.pcfeedback.constants.ApplicationPaths;
import eu.planlos.pcfeedback.model.LoginFormContainer;
import eu.planlos.pcfeedback.service.ModelFillerService;

@Slf4j
@Controller
public class LoginController {
	
	private final ModelFillerService mfs;

	public LoginController(ModelFillerService mfs) {
		this.mfs = mfs;
	}
	
	/**
	 * Provides the login form page with login container object
	 * @param model Spring web model
	 * @param error Flag if errorous login request
	 * @return name of template
	 */
	@GetMapping(path = ApplicationPaths.URL_LOGIN_FORM)
	public String loginpage(Model model, @RequestParam(defaultValue = "false") boolean error) {
		
		prepareContent(model, error);
		return ApplicationPaths.RES_LOGIN_FORM;
	}
	
	private void prepareContent(Model model, boolean error) {

		if(error) {
			model.addAttribute("error", "Login fehlgeschlagen!");
		}
		
		mfs.fillGlobal(model);
		model.addAttribute("loginFormContainer", new LoginFormContainer());
		model.addAttribute("formAction", ApplicationPaths.URL_LOGIN);
	}
}
