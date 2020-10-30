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
import eu.planlos.pcfeedback.model.db.Project;
import eu.planlos.pcfeedback.model.db.UserAgent;
import eu.planlos.pcfeedback.service.ModelFillerService;
import eu.planlos.pcfeedback.service.ProjectService;
import eu.planlos.pcfeedback.service.UserAgentService;

@Controller
public class UserAgentController {

	private static final Logger LOG = LoggerFactory.getLogger(UserAgentController.class);
	
	@Autowired
	private ModelFillerService mfs;
	
	@Autowired
	private UserAgentService userAgentService;
	
	@Autowired
	private ProjectService pService;
	
	@RequestMapping(path = ApplicationPathHelper.URL_ADMIN_SHOWUSERAGENTS + "{projectName}", method = RequestMethod.GET)
	public String showUserAgents(Model model, @PathVariable("projectName") String projectName) {

		Project project = pService.findProject(projectName);
		
		LOG.debug("Loading User-Agents");
		List<UserAgent> userAgentList = userAgentService.findAllForProject(project);
		
		model.addAttribute("userAgentList", userAgentList);

		mfs.fillGlobal(model);
		
		return ApplicationPathHelper.RES_ADMIN_SHOWUSERAGENTS;
	}
}
