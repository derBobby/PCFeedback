package eu.planlos.pcfeedback.controller.feedback;

import java.io.IOException;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import eu.planlos.pcfeedback.constants.ApplicationPathHelper;
import eu.planlos.pcfeedback.constants.SessionAttributeHelper;
import eu.planlos.pcfeedback.model.UiTextKey;
import eu.planlos.pcfeedback.model.db.Project;
import eu.planlos.pcfeedback.model.db.UiText;
import eu.planlos.pcfeedback.service.ModelFillerService;
import eu.planlos.pcfeedback.service.ProjectService;
import eu.planlos.pcfeedback.service.UiTextService;

@Controller
public class ProjectHomeController {

	private static final Logger LOG = LoggerFactory.getLogger(ProjectHomeController.class);

	private ModelFillerService mfs;
	private ProjectService projectService;
	private UiTextService uts;
	
	@Autowired
	public ProjectHomeController(ModelFillerService mfs, ProjectService projectService, UiTextService uts) {
		this.mfs = mfs;
		this.projectService = projectService;
		this.uts = uts;
	}
	
	/**
	 * Sets chosen project in session and load project home page</br>
	 * Kind of <b>Step 0</b> in the feedback process.
	 * 
	 * @param model
	 * @return home template to load
	 * @throws IOException 
	 */
	@RequestMapping(ApplicationPathHelper.URL_PROJECTHOME + "{projectName}")
	public String chooseProject(HttpSession session, ServletResponse response, Model model, @PathVariable("projectName") String projectName) throws IOException {
		
		HttpServletResponse res = (HttpServletResponse) response;

		if(projectService.exists(projectName)) {
			
			Project project = projectService.findProject(projectName);
			if(! projectService.isOnline(project)) {
				LOG.error("User tried to Start inactive project {}", projectName);
				res.sendError(404, String.format("Projekt %s läuft aktuell nicht.", projectName));
				return null;
			}
			session.setAttribute(SessionAttributeHelper.PROJECT, project);
			
			UiText uiText = uts.getUiText(project, UiTextKey.MSG_PROJECTHOME);
			mfs.fillUiText(model, uiText);
			mfs.fillGlobal(model);
			return ApplicationPathHelper.RES_PROJECTHOME;
		}
		
		res.sendError(404, String.format("Projekt %s wurde nicht gefunden.", projectName));
		return null;
	}
}
