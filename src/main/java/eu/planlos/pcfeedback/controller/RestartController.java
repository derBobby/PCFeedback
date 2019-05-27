package eu.planlos.pcfeedback.controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import eu.planlos.pcfeedback.constants.ApplicationPath;

@Controller
public class RestartController {

	@RequestMapping(path = ApplicationPath.URL_RESTART)
	public String restartFeedback(HttpSession session) {
		
		session.invalidate();
		return "redirect:" + ApplicationPath.URL_HOME;
	}
}
