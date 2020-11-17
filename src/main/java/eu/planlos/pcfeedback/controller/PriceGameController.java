package eu.planlos.pcfeedback.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import eu.planlos.pcfeedback.constants.ApplicationPathHelper;
import eu.planlos.pcfeedback.model.UiTextKey;
import eu.planlos.pcfeedback.model.db.Project;
import eu.planlos.pcfeedback.service.ModelFillerService;
import eu.planlos.pcfeedback.service.ProjectService;

@Controller
public class PriceGameController {

	@Autowired
	private ProjectService ps;
	
	@Autowired
	private ModelFillerService mfs;
	
	/**
	 * Provides the price game site
	 * @param model
	 * @return home template to load
	 */
	@RequestMapping(ApplicationPathHelper.URL_PRICEGAME + "{projectName}")
	public String priceGame(Model model, @PathVariable(name = "projectName") String projectName) {
	
		Project project = ps.findProject(projectName);
		
		mfs.fillUiText(model, project, UiTextKey.MSG_PRICEGAME);
		mfs.fillGlobal(model);
		
		return ApplicationPathHelper.RES_PRICEGAME;
	}
}
