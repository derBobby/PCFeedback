package eu.planlos.pcfeedback.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import eu.planlos.pcfeedback.constants.ApplicationPathHelper;
import eu.planlos.pcfeedback.model.db.Project;
import eu.planlos.pcfeedback.service.ModelFillerService;
import eu.planlos.pcfeedback.service.ProjectService;

@Controller
public class HomeController {

	private ModelFillerService mfs;
	private ProjectService projectService;
	
	@Autowired
	public HomeController(ModelFillerService mfs, ProjectService projectService) {
		this.mfs = mfs;
		this.projectService = projectService;
	}
	
	@RequestMapping(path = ApplicationPathHelper.URL_HOME)
	public String home(Model model) {
		
		List<Project> pList = projectService.getActive();
		model.addAttribute("projectList", pList);
		
		model.addAttribute("URL_PROJECTHOME", ApplicationPathHelper.URL_PROJECTHOME);
		mfs.fillGlobal(model);
		return ApplicationPathHelper.RES_HOME;
	}
}
