package eu.planlos.pcfeedback.controller;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import eu.planlos.pcfeedback.constants.ApplicationPathHelper;
import eu.planlos.pcfeedback.constants.SessionAttributeHelper;
import eu.planlos.pcfeedback.model.Participant;
import eu.planlos.pcfeedback.model.Project;
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
	@RequestMapping(ApplicationPathHelper.URL_FEEDBACK_END)
	public String end(HttpSession session, Model model) {

		Participant participant = (Participant) session.getAttribute(SessionAttributeHelper.PARTICIPANT);
		Project project = (Project) session.getAttribute(SessionAttributeHelper.PROJECT);
		
		if(participant == null) {
			LOG.debug("User tried to access feedback end without entering participation info");
			return "redirect:" + ApplicationPathHelper.URL_FEEDBACK_START;
		}

		//TODO where else to set these?
		session.setAttribute(SessionAttributeHelper.FEEDBACK, null);
		session.setAttribute(SessionAttributeHelper.FREETEXT, null);
		session.setAttribute(SessionAttributeHelper.PARTICIPANT, null);
		session.setAttribute(SessionAttributeHelper.PROJECT, null);
		
		mfs.fillUiText(model, project, UiTextKey.MSG_FEEDBACK_END);
		mfs.fillGlobal(model);
		return ApplicationPathHelper.RES_FEEDBACK_END;
	}
}
