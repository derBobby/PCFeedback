package eu.planlos.pcfeedback.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import eu.planlos.pcfeedback.constants.ApplicationPathHelper;
import eu.planlos.pcfeedback.model.Project;
import eu.planlos.pcfeedback.service.ModelFillerService;
import eu.planlos.pcfeedback.service.ProjectService;

@Controller
public class HomeController {

	@Autowired
	private ModelFillerService mfs;
	
	@Autowired
	private ProjectService ps;
	
	@RequestMapping(path = ApplicationPathHelper.URL_HOME)
	public String home(Model model) {
		
		List<Project> pList = ps.findAll();
		model.addAttribute("projectList", pList);
		
		model.addAttribute("URL_PROJECTHOME", ApplicationPathHelper.URL_PROJECTHOME);
		mfs.fillGlobal(model);
		return ApplicationPathHelper.RES_HOME;
	}
}
