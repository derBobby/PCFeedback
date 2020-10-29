package eu.planlos.pcfeedback.controller.admin;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.planlos.pcfeedback.constants.ApplicationPathHelper;
import eu.planlos.pcfeedback.exceptions.UiTextException;
import eu.planlos.pcfeedback.model.Project;
import eu.planlos.pcfeedback.model.UiText;
import eu.planlos.pcfeedback.model.UiTextContainer;
import eu.planlos.pcfeedback.service.ModelFillerService;
import eu.planlos.pcfeedback.service.ProjectService;
import eu.planlos.pcfeedback.service.UiTextService;

@Controller
public class UiTextController {

	private static final Logger LOG = LoggerFactory.getLogger(UiTextController.class);
	
	@Autowired
	private UiTextService uts;
	
	@Autowired
	private ModelFillerService mfs;
	
	@Autowired
	private ProjectService ps;
	
	@RequestMapping(path = ApplicationPathHelper.URL_ADMIN_EDITUITEXT + "{projectName}", method = RequestMethod.GET)
	public String showUiText(@PathVariable(name = "projectName") String projectName, Model model) {

		Project project = ps.findProject(projectName);
		if(project == null) {
			//TODO implement
			return "FUCK";
		}
		
		List<UiText> uiTextList = uts.getUiTextForProject(project);
		model.addAttribute("uiTextList", uiTextList);
		
		mfs.fillGlobal(model);
		return ApplicationPathHelper.RES_ADMIN_EDITUITEXT;
	}
	
	@RequestMapping(path = ApplicationPathHelper.URL_ADMIN_EDITUITEXT, method = RequestMethod.POST)
	public String submitEdit(@ModelAttribute UiTextContainer uiTextContainer, Model model) throws UiTextException {

		try {
			uts.updateText(uiTextContainer.getIdUiText(), uiTextContainer.getText());
			return "redirect:" + ApplicationPathHelper.URL_ADMIN_EDITUITEXT + uiTextContainer.getProjectName();
		} catch (UiTextException e) {
			LOG.error(e.getMessage());
			throw e;
		}
	}
	
	
}
