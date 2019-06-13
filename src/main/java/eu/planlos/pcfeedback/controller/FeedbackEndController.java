package eu.planlos.pcfeedback.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import eu.planlos.pcfeedback.constants.ApplicationPath;
import eu.planlos.pcfeedback.service.ModelFillerService;

@Controller
public class FeedbackEndController {

	@Autowired
	private ModelFillerService mfs;
	
	@RequestMapping(ApplicationPath.URL_FEEDBACK_END)
	public String end(Model model) {
			
		mfs.fillGlobal(model);
		
		return ApplicationPath.RES_FEEDBACK_END;
	}
}
