package eu.planlos.pcfeedback.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import eu.planlos.pcfeedback.constants.ApplicationPathHelper;
import eu.planlos.pcfeedback.model.UiTextKey;
import eu.planlos.pcfeedback.service.ModelFillerService;

@Controller
public class HomeController {

	@Autowired
	private ModelFillerService mfs;
	
	/**
	 * Provides to index page
	 * @param model
	 * @return home template to load
	 */
	@RequestMapping(ApplicationPathHelper.URL_HOME)
	public String home(Model model) {
	
		mfs.fillUiText(model, UiTextKey.MSG_HOME);
		mfs.fillGlobal(model);
		
		return ApplicationPathHelper.RES_HOME;
	}
}
