package eu.planlos.pcfeedback.controller.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.planlos.pcfeedback.constants.ApplicationPaths;
import eu.planlos.pcfeedback.exceptions.DuplicateRatingObjectException;
import eu.planlos.pcfeedback.exceptions.ProjectAlreadyExistingException;
import eu.planlos.pcfeedback.exceptions.WrongRatingQuestionCountExistingException;
import eu.planlos.pcfeedback.model.db.Project;
import eu.planlos.pcfeedback.model.db.RatingObject;
import eu.planlos.pcfeedback.model.db.RatingQuestion;
import eu.planlos.pcfeedback.service.ModelFillerService;
import eu.planlos.pcfeedback.service.ParticipantService;
import eu.planlos.pcfeedback.service.ParticipationResultService;
import eu.planlos.pcfeedback.service.ProjectService;
import eu.planlos.pcfeedback.service.RatingObjectService;
import eu.planlos.pcfeedback.service.RatingQuestionService;
import eu.planlos.pcfeedback.service.UiTextService;

@Slf4j
@Controller
public class ProjectController {

	private final ProjectService projectService;
	private final ParticipantService participantService;
	private final ParticipationResultService prs;
	private final UiTextService uts;
	private final ModelFillerService mfs;
	private final RatingObjectService ros;
	private final RatingQuestionService rqs;
	
	public ProjectController(ProjectService projectService, ParticipantService participantService,
			ParticipationResultService prs, UiTextService uts,
			ModelFillerService mfs, RatingObjectService ros, RatingQuestionService rqs) {
		this.projectService = projectService;
		this.participantService = participantService;
		this.prs = prs;
		this.uts = uts;
		this.mfs = mfs;
		this.ros = ros;
		this.rqs = rqs;
	}
	
	@RequestMapping(method = RequestMethod.GET, path = ApplicationPaths.URL_ADMIN_PROJECTS)
	public String listProjects(Model model) {
		
		List<Project> projectList = projectService.findAll();

		model.addAttribute("URL_ADMIN_PROJECTRUN", ApplicationPaths.URL_ADMIN_PROJECTRUN);
		model.addAttribute("URL_ADMIN_PROJECTRESET", ApplicationPaths.URL_ADMIN_PROJECTRESET);
		model.addAttribute("URL_ADMIN_PROJECTDELETE", ApplicationPaths.URL_ADMIN_PROJECTDELETE);
		model.addAttribute("projectList", projectList);

		mfs.fillGlobal(model);
		
		return ApplicationPaths.RES_ADMIN_PROJECTS;
	}
	
	@RequestMapping(method = RequestMethod.GET, path = ApplicationPaths.URL_ADMIN_PROJECTDETAILS)
	public String addProject(Model model) {
		
		List<RatingObject> roList = new ArrayList<>();
		roList.add(new RatingObject("Wertungsobjekt 1"));
		roList.add(new RatingObject("Wertungsobjekt 2"));
		roList.add(new RatingObject("Wertungsobjekt 3"));
		Project project = new Project(roList);

		mfs.fillProjectDetails(model, project);
		mfs.fillGlobal(model);

		return ApplicationPaths.RES_ADMIN_PROJECTDETAILS;
	}	
	
	@RequestMapping(path = ApplicationPaths.URL_ADMIN_PROJECTDETAILS + "/{projectName}")
	public String editProject(Model model, @PathVariable("projectName") String projectName) {
		
		Project project = projectService.findProject(projectName);

		mfs.fillProjectDetails(model, project);
		mfs.fillGlobal(model);

		return ApplicationPaths.RES_ADMIN_PROJECTDETAILS;
	}
	
	@RequestMapping(method = RequestMethod.POST, path = ApplicationPaths.URL_ADMIN_PROJECTDETAILS)
	public String submitProject(Model model, ServletResponse response, @ModelAttribute("project") @Valid Project uiProject, BindingResult bindingResult) throws IOException {
			
		boolean isNewProject = false;
		if(uiProject.getIdProject() == null) {
			isNewProject = true;
		}
		
		HttpServletResponse res = (HttpServletResponse) response;
		
		if(! isNewProject) {
			Project dbProject = projectService.findProject(uiProject.getIdProject());
			if(dbProject.isActive()) {
				log.error("Project name='{}' is active. Edit not allowed -> sending 403", dbProject.getProjectName());
				res.sendError(403, String.format("Projekt %s ist nicht aktiv. Speichern verboten.", dbProject.getProjectName()));
				return null;
			}
		}
		
		// validate model input
		if (bindingResult.hasErrors()) {
			log.debug("Input from form not valid");

			mfs.fillProjectDetails(model, uiProject);
			mfs.fillGlobal(model);
			
			return ApplicationPaths.RES_ADMIN_PROJECTDETAILS;
		}

		log.debug("Form input is valid");

		try {
			
			log.debug("Trying to save project: id='{}' | name='{}'", uiProject.getIdProject(), uiProject.getProjectName());
			
			rqs.checkEnoughRatingQuestions(uiProject, false);
			ros.validateAndSaveList(uiProject.getRatingObjectList());
			
			projectService.save(uiProject);

			if(isNewProject) {
				log.debug("Adding UiTexts for new Project");
				uts.initializeUiText(uiProject);
			}
			
			
			log.debug("Proceeding to projects page");
			
			return "redirect:" + ApplicationPaths.URL_ADMIN_PROJECTS;

		} catch (ProjectAlreadyExistingException | DuplicateRatingObjectException | WrongRatingQuestionCountExistingException e) {

			log.error(e.getMessage());
			
			model.addAttribute("globalError", e.getMessage());
			
			mfs.fillProjectDetails(model, uiProject);
			mfs.fillGlobal(model);
			
			return ApplicationPaths.RES_ADMIN_PROJECTDETAILS;
		}
	}
	
	@RequestMapping(path = ApplicationPaths.URL_ADMIN_PROJECTRUN + "{projectName}")
	public String runProject(Model model, ServletResponse response, @PathVariable String projectName) throws ProjectAlreadyExistingException, IOException {
		
		HttpServletResponse res = (HttpServletResponse) response;

		Project project = projectService.findProject(projectName);
		if(project == null) {
			log.error("Project name='{}' does not exist -> sending 400", projectName);
			res.sendError(404, String.format("Projekt %s wurde nicht gefunden.", projectName));
			return null;
		}
	
		project.setActive(true);
		
		List<RatingQuestion> rqList = new ArrayList<>();
		rqList.addAll(rqs.create(project));
		project.setActive(true);
		rqs.saveAll(rqList);
		projectService.save(project);
		
		return "redirect:" + ApplicationPaths.URL_ADMIN_PROJECTS;
	}
	
	@RequestMapping(path = ApplicationPaths.URL_ADMIN_PROJECTRESET + "{projectName}")
	public String resetProject(Model model, ServletResponse response, @PathVariable String projectName) throws ProjectAlreadyExistingException, IOException {
		
		HttpServletResponse res = (HttpServletResponse) response;

		Project project = projectService.findProject(projectName);
		if(project == null) {
			log.error("Project name='{}' does not exist -> sending 400", projectName);
			res.sendError(404, String.format("Projekt %s wurde nicht gefunden.", projectName));
			return null;
		}
	
		log.debug("Resetting Project name={}", projectName);
		
		prs.resetProject(project);
		participantService.resetProject(project);
		projectService.resetProject(project);
		rqs.resetProject(project);
		
		return "redirect:" + ApplicationPaths.URL_ADMIN_PROJECTS;
	}	
	@RequestMapping(path = ApplicationPaths.URL_ADMIN_PROJECTDELETE+ "{projectName}")
	public String deleteProject(Model model, ServletResponse response, @PathVariable String projectName) throws IOException, ProjectAlreadyExistingException {
		
		HttpServletResponse res = (HttpServletResponse) response;

		Project project = projectService.findProject(projectName);
		if(project == null) {
			log.error("Project name='{}' does not exist -> sending 400", projectName);
			res.sendError(404, String.format("Projekt %s wurde nicht gefunden.", projectName));
			return null;
		}
	
		log.debug("Deleting Project name='{}'", projectName);
		
		prs.resetProject(project);
		participantService.resetProject(project);
		rqs.resetProject(project);
		uts.deleteByProject(project);
		
		List<RatingObject> roList = project.getRatingObjectList();
		project.setRatingObjectList(null);
		projectService.save(project);
		
		ros.deleteAll(roList);
		projectService.deleteProject(project); 
		
		return "redirect:" + ApplicationPaths.URL_ADMIN_PROJECTS;
	}
}
