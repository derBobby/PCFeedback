package eu.planlos.pcfeedback.config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import eu.planlos.pcfeedback.constants.ApplicationPaths;
import eu.planlos.pcfeedback.filter.RequestResponseProjectFilter;
import eu.planlos.pcfeedback.service.ProjectService;

import static eu.planlos.pcfeedback.util.csv.ICSVExporter.log;

/**
 * Class configures for which Controllers / URLs the {@link RequestResponseProjectFilter} will be enabled.
 */
@Slf4j
@Configuration
public class ProjectFilterConfiguration {

	private final ProjectService projectService;
	
	public ProjectFilterConfiguration(ProjectService projectService) {
		this.projectService = projectService;
	}
	
	@Bean
	public FilterRegistrationBean<RequestResponseProjectFilter> projectFilter(){
	    FilterRegistrationBean<RequestResponseProjectFilter> registrationBean = new FilterRegistrationBean<>();
	       
	    log.debug("Setting up project filter");
	    
	    registrationBean.setFilter(new RequestResponseProjectFilter(projectService));
	    registrationBean.addUrlPatterns(
	    		ApplicationPaths.URL_FEEDBACK_START,
	    		ApplicationPaths.URL_FEEDBACK_QUESTION,
	    		ApplicationPaths.URL_FEEDBACK_RESULT_SUBMIT,
	    		ApplicationPaths.URL_FEEDBACK_END,
	    		ApplicationPaths.URL_FEEDBACK_QUESTION_SUBMIT
	    	);
	    return registrationBean;    
	}
}
