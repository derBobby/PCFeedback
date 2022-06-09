package eu.planlos.pcfeedback.controller.admin;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.planlos.pcfeedback.constants.ApplicationPaths;
import eu.planlos.pcfeedback.model.db.Participant;
import eu.planlos.pcfeedback.model.db.Project;
import eu.planlos.pcfeedback.service.ModelFillerService;
import eu.planlos.pcfeedback.service.ParticipantService;
import eu.planlos.pcfeedback.service.ProjectService;

@Slf4j
@Controller
public class UserAgentController {

	private final ModelFillerService mfs;
	private final ProjectService projectService;
	private final ParticipantService participantService;
	
	public UserAgentController(ProjectService projectService, ParticipantService participantService, ModelFillerService mfs) {
		this.mfs = mfs;
		this.projectService = projectService;
		this.participantService = participantService;
	}
	
	@RequestMapping(path = ApplicationPaths.URL_ADMIN_SHOWUSERAGENTS + "{idProject}", method = RequestMethod.GET)
	public String showUserAgents(Model model, @PathVariable("idProject") Long idProject) {

		//TODO test idProject
		
		log.debug("Getting project from id");
		Project project = projectService.findProject(idProject);
		
		log.debug("Loading participants");
		List<Participant> participantList = participantService.getAllParticipantsForProject(project);
				
		model.addAttribute("participantList", participantList);

		mfs.fillGlobal(model);
		
		return ApplicationPaths.RES_ADMIN_SHOWUSERAGENTS;
	}
}
