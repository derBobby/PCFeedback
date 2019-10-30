package eu.planlos.pcfeedback.controller.admin;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.planlos.pcfeedback.constants.ApplicationPath;
import eu.planlos.pcfeedback.model.UserAgent;
import eu.planlos.pcfeedback.service.ModelFillerService;
import eu.planlos.pcfeedback.service.UserAgentService;

@Controller
public class ShowUserAgentController {

	private static final Logger logger = LoggerFactory.getLogger(ShowUserAgentController.class);
	
	@Autowired
	private ModelFillerService mfs;
	
	@Autowired
	private UserAgentService userAgentService;
	
	@RequestMapping(path = ApplicationPath.URL_ADMIN_SHOWUSERAGENTS, method = RequestMethod.GET)
	public String showUserAgents(Model model) {

		logger.debug("Loading User-Agents");
		List<UserAgent> userAgentList = userAgentService.findAll();
		
		model.addAttribute("userAgentList", userAgentList);

		mfs.fillGlobal(model);
		
		return ApplicationPath.RES_ADMIN_SHOWUSERAGENTS;
	}
}
