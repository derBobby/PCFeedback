package eu.planlos.pcfeedback.controller;

import static eu.planlos.pcfeedback.constants.ApplicationPath.RES_ERROR;

import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import eu.planlos.pcfeedback.constants.ApplicationPath;
import eu.planlos.pcfeedback.service.ModelFillerService;

@Controller
public class CustomErrorController implements ErrorController {

	private static final Logger logger = LoggerFactory.getLogger(CustomErrorController.class);

	@Autowired
	private ErrorAttributes errorAttributes;
	
	@Autowired
	private ModelFillerService mfs;	
	
//	@Autowired
//	private MailService errorMailNotificationService;
    	
	//TODO NEW prevent stacktrace from being written to log
	@RequestMapping(path = ApplicationPath.URL_ERROR)
	public String handleError(HttpServletRequest request, Authentication auth, WebRequest webRequest, Model model) throws Exception {
	
        String errorMessage = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Exception errorException = (Exception) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        String requestedSite = request.getRequestURI();

        // Get error stack trace map object
        Map<String, Object> body = errorAttributes.getErrorAttributes(webRequest, true);
        // Extract stack trace string
        String errorTrace = (String) body.get("trace");
		
		String errorTitle = "Unbekannter Fehler";
	    Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
	    
	    
	    if (status != null) {
	    	
	        Integer statusCode = Integer.valueOf(status.toString());
	        
        	if(statusCode == HttpStatus.UNAUTHORIZED.value()) {
	        	errorTitle = "Fehlende Authentifizierung";
	        	logger.error("User was not authenticated when requesting site: " + requestedSite);
	        }
	        
	        if(statusCode == HttpStatus.FORBIDDEN.value()) {
	        	errorTitle = "Zugriff verboten";
	        	logger.error("User was not authorized for requested site: " + requestedSite);
	    		if(auth != null) {
	    			logger.error("User was " + auth.getName() + "");
	    		}
	        }
	        
	        if(statusCode == HttpStatus.NOT_FOUND.value()) {
	        	errorTitle = "Seite existiert nicht";
	        	logger.error("Requested site does not exist: " + requestedSite);
	        }
	        
	        if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
	        	errorTitle = "Fehler im Server";
	        	logger.error("Requested site produced internal server error: " + requestedSite);
	        }
		    
		    // Send email notification
		    //	    errorMailNotificationService.sendErrorNotification(title, errorMessage, errorException, errorTrace);

	    	mfs.fillError(model, statusCode, errorTitle, errorMessage, errorException, errorTrace, true);
	    	mfs.fillGlobal(model);
			return RES_ERROR;
	    }
	    
	    logger.error("~~~ We should not have gotten here ¯\\_(ツ)_/¯ ~~~");
	    throw new Exception("We should not have gotten here ¯\\_(ツ)_/¯");
	}

	@Override
	public String getErrorPath() {
		return ApplicationPath.URL_ERROR;
	}
}
