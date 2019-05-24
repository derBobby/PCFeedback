package eu.planlos.pcfeedback.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import eu.planlos.pcfeedback.constants.ApplicationPath;

@Controller
public class HomeController {

	@Autowired
	private ModelFillerService mfs;
	
	@RequestMapping(ApplicationPath.URL_HOME)
	public String home(Model model) {

		model.addAttribute("URL_FEEDBACK_START", ApplicationPath.URL_FEEDBACK_START);
		
		mfs.fill(model);
		
		return ApplicationPath.RES_HOME;
	}
}
