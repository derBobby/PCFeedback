package eu.planlos.pcfeedback.auth.basic.service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * Will be used from Spring Boot if user is authenticated but not authorised.
 * Method call is triggered/configured in @see SecurityConfiguration
 * @author derBobby
 *
 */
@Slf4j
@Component
public class LoginAccessDeniedHandler implements AccessDeniedHandler {

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException exception) throws IOException, ServletException {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(auth == null) {
			log.error("Keine Authentication geliefert bei Zugriff auf {}", request.getRequestURI());
		} else {
			log.error("Fehlgeschlagener Zugriff von {} auf {}", auth.getName(), request.getRequestURI());
		}
		response.sendError(HttpServletResponse.SC_FORBIDDEN);
	}
}
