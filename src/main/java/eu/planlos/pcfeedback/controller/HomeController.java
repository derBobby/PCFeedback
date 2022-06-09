package eu.planlos.pcfeedback.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import eu.planlos.pcfeedback.constants.ApplicationPaths;
import eu.planlos.pcfeedback.model.db.Project;
import eu.planlos.pcfeedback.service.ModelFillerService;
import eu.planlos.pcfeedback.service.ProjectService;

@Controller
public class HomeController {

	private final ModelFillerService mfs;
	private final ProjectService projectService;
	
	public HomeController(ModelFillerService mfs, ProjectService projectService) {
		this.mfs = mfs;
		this.projectService = projectService;
	}
	
	@RequestMapping(path = ApplicationPaths.URL_HOME)
	public String home(Model model) {
		
		List<Project> pList = projectService.getActive();
		model.addAttribute("projectList", pList);
		
		model.addAttribute("URL_PROJECTHOME", ApplicationPaths.URL_PROJECTHOME);
		mfs.fillGlobal(model);
		return ApplicationPaths.RES_HOME;
	}
}
