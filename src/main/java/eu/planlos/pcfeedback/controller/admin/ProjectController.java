package eu.planlos.pcfeedback.controller.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
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
import eu.planlos.pcfeedback.exceptions.DuplicateRatingObjectException;
import eu.planlos.pcfeedback.exceptions.ProjectAlreadyExistingException;
import eu.planlos.pcfeedback.model.Project;
import eu.planlos.pcfeedback.model.RatingObject;
import eu.planlos.pcfeedback.model.RatingQuestion;
import eu.planlos.pcfeedback.service.ModelFillerService;
import eu.planlos.pcfeedback.service.ProjectService;
import eu.planlos.pcfeedback.service.RatingObjectService;
import eu.planlos.pcfeedback.service.RatingQuestionService;
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

	@Autowired
	private RatingObjectService ros;

	@Autowired
	private RatingQuestionService rqService;
	
	@RequestMapping(method = RequestMethod.GET, path = ApplicationPathHelper.URL_ADMIN_PROJECTS)
	public String listProjects(Model model) {
		
		List<Project> projectList = ps.findAll();
		
		for(Project project : projectList) {
			project.setOnline(ps.isOnline(project));
		}
		
		model.addAttribute("URL_ADMIN_PROJECTRUN", ApplicationPathHelper.URL_ADMIN_PROJECTRUN);
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

		mfs.fillProjectDetails(model, project);
		mfs.fillGlobal(model);

		return ApplicationPathHelper.RES_ADMIN_PROJECTDETAILS;
	}	
	
	@RequestMapping(method = RequestMethod.GET, path = ApplicationPathHelper.URL_ADMIN_PROJECTDETAILS + "/{projectName}")
	public String editProject(Model model, @PathVariable("projectName") String projectName) {
		
		Project project = ps.findProject(projectName);

		mfs.fillProjectDetails(model, project);
		mfs.fillGlobal(model);

		return ApplicationPathHelper.RES_ADMIN_PROJECTDETAILS;
	}
	
	@RequestMapping(method = RequestMethod.POST, path = ApplicationPathHelper.URL_ADMIN_PROJECTDETAILS)
	public String submitProject(Model model, ServletResponse response, @ModelAttribute("project") @Valid Project uiProject, BindingResult bindingResult) throws IOException {
			
		boolean isNewProject = false;
		if(uiProject.getIdProject() == null) {
			isNewProject = true;
		}
		
		HttpServletResponse res = (HttpServletResponse) response;
		
		if(! isNewProject) {
			Project dbProject = ps.findProject(uiProject.getIdProject());
			if(dbProject.isActive()) {
				LOG.error("Project name='{}' is active. Edit not allowed -> sending 403", dbProject.getProjectName());
				res.sendError(403, String.format("Projekt %s ist nicht aktiv. Speichern verboten.", dbProject.getProjectName()));
				return null;
			}
		}
		
		// validate model input
		if (bindingResult.hasErrors()) {
			LOG.debug("Input from form not valid");

			mfs.fillProjectDetails(model, uiProject);
			mfs.fillGlobal(model);
			
			return ApplicationPathHelper.RES_ADMIN_PROJECTDETAILS;
		}

		LOG.debug("Form input is valid");

		try {
			
			LOG.debug("Trying to save project: id={} | name={}", uiProject.getIdProject(), uiProject.getProjectName());
			
			ros.validateUniqueAndSaveList(uiProject.getRatingObjectList());

			ps.save(uiProject);

			if(isNewProject) {
				LOG.debug("Adding UiTexts for new Project");
				uts.initializeUiText(uiProject);
			}
			
			
			LOG.debug("Proceeding to projects page");
			
			return "redirect:" + ApplicationPathHelper.URL_ADMIN_PROJECTS;

		} catch (ProjectAlreadyExistingException | DuplicateRatingObjectException e) {

			LOG.error(e.getMessage());
			
			model.addAttribute("globalError", e.getMessage());
			
			mfs.fillProjectDetails(model, uiProject);
			mfs.fillGlobal(model);
			
			return ApplicationPathHelper.RES_ADMIN_PROJECTDETAILS;
		}
	}
	
	@RequestMapping(path = ApplicationPathHelper.URL_ADMIN_PROJECTRUN + "{projectName}")
	public String runProject(Model model, ServletResponse response, @PathVariable String projectName) throws ProjectAlreadyExistingException, IOException {
		
		HttpServletResponse res = (HttpServletResponse) response;

		Project project = ps.findProject(projectName);
		if(project == null) {
			LOG.error("Project name='{}' does not exist -> sending 400", projectName);
			res.sendError(404, String.format("Projekt %s wurde nicht gefunden.", projectName));
			return null;
		}
	
		project.setActive(true);
		
		List<RatingQuestion> rqList = new ArrayList<>();
		rqList.addAll(rqService.create(project));
		project.setActive(true);
		rqService.saveAll(rqList);
		ps.save(project);
		
		return "redirect:" + ApplicationPathHelper.URL_ADMIN_PROJECTS;
	}
}
