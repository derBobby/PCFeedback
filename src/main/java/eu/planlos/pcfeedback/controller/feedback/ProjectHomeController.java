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
import eu.planlos.pcfeedback.model.Project;
import eu.planlos.pcfeedback.model.UiTextKey;
import eu.planlos.pcfeedback.service.ModelFillerService;
import eu.planlos.pcfeedback.service.ProjectService;

@Controller
public class ProjectHomeController {

	private static final Logger LOG = LoggerFactory.getLogger(ProjectHomeController.class);

	@Autowired
	private ModelFillerService mfs;
	
	@Autowired
	private ProjectService ps;
	
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

		if(ps.exists(projectName)) {
			
			Project project = ps.findProject(projectName);
			if(! ps.isOnline(project)) {
				LOG.error("User tried to Start inactive project {}", projectName);
				res.sendError(404, String.format("Projekt %s läuft aktuell nicht.", projectName));
				return null;
			}
			session.setAttribute(SessionAttributeHelper.PROJECT, project);
			
			mfs.fillUiText(model, project, UiTextKey.MSG_PROJECTHOME);
			mfs.fillGlobal(model);
			return ApplicationPathHelper.RES_PROJECTHOME;
		}
		
		res.sendError(404, String.format("Projekt %s wurde nicht gefunden.", projectName));
		return null;
	}
}
