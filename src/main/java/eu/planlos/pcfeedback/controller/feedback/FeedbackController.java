package eu.planlos.pcfeedback.controller.feedback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eu.planlos.pcfeedback.constants.ApplicationPaths;
import eu.planlos.pcfeedback.constants.ApplicationSessionAttributes;
import eu.planlos.pcfeedback.exceptions.InvalidFeedbackException;
import eu.planlos.pcfeedback.exceptions.NoFeedbackException;
import eu.planlos.pcfeedback.exceptions.ParticipantAlreadyExistingException;
import eu.planlos.pcfeedback.exceptions.RatingQuestionsNotExistentException;
import eu.planlos.pcfeedback.model.FeedbackDTO;
import eu.planlos.pcfeedback.model.Gender;
import eu.planlos.pcfeedback.model.UiTextKey;
import eu.planlos.pcfeedback.model.db.Participant;
import eu.planlos.pcfeedback.model.db.ParticipationResult;
import eu.planlos.pcfeedback.model.db.Project;
import eu.planlos.pcfeedback.model.db.RatingQuestion;
import eu.planlos.pcfeedback.model.db.UiText;
import eu.planlos.pcfeedback.service.MailService;
import eu.planlos.pcfeedback.service.ModelFillerService;
import eu.planlos.pcfeedback.service.ParticipantService;
import eu.planlos.pcfeedback.service.ParticipationResultService;
import eu.planlos.pcfeedback.service.RatingQuestionService;
import eu.planlos.pcfeedback.service.UiTextService;


@Slf4j
@Controller
@org.springframework.web.bind.annotation.SessionAttributes(names = {ApplicationSessionAttributes.PARTICIPANT, ApplicationSessionAttributes.PROJECT, ApplicationSessionAttributes.FEEDBACK})
public class FeedbackController {

	private static final String ERROR_TEMPLATE = "feedback_error";
	
	private static final int FREETEXTMAXLENGTH = 2000;

	private final ModelFillerService mfs;
	private final RatingQuestionService ratingQuestionService;
	private final ParticipantService participantService;
	private final ParticipationResultService participationResultService;
	private final MailService mailService;
	private final UiTextService uts;

	public FeedbackController(ModelFillerService mfs, RatingQuestionService	ratingQuestionService,
			ParticipantService participantService, ParticipationResultService participationResultService, MailService mailService,
			UiTextService uts) {
		this.mfs = mfs;
		this.ratingQuestionService = ratingQuestionService;
		this.participantService = participantService;
		this.participationResultService = participationResultService;
		this.mailService = mailService;
		this.uts = uts;
	}
	
	/**
	 * User is redirected to this controller after successfully writing participant info to session.
	 * 
	 * @param model
	 * @param project
	 * @param participant
	 * @return template to load
	 */
	@RequestMapping(path = ApplicationPaths.URL_FEEDBACK_QUESTION)
	public String feedback(Model model,
			@ModelAttribute(ApplicationSessionAttributes.PROJECT) Project project,
			@ModelAttribute(ApplicationSessionAttributes.PARTICIPANT) Participant participant) {
		
		//TODO ParticipantFilter?
		if(participant == null) {
			log.debug("User tried to access feedback without entering participant details");
			return "redirect:" + ApplicationPaths.URL_FEEDBACK_START;
		}
		
		log.debug("Participant: {}", participant.toString());
		
		Gender gender = participant.getGender();
		List<RatingQuestion> ratingQuestionList = new ArrayList<>();		
		try {
			ratingQuestionService.addRatingQuestionsForProjectAndGenderToList(ratingQuestionList, project, gender);
			
		} catch (RatingQuestionsNotExistentException e) {
			log.error("FATAL - COULD NOT LOAD LIST OF MATCHING RatingQuestion. This should never happen.");
			log.error("FATAL - project={}, gender={}", project.getProjectName(), gender.name());
		} 
				
		model.addAttribute("ratingQuestionList", ratingQuestionList);

		UiText uiText = uts.getUiText(project, UiTextKey.MSG_FEEDBACK_QUESTION);
		
		mfs.fillUiText(model, uiText);
		mfs.fillGlobal(model);
		return ApplicationPaths.RES_FEEDBACK_QUESTION;
	}
	
	/**
	 * 
	 * @param model
	 * @param session
	 * @param fbc
	 * @param project
	 * @param participant
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(path = ApplicationPaths.URL_FEEDBACK_QUESTION_SUBMIT, method = RequestMethod.POST)
	public String questionSubmit(Model model,
			ServletResponse response,
			HttpSession session,
			@ModelAttribute FeedbackDTO fbc,
			@ModelAttribute(ApplicationSessionAttributes.PROJECT) Project project,
			@ModelAttribute(ApplicationSessionAttributes.PARTICIPANT) Participant participant) throws IOException {

		String resource = ApplicationPaths.RES_FEEDBACK_FREETEXT;
		
		try {
			fbc.validate(project.getRatingQuestionCount());
		
			log.debug("Adding feedback to session");
			session.setAttribute(ApplicationSessionAttributes.FEEDBACK, fbc);
						
			if(! project.isAskFreetext()) {
				HttpServletResponse res = (HttpServletResponse) response;
				res.sendRedirect(ApplicationPaths.URL_FEEDBACK_RESULT_SUBMIT);
				//TODO must it be like this? Looks stupid
				return null;
			}
					
		} catch (NoFeedbackException | InvalidFeedbackException e) {

			try {
				Map<Long, Integer> feedbackMap = fbc.getFeedbackMap();
				List<RatingQuestion> ratingQuestionList = new ArrayList<>();
				ratingQuestionList.addAll(ratingQuestionService.reloadForInvalidFeedback(project, participant.getGender(), feedbackMap));
				model.addAttribute("ratingQuestionList", ratingQuestionList);
			
				model.addAttribute("feedbackError", e.getMessage());
				model.addAttribute("chosenList", feedbackMap);
				
				UiText uiText = uts.getUiText(project, UiTextKey.MSG_FEEDBACK_QUESTION);
				mfs.fillUiText(model, uiText);
				
				resource = ApplicationPaths.RES_FEEDBACK_QUESTION;
				
			} catch (RatingQuestionsNotExistentException | InvalidFeedbackException f) {
				log.debug("{}", f.toString());
				resource = ERROR_TEMPLATE;
			}
			
		}
		model.addAttribute("freeTextMaxLength", FREETEXTMAXLENGTH);
		UiText uiText = uts.getUiText(project, UiTextKey.MSG_FEEDBACK_FREETEXT);
		mfs.fillUiText(model, uiText);
		mfs.fillGlobal(model);		
		return resource;
	}
	
	/**
	 * Method which saves all results. Takes participant and feedback from session
	 * @param model
	 * @param userAgent
	 * @param project
	 * @param participant
	 * @param fbContainer
	 * @param freeText
	 * @return
	 */
	@RequestMapping(path = ApplicationPaths.URL_FEEDBACK_RESULT_SUBMIT, method = RequestMethod.POST)
	public String resultSubmit(Model model,
			@RequestHeader("User-Agent") String userAgent,
			@ModelAttribute(ApplicationSessionAttributes.PROJECT) Project project,
			@ModelAttribute(ApplicationSessionAttributes.PARTICIPANT) Participant participant,
			@ModelAttribute(ApplicationSessionAttributes.FEEDBACK) FeedbackDTO fbContainer,
			@RequestParam String freeText) {
		
		participant.setUserAgent(userAgent);
		
		return processEndResult(model, freeText, project, participant, fbContainer);
	}
	
	/**
	 * Method which saves all results. Takes participant and feedback from session
	 * @param model
	 * @param userAgent
	 * @param project
	 * @param participant
	 * @param fbContainer
	 * @return
	 */
	@RequestMapping(path = ApplicationPaths.URL_FEEDBACK_RESULT_SUBMIT, method = RequestMethod.GET)
	public String resultSubmit(Model model,
			@RequestHeader("User-Agent") String userAgent,
			@ModelAttribute(ApplicationSessionAttributes.PROJECT) Project project,
			@ModelAttribute(ApplicationSessionAttributes.PARTICIPANT) Participant participant,
			@ModelAttribute(ApplicationSessionAttributes.FEEDBACK) FeedbackDTO fbContainer) {
		
		participant.setUserAgent(userAgent);
		
		return processEndResult(model, null, project, participant, fbContainer);
	}

	private String processEndResult(Model model, String freeText, Project project,
			Participant participant, FeedbackDTO fbContainer) {
		Map<Long, Integer> feedbackMap = fbContainer.getFeedbackMap();
				
		String resource = "redirect:" + ApplicationPaths.URL_FEEDBACK_END;
		
		try {
			
			finalValidation(project, participant, feedbackMap);
			
			//Save participant first, might not complete
			participantService.save(participant);
			ratingQuestionService.saveFeedback(feedbackMap);
			
			//Save the result for later plausibilisation/correction
			ParticipationResult participationResult = new ParticipationResult(project, participant, feedbackMap, freeText);
			participationResultService.saveParticipationResult(participationResult);
			
			//Send notification mail
			String recepient = project.getNotificationMail();
			if(recepient != null && ! recepient.equals("")) {
				log.debug("Sending notification mail");
				mailService.notifyParticipation(project);
			} else {
				log.debug("Not sending notification mail");
			}
			
		} catch (ParticipantAlreadyExistingException e) {
			log.error("This should not happen, because session is destroyed on submitting feedback");
			resource = ERROR_TEMPLATE;
			
		} catch (InvalidFeedbackException e) {
			log.error("Project is not in all objects the same: project, participant, etc.");
			resource = ERROR_TEMPLATE;
			
		} finally {
			if(resource.equals(ERROR_TEMPLATE)) {
				mfs.fillGlobal(model);
			}
		}
		return resource;
	}

	private void finalValidation(Project project, Participant participant, Map<Long, Integer> feedbackMap) throws InvalidFeedbackException {

		int mapEntryCount = feedbackMap.size();
		
		if(mapEntryCount < project.getRatingQuestionCount()) {
			throw new InvalidFeedbackException("Im Feedback ist nicht die erwartete Anzahl Paare enthalten.");
		}
		
		long project1 = project.getIdProject();
		long project2 = participant.getProject().getIdProject();
		
		if(project1 != project2) {
			throw new InvalidFeedbackException("Projekt in folgenden Objekten nicht identisch: Projekt, Teilnehmer.");
		}

		if(mapEntryCount == 0) {
			log.debug("No rating questions have been submitted");
			return;
		}
		
		long idRatingQuestion = feedbackMap.keySet().iterator().next();
		RatingQuestion ratingQuestion = ratingQuestionService.findByIdRatingQuestion(idRatingQuestion);
		
		long project3 = ratingQuestion.getProject().getIdProject();
		
		if(project1 != project3) {
			throw new InvalidFeedbackException("Projekt in folgenden Objekten nicht identisch: Projekt, Frage.");
		}		
	}
}
