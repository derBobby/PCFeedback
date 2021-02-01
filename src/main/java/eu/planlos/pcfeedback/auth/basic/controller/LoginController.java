package eu.planlos.pcfeedback.auth.basic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import eu.planlos.pcfeedback.constants.ApplicationPathHelper;
import eu.planlos.pcfeedback.model.LoginFormContainer;
import eu.planlos.pcfeedback.service.ModelFillerService;

@Profile("!KC")
@Controller
public class LoginController {
	
	private ModelFillerService mfs;
	
	@Autowired
	public LoginController(ModelFillerService mfs) {
		this.mfs = mfs;
	}
	
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
