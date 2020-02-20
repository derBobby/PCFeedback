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

	private static final Logger LOG = LoggerFactory.getLogger(CustomErrorController.class);

	@Autowired
	private ErrorAttributes errorAttributes;
	
	@Autowired
	private ModelFillerService mfs;	
	
//	@Autowired
//	private MailService errorMailNotificationService;
    
	/**
	 * Is called whenever an error is thrown during web access
	 * @param request automatically provided
	 * @param auth automatically provided
	 * @param webRequest automatically provided
	 * @param model automatically provided
	 * @return error template to load 
	 */
	@RequestMapping(path = ApplicationPath.URL_ERROR)
	public String handleError(HttpServletRequest request, Authentication auth, WebRequest webRequest, Model model) {
	
        String errorMessage = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Exception errorException = (Exception) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        String requestedSite = request.getRequestURI();

        // Get error stack trace map object
        Map<String, Object> body = errorAttributes.getErrorAttributes(webRequest, true);
        // Extract stack trace string
        String errorTrace = (String) body.get("trace");
		
		String errorTitle = "Unbekannter Fehler";
	    Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
	    
        Integer statusCode = status != null ? Integer.valueOf(status.toString()) : -1;
	    
    	if(statusCode == HttpStatus.UNAUTHORIZED.value()) {
        	errorTitle = "Fehlende Authentifizierung";
        	LOG.error("User was not authenticated when requesting site: {}", requestedSite);
        }
        
    	else if(statusCode == HttpStatus.FORBIDDEN.value()) {
        	errorTitle = "Zugriff verboten";
        	LOG.error("User was not authorized for requested site: {}", requestedSite);
    		if(auth != null) {
    			LOG.error("User was: {}", auth.getName());
    		}
        }
        
    	else if(statusCode == HttpStatus.NOT_FOUND.value()) {
        	errorTitle = "Seite existiert nicht";
        	LOG.error("Requested site does not exist: {}", requestedSite);
        }
        
    	else if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
        	errorTitle = "Fehler im Server";
        	LOG.error("Requested site produced internal server error: {}", requestedSite);
        	
        } else {
    	    errorTitle = "Unbekannter Fehler";
    	    LOG.error("~~~ We should not have gotten here ¯\\_(ツ)_/¯ ~~~");
        }
	    
	    // Send email notification
	    //errorMailNotificationService.sendErrorNotification(title, errorMessage, errorException, errorTrace); 
	    
    	mfs.fillError(model, statusCode, errorTitle, errorMessage, errorException, errorTrace, true);
    	mfs.fillGlobal(model);
		return RES_ERROR;
	}

	@Override
	public String getErrorPath() {
		return ApplicationPath.URL_ERROR;
	}
}
