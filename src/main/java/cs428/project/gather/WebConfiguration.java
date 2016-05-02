package cs428.project.gather;

import cs428.project.gather.utilities.ActorInterceptor;

import java.util.*;
import org.h2.server.web.WebServlet;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.*;

/**
 * 
 * @author Team Gather
 * The web configuration for Spring boot framework
 * 
 */
@Configuration
public class WebConfiguration extends WebMvcConfigurerAdapter {
	
	/**
	 * set up the H2 database servlet and configure how to access 
	 * the databse through web.
	 *
	 * @return servelt registration bean
	 */
	@Bean
	ServletRegistrationBean h2servletRegistration() {
		ServletRegistrationBean registrationBean = new ServletRegistrationBean(new WebServlet());
		Map<String, String> params = new HashMap<String, String>();
		params.put("webAllowOthers", "");
		registrationBean.addUrlMappings("/console/*");
		registrationBean.setInitParameters(params);
		return registrationBean;
	}

	/**
	 * Returns a new Actor interceptor which contains the user's session 
	 * information.
	 *
	 * @return actor interceptor
	 */
	@Bean
	public ActorInterceptor actorInterceptor() {
		return new ActorInterceptor();
	}

	/**
	 * Added the actor interceptor to registry
	 *
	 * @param registry the registry to add the actor interceptor
	 * 
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		ActorInterceptor actorInterceptor = actorInterceptor();
		registry.addInterceptor(actorInterceptor);
	}
}
