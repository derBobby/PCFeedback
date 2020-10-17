package eu.planlos.pcfeedback.controller.admin;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.planlos.pcfeedback.constants.ApplicationPathHelper;
import eu.planlos.pcfeedback.exceptions.ProjectAlreadyExistingException;
import eu.planlos.pcfeedback.model.Project;
import eu.planlos.pcfeedback.service.ModelFillerService;
import eu.planlos.pcfeedback.service.ProjectService;

@Controller
public class ProjectController {

	private static Logger LOG = LoggerFactory.getLogger(ProjectController.class);
	
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
		model.addAttribute("URL_ADMIN_ADDPROJECTS", ApplicationPathHelper.URL_ADMIN_PROJECTDETAILS);
		mfs.fillGlobal(model);
		
		return ApplicationPathHelper.RES_ADMIN_PROJECTS;
	}
	
	@RequestMapping(method = RequestMethod.GET, path = ApplicationPathHelper.URL_ADMIN_PROJECTDETAILS)
	public String addProject(Model model) {
		
		Project project = new Project();

		mfs.fillProjectDetails(model, project, "Hinzufügen");
		mfs.fillGlobal(model);

		return ApplicationPathHelper.RES_ADMIN_PROJECTDETAILS;
	}
	
	@RequestMapping(method = RequestMethod.POST, path = ApplicationPathHelper.URL_ADMIN_PROJECTDETAILS)
	public String submitProject(Model model, @ModelAttribute("participant") @Valid Project project, BindingResult bindingResult) {
		
		String buttonText = "Hinzufügen";
		if(project.getIdProject() != null) {
			buttonText = "Speichern";
		}
		
		// validate model input
		if (bindingResult.hasErrors()) {
			LOG.debug("Input from form not valid");

			mfs.fillProjectDetails(model, project, buttonText);
			mfs.fillGlobal(model);
			
			return ApplicationPathHelper.RES_ADMIN_PROJECTDETAILS;
		}

		LOG.debug("Form input is valid");

		try {

			LOG.debug("Trying to save project: id={} | name={}", project.getIdProject(), project.getProjectName());
			ps.save(project);

			LOG.debug("Proceeding to projects page");
			
			return "redirect:" + ApplicationPathHelper.URL_ADMIN_PROJECTS;

		} catch (ProjectAlreadyExistingException e) {

			LOG.error("Project exists already, returning to form");
			
			mfs.fillProjectDetails(model, project, buttonText);
			mfs.fillGlobal(model);
			
			return ApplicationPathHelper.RES_ADMIN_PROJECTDETAILS;
		}
	}
}
