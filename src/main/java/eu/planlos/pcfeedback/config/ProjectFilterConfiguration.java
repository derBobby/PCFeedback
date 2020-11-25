package eu.planlos.pcfeedback.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import eu.planlos.pcfeedback.constants.ApplicationPathHelper;
import eu.planlos.pcfeedback.filter.RequestResponseProjectFilter;
import eu.planlos.pcfeedback.service.ProjectService;

/**
 * Class configures for which Controllers / URLs the {@link RequestResponseProjectFilter} will be enabled.
 */
@Configuration
public class ProjectFilterConfiguration {

	private static final Logger LOG = LoggerFactory.getLogger(ProjectFilterConfiguration.class);

	@Autowired
	private ProjectService ps;
	
	@Bean
	public FilterRegistrationBean<RequestResponseProjectFilter> projectFilter(){
	    FilterRegistrationBean<RequestResponseProjectFilter> registrationBean = new FilterRegistrationBean<>();
	       
	    LOG.debug("Setting up project filter");
	    
	    registrationBean.setFilter(new RequestResponseProjectFilter(ps));
	    registrationBean.addUrlPatterns(
	    		ApplicationPathHelper.URL_FEEDBACK_START,
	    		ApplicationPathHelper.URL_FEEDBACK_QUESTION,
	    		ApplicationPathHelper.URL_FEEDBACK_RESULT_SUBMIT,
	    		ApplicationPathHelper.URL_FEEDBACK_END,
	    		ApplicationPathHelper.URL_FEEDBACK_QUESTION_SUBMIT
	    	);
	         
	    return registrationBean;    
	}
}
