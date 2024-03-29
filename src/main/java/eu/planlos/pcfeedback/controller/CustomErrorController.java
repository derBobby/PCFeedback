package eu.planlos.pcfeedback.controller;

import static eu.planlos.pcfeedback.constants.ApplicationPaths.RES_ERROR;

import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import eu.planlos.pcfeedback.constants.ApplicationPaths;
import eu.planlos.pcfeedback.service.ModelFillerService;

@Slf4j
@Controller
public class CustomErrorController implements ErrorController {

	private final ErrorAttributes errorAttributes;
	private final ModelFillerService mfs;

	public CustomErrorController(ErrorAttributes errorAttributes, ModelFillerService mfs) {
		this.errorAttributes = errorAttributes;
		this.mfs = mfs;
	}

	/**
	 * Is called whenever an error is thrown during web access
	 * 
	 * @param request    automatically provided
	 * @param auth       automatically provided
	 * @param webRequest automatically provided
	 * @param model      automatically provided
	 * @return error template to load
	 */
	@RequestMapping(path = ApplicationPaths.URL_ERROR)
	public String handleError(HttpServletRequest request, Authentication auth, WebRequest webRequest, Model model) {

		String errorMessage = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
		Exception errorException = (Exception) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
		// TODO not the real url
		String requestedSite = request.getRequestURI();

		// Get error stack trace map object
		Map<String, Object> body = errorAttributes.getErrorAttributes(webRequest, ErrorAttributeOptions.defaults());
		// Extract stack trace string
		String errorTrace = (String) body.get("trace");

		String errorTitle;
		Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

		Integer statusCode = (status != null ? Integer.valueOf(status.toString()) : -1);

		if (statusCode == HttpStatus.UNAUTHORIZED.value()) {
			errorTitle = "Fehlende Authentifizierung";
			log.error("User was not authenticated when requesting site: {}", requestedSite);
		}

		else if (statusCode == HttpStatus.FORBIDDEN.value()) {
			errorTitle = "Zugriff verboten";
			log.error("User was not authorized for requested site: {}", requestedSite);
			if (auth != null) {
				log.error("User was: {}", auth.getName());
			}
		}

		else if (statusCode == HttpStatus.NOT_FOUND.value()) {
			errorTitle = "Ressource existiert nicht";
			log.error("Requested ressource does not exist. Message: {}", errorMessage);
		}

		else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
			errorTitle = "Fehler im Server";
			log.error("Requested site produced internal server error: {}", requestedSite);

		} else {
			errorTitle = "Unbekannter Fehler";
			log.error("~~~ We should not have gotten here ¯\\_(ツ)_/¯ ~~~");
		}

		mfs.fillError(model, statusCode, errorTitle, errorMessage, errorException, errorTrace, true);
		mfs.fillGlobal(model);
		return RES_ERROR;
	}

	@Override
	public String getErrorPath() {
		return ApplicationPaths.URL_ERROR;
	}
}
