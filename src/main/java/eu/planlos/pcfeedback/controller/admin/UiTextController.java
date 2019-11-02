package eu.planlos.pcfeedback.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.planlos.pcfeedback.constants.ApplicationPath;
import eu.planlos.pcfeedback.model.UiText;
import eu.planlos.pcfeedback.service.ModelFillerService;
import eu.planlos.pcfeedback.service.UiTextService;

@Controller
public class UiTextController {

	@Autowired
	private UiTextService uts;
	
	@Autowired
	private ModelFillerService mfs;
	
	@RequestMapping(path = ApplicationPath.URL_ADMIN_EDITUITEXT, method = RequestMethod.GET)
	public String showUiText(Model model) {
		
		List<UiText> uiTextList = uts.getAllUiText();
		model.addAttribute("uiTextList", uiTextList);
		
		mfs.fillGlobal(model);
		return ApplicationPath.RES_ADMIN_EDITUITEXT;
	}
	
	@RequestMapping(path = ApplicationPath.URL_ADMIN_EDITUITEXT, method = RequestMethod.POST)
	public String submitEdit(@PathVariable UiText uiText, Model model) {
		return "l2";
	}
	
	
}
