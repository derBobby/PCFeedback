package eu.planlos.pcfeedback.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import eu.planlos.pcfeedback.constants.ApplicationPath;
import eu.planlos.pcfeedback.service.ModelFillerService;

@Controller
public class HomeController {

	@Autowired
	private ModelFillerService mfs;
	
	@RequestMapping(ApplicationPath.URL_HOME)
	public String home(Model model) {

		mfs.fillAnonymous(model);
		mfs.fillStartFeedback(model);
		mfs.fillGlobal(model);
		
		return ApplicationPath.RES_HOME;
	}
}
