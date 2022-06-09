package eu.planlos.pcfeedback.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.planlos.pcfeedback.constants.ApplicationPaths;
import eu.planlos.pcfeedback.constants.ApplicationSessionAttributes;
import eu.planlos.pcfeedback.model.db.Project;
import eu.planlos.pcfeedback.service.ProjectService;

@Slf4j
public class RequestResponseProjectFilter implements Filter {

	private final ProjectService projectService;
	
	public RequestResponseProjectFilter(ProjectService projectService) {
		this.projectService = projectService;
	}
	
	/**
	 *  Filter reads project from session.</br>
	 *  If it is empty it will redirect to Home.
	 *  If it is not empty but not existing it will send 400
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		
		HttpSession session = req.getSession();
		Project project = (Project) session.getAttribute(ApplicationSessionAttributes.PROJECT);
		
		// Check if session has project
		if(project == null) {
			log.error("No project saved in session -> redirecting do start page");
			res.sendRedirect(ApplicationPaths.URL_HOME);
		} else {
			String projectName = project.getProjectName();
			if(projectService.exists(projectName)) {
				log.debug("Valid project saved in session: project={}", projectName);
				chain.doFilter(request, response);
				
			} else {
				log.error("Project name='{}' saved in session does not exist -> sending 400", projectName);
				res.sendError(404, String.format("Projekt %s wurde nicht gefunden.", projectName));
			}
		}
	}
}