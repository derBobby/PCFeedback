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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.planlos.pcfeedback.constants.ApplicationPathHelper;
import eu.planlos.pcfeedback.constants.SessionAttributeHelper;
import eu.planlos.pcfeedback.model.Project;
import eu.planlos.pcfeedback.service.ProjectService;

public class RequestResponseProjectFilter implements Filter {

	private static final Logger LOG = LoggerFactory.getLogger(RequestResponseProjectFilter.class);
	
	private ProjectService ps;
	
	public RequestResponseProjectFilter(ProjectService ps) {
		this.ps = ps;
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
		Project project = (Project) session.getAttribute(SessionAttributeHelper.PROJECT);
		
		// Check if session has project
		if(project == null) {
			LOG.error("No project saved in session -> redirecting do start page");
			res.sendRedirect(ApplicationPathHelper.URL_HOME);
		} else {
			String projectName = project.getProjectName();
			if(! ps.exists(projectName)) {
				LOG.error("Project name='{}' saved in session does not exist -> sending 400", projectName);
				res.sendError(404, String.format("Projekt %s wurde nicht gefunden.", projectName));

			} else {
				LOG.debug("Valid project saved in session: project={}", projectName);
				chain.doFilter(request, response);
			}
		}
	}
}