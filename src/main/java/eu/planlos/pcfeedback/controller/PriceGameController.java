package eu.planlos.pcfeedback.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import eu.planlos.pcfeedback.constants.ApplicationPathHelper;
import eu.planlos.pcfeedback.constants.SessionAttributeHelper;
import eu.planlos.pcfeedback.model.Project;
import eu.planlos.pcfeedback.model.UiTextKey;
import eu.planlos.pcfeedback.service.ModelFillerService;
import eu.planlos.pcfeedback.service.ProjectService;

@Controller
public class PriceGameController {

	@Autowired
	private ModelFillerService mfs;

	@Autowired
	private ProjectService ps;
	
	/**
	 * Provides the price game site
	 * @param model
	 * @return home template to load
	 */
	@RequestMapping(ApplicationPathHelper.URL_PRICEGAME)
	public String priceGame(HttpSession session, Model model) {
	
		Project project = (Project) session.getAttribute(SessionAttributeHelper.PROJECT);
		
		mfs.fillUiText(model, project, UiTextKey.MSG_PRICEGAME);
		mfs.fillGlobal(model);
		
		return ApplicationPathHelper.RES_PRICEGAME;
	}
}
