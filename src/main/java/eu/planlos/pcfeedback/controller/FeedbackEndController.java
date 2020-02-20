package eu.planlos.pcfeedback.controller;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import eu.planlos.pcfeedback.constants.ApplicationPath;
import eu.planlos.pcfeedback.constants.SessionAttribute;
import eu.planlos.pcfeedback.model.Participant;
import eu.planlos.pcfeedback.model.UiTextKey;
import eu.planlos.pcfeedback.service.ModelFillerService;

@Controller
public class FeedbackEndController {

	private static final Logger LOG = LoggerFactory.getLogger(FeedbackEndController.class);
	
	@Autowired
	private ModelFillerService mfs;
	
	/**
	 * After feedback is saved user gets redirected to end page.
	 * Controller also clears participant from session.
	 * @param session provides participant details
	 * @param model
	 * @return template to load
	 */
	@RequestMapping(ApplicationPath.URL_FEEDBACK_END)
	public String end(HttpSession session, Model model) {

		Participant participant = (Participant) session.getAttribute(SessionAttribute.PARTICIPANT);
		
		if(participant == null) {
			LOG.debug("User tried to access feedback end without entering participation info");
			return "redirect:" + ApplicationPath.URL_FEEDBACK_START;
		}
		
		session.setAttribute(SessionAttribute.PARTICIPANT, null);
		
		mfs.fillUiText(model, UiTextKey.MSG_FEEDBACKEND);
		mfs.fillGlobal(model);
		return ApplicationPath.RES_FEEDBACK_END;
	}
}
