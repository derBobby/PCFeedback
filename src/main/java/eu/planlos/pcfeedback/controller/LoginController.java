package eu.planlos.pcfeedback.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import eu.planlos.pcfeedback.constants.ApplicationPathHelper;
import eu.planlos.pcfeedback.model.LoginFormContainer;
import eu.planlos.pcfeedback.service.ModelFillerService;

@Controller
public class LoginController {
	
	@Autowired
	private ModelFillerService mfs;
	
	/**
	 * Provides the login form page with login container object
	 * @param model
	 * @param error
	 * @return
	 */
	@GetMapping(path = ApplicationPathHelper.URL_LOGIN_FORM)
	public String loginpage(Model model, @RequestParam(defaultValue = "false") boolean error) {
		
		prepareContent(model, error);
		return ApplicationPathHelper.RES_LOGIN_FORM;
	}
	
	private void prepareContent(Model model, boolean error) {

		if(error) {
			model.addAttribute("error", "Login fehlgeschlagen!");
		}
		
		mfs.fillGlobal(model);
		model.addAttribute("loginFormContainer", new LoginFormContainer());
		model.addAttribute("formAction", ApplicationPathHelper.URL_LOGIN);
		
	}
}
