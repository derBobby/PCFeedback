package eu.planlos.pcfeedback.controller.admin;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.planlos.pcfeedback.constants.ApplicationPathHelper;
import eu.planlos.pcfeedback.exceptions.ProjectAlreadyExistingException;
import eu.planlos.pcfeedback.model.Project;
import eu.planlos.pcfeedback.model.RatingObject;
import eu.planlos.pcfeedback.service.ModelFillerService;
import eu.planlos.pcfeedback.service.ProjectService;
import eu.planlos.pcfeedback.service.UiTextService;

@Controller
public class ProjectController {

	private static Logger LOG = LoggerFactory.getLogger(ProjectController.class);

	@Autowired
	private ProjectService ps;
	
	@Autowired
	private UiTextService uts;
	
	@Autowired
	private ModelFillerService mfs;
	
	@RequestMapping(method = RequestMethod.GET, path = ApplicationPathHelper.URL_ADMIN_PROJECTS)
	public String projects(Model model) {
		
		List<Project> projectList = ps.findAll();
		model.addAttribute("projectList", projectList);

		mfs.fillGlobal(model);
		
		return ApplicationPathHelper.RES_ADMIN_PROJECTS;
	}
	
	@RequestMapping(method = RequestMethod.GET, path = ApplicationPathHelper.URL_ADMIN_PROJECTDETAILS)
	public String addProject(Model model) {
		
		List<RatingObject> roList = new ArrayList<>();
		roList.add(new RatingObject("Wertungsobjekt 1"));
		roList.add(new RatingObject("Wertungsobjekt 2"));
		roList.add(new RatingObject("Wertungsobjekt 3"));
		Project project = new Project(roList);

		mfs.fillProjectDetails(model, project, "Hinzufügen");
		mfs.fillGlobal(model);

		return ApplicationPathHelper.RES_ADMIN_PROJECTDETAILS;
	}	
	
	@RequestMapping(method = RequestMethod.GET, path = ApplicationPathHelper.URL_ADMIN_PROJECTDETAILS + "/{projectName}")
	public String editProject(Model model, @PathVariable("projectName") String projectName) {
		
		Project project = ps.findProject(projectName);
		
		mfs.fillProjectDetails(model, project, "Hinzufügen");
		mfs.fillGlobal(model);

		return ApplicationPathHelper.RES_ADMIN_PROJECTDETAILS;
	}
	
	@RequestMapping(method = RequestMethod.POST, path = ApplicationPathHelper.URL_ADMIN_PROJECTDETAILS)
	public String submitProject(Model model, @ModelAttribute("project") @Valid Project project, BindingResult bindingResult) {
		
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
			
			if(project.getIdProject() == null) {
				uts.initializeUiText(project);
			}
			
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
