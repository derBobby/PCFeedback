package eu.planlos.pcfeedback.controller.admin;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.planlos.pcfeedback.constants.ApplicationPathHelper;
import eu.planlos.pcfeedback.model.db.Participant;
import eu.planlos.pcfeedback.model.db.Project;
import eu.planlos.pcfeedback.service.ModelFillerService;
import eu.planlos.pcfeedback.service.ParticipantService;
import eu.planlos.pcfeedback.service.ProjectService;

@Controller
public class UserAgentController {

	private static final Logger LOG = LoggerFactory.getLogger(UserAgentController.class);
	
	private ModelFillerService mfs;
	private ProjectService projectService;
	private ParticipantService participantService;
	
	@Autowired
	public UserAgentController(ProjectService projectService, ParticipantService participantService, ModelFillerService mfs) {
		this.mfs = mfs;
		this.projectService = projectService;
		this.participantService = participantService;
	}
	
	@RequestMapping(path = ApplicationPathHelper.URL_ADMIN_SHOWUSERAGENTS + "{idProject}", method = RequestMethod.GET)
	public String showUserAgents(Model model, @PathVariable("idProject") Long idProject) {

		//TODO test idProject
		
		LOG.debug("Getting project from id");
		Project project = projectService.findProject(idProject);
		
		LOG.debug("Loading participants");
		List<Participant> participantList = participantService.getAllParticipantsForProject(project);
				
		model.addAttribute("participantList", participantList);

		mfs.fillGlobal(model);
		
		return ApplicationPathHelper.RES_ADMIN_SHOWUSERAGENTS;
	}
}
