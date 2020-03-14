package eu.planlos.pcfeedback.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.planlos.pcfeedback.constants.ApplicationPathHelper;
import eu.planlos.pcfeedback.model.Project;
import eu.planlos.pcfeedback.service.ModelFillerService;
import eu.planlos.pcfeedback.service.ProjectService;

@Controller
public class ProjectController {

	@Autowired
	private ProjectService ps;
	
	@Autowired
	private ModelFillerService mfs;
	
	@RequestMapping(method = RequestMethod.GET, path = ApplicationPathHelper.URL_ADMIN_PROJECTS)
	public String projects(Model model) {
		
		List<Project> projectList = ps.findAll();
		model.addAttribute("projectList", projectList);

		model.addAttribute("URL_ADMIN_SHOWFEEDBACK", ApplicationPathHelper.URL_ADMIN_SHOWFEEDBACK);
		model.addAttribute("URL_PROJECTHOME", ApplicationPathHelper.URL_PROJECTHOME);
		
		mfs.fillGlobal(model);
		
		return ApplicationPathHelper.RES_ADMIN_PROJECTS;
	}
}
