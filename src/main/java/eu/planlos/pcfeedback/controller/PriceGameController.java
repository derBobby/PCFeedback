package eu.planlos.pcfeedback.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import eu.planlos.pcfeedback.constants.ApplicationPathHelper;
import eu.planlos.pcfeedback.model.UiTextKey;
import eu.planlos.pcfeedback.service.ModelFillerService;

@Controller
public class PriceGameController {

	@Autowired
	private ModelFillerService mfs;
	
	/**
	 * Provides the price game site
	 * @param model
	 * @return home template to load
	 */
	@RequestMapping(ApplicationPathHelper.URL_PRICEGAME)
	public String priceGame(Model model) {
	
		mfs.fillUiText(model, UiTextKey.MSG_PRICEGAME);
		mfs.fillGlobal(model);
		
		return ApplicationPathHelper.RES_PRICEGAME;
	}
}
