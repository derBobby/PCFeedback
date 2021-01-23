package eu.planlos.pcfeedback.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import eu.planlos.pcfeedback.constants.ApplicationPathHelper;
import eu.planlos.pcfeedback.model.UiTextKey;
import eu.planlos.pcfeedback.model.db.Project;
import eu.planlos.pcfeedback.model.db.UiText;
import eu.planlos.pcfeedback.service.ModelFillerService;
import eu.planlos.pcfeedback.service.ProjectService;
import eu.planlos.pcfeedback.service.UiTextService;

@Controller
public class PriceGameController {

	private ProjectService projectService;
	private ModelFillerService mfs;
	private UiTextService uts;

	@Autowired
	public PriceGameController(ProjectService projectService, ModelFillerService mfs, UiTextService uts) {
		this.projectService = projectService;
		this.mfs = mfs;
		this.uts = uts;
	}
	
	/**
	 * Provides the price game site
	 * @param model
	 * @return home template to load
	 */
	@RequestMapping(ApplicationPathHelper.URL_PRICEGAME + "{projectName}")
	public String priceGame(Model model, @PathVariable(name = "projectName") String projectName) {
	
		Project project = projectService.findProject(projectName);
		UiText uiText = uts.getUiText(project, UiTextKey.MSG_PRICEGAME);
				
		model.addAttribute("projectName", projectName);
		mfs.fillUiText(model, uiText);
		mfs.fillGlobal(model);
		
		return ApplicationPathHelper.RES_PRICEGAME;
	}
}
