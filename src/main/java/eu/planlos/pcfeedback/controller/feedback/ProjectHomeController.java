package eu.planlos.pcfeedback.controller.feedback;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import eu.planlos.pcfeedback.constants.ApplicationPathHelper;
import eu.planlos.pcfeedback.constants.SessionAttributeHelper;
import eu.planlos.pcfeedback.model.Project;
import eu.planlos.pcfeedback.model.UiTextKey;
import eu.planlos.pcfeedback.service.ModelFillerService;
import eu.planlos.pcfeedback.service.ProjectService;

@Controller
public class ProjectHomeController {

	@Autowired
	private ModelFillerService mfs;
	
	@Autowired
	private ProjectService ps;
	
	/**
	 * Provides to index page
	 * @param model
	 * @return home template to load
	 */
	@RequestMapping(ApplicationPathHelper.URL_PROJECTHOME + "{projectName}")
	public String projectHome(HttpSession session, Model model, @PathVariable("projectName") String projectName) {

		String result = "redirect:" + ApplicationPathHelper.URL_HOME;
		
		if(ps.exists(projectName)) {
			
			Project project = ps.findProject(projectName);
			session.setAttribute(SessionAttributeHelper.PROJECT, project);
			
			mfs.fillUiText(model, project, UiTextKey.MSG_PROJECTHOME);
			mfs.fillGlobal(model);
			result = ApplicationPathHelper.RES_PROJECTHOME;
		}
		
		return result;
	}
}
