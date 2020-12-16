package eu.planlos.pcfeedback.controller.feedback;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import eu.planlos.pcfeedback.constants.ApplicationPathHelper;
import eu.planlos.pcfeedback.constants.SessionAttributeHelper;
import eu.planlos.pcfeedback.model.UiTextKey;
import eu.planlos.pcfeedback.model.db.Participant;
import eu.planlos.pcfeedback.model.db.Project;
import eu.planlos.pcfeedback.model.db.UiText;
import eu.planlos.pcfeedback.service.ModelFillerService;
import eu.planlos.pcfeedback.service.UiTextService;

@Controller
@SessionAttributes(names = {SessionAttributeHelper.PARTICIPANT, SessionAttributeHelper.PROJECT})
public class FeedbackEndController {

	private static final Logger LOG = LoggerFactory.getLogger(FeedbackEndController.class);
	
	@Autowired
	private ModelFillerService mfs;
	
	@Autowired
	private UiTextService uts;
	
	/**
	 * After feedback is saved user gets redirected to end page.
	 * Controller also clears participant from session.
	 * @param model
	 * @param session
	 * @param participant
	 * @param project
	 * @return
	 */
	@RequestMapping(ApplicationPathHelper.URL_FEEDBACK_END)
	public String end(Model model,
			HttpSession session,
			@ModelAttribute(SessionAttributeHelper.PARTICIPANT) Participant participant,
			@ModelAttribute(SessionAttributeHelper.PROJECT) Project project) {
		
		if(participant == null) {
			LOG.debug("User tried to access feedback end without entering participation info");
			return "redirect:" + ApplicationPathHelper.URL_FEEDBACK_START;
		}

		//TODO where else to set these?
		session.setAttribute(SessionAttributeHelper.FEEDBACK, null);
		session.setAttribute(SessionAttributeHelper.FREETEXT, null);
		session.setAttribute(SessionAttributeHelper.PARTICIPANT, null);
		session.setAttribute(SessionAttributeHelper.PROJECT, null);
		
		UiText uiText = uts.getUiText(project, UiTextKey.MSG_FEEDBACK_END);
		mfs.fillUiText(model, uiText);
		mfs.fillGlobal(model);
		model.addAttribute("projectName", project.getProjectName());
		return ApplicationPathHelper.RES_FEEDBACK_END;
	}
}
