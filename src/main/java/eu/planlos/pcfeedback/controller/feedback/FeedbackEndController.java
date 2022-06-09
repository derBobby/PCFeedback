package eu.planlos.pcfeedback.controller.feedback;

import eu.planlos.pcfeedback.constants.ApplicationPaths;
import eu.planlos.pcfeedback.constants.ApplicationSessionAttributes;
import eu.planlos.pcfeedback.model.UiTextKey;
import eu.planlos.pcfeedback.model.db.Participant;
import eu.planlos.pcfeedback.model.db.Project;
import eu.planlos.pcfeedback.model.db.UiText;
import eu.planlos.pcfeedback.service.ModelFillerService;
import eu.planlos.pcfeedback.service.UiTextService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@SessionAttributes(names = {ApplicationSessionAttributes.PARTICIPANT, ApplicationSessionAttributes.PROJECT})
public class FeedbackEndController {

	private final ModelFillerService mfs;
	private final UiTextService uts;

	public FeedbackEndController(ModelFillerService mfs, UiTextService uts) {
		this.mfs = mfs;
		this.uts = uts;
	}
	
	/**
	 * After feedback is saved user gets redirected to end page.
	 * Controller also clears participant from session.
	 * @param model
	 * @param session
	 * @param participant
	 * @param project
	 * @return
	 */
	@RequestMapping(ApplicationPaths.URL_FEEDBACK_END)
	public String end(Model model,
			HttpSession session,
			@ModelAttribute(ApplicationSessionAttributes.PARTICIPANT) Participant participant,
			@ModelAttribute(ApplicationSessionAttributes.PROJECT) Project project) {
		
		if(participant == null) {
			log.debug("User tried to access feedback end without entering participation info");
			return "redirect:" + ApplicationPaths.URL_FEEDBACK_START;
		}

		//TODO where else to set these?
		session.setAttribute(ApplicationSessionAttributes.FEEDBACK, null);
		session.setAttribute(ApplicationSessionAttributes.FREETEXT, null);
		session.setAttribute(ApplicationSessionAttributes.PARTICIPANT, null);
		session.setAttribute(ApplicationSessionAttributes.PROJECT, null);
		
		UiText uiText = uts.getUiText(project, UiTextKey.MSG_FEEDBACK_END);
		mfs.fillUiText(model, uiText);
		mfs.fillGlobal(model);
		model.addAttribute("projectName", project.getProjectName());
		return ApplicationPaths.RES_FEEDBACK_END;
	}
}
