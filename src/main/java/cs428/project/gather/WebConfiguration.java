package cs428.project.gather;

import cs428.project.gather.utilities.ActorInterceptor;

import java.util.*;
import org.h2.server.web.WebServlet;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.*;


@Configuration
public class WebConfiguration extends WebMvcConfigurerAdapter {
	@Bean
	ServletRegistrationBean h2servletRegistration() {
		ServletRegistrationBean registrationBean = new ServletRegistrationBean(new WebServlet());
		Map<String, String> params = new HashMap<String, String>();
		params.put("webAllowOthers", "");
		registrationBean.addUrlMappings("/console/*");
		registrationBean.setInitParameters(params);
		return registrationBean;
	}

	@Bean
	public ActorInterceptor actorInterceptor() {
		return new ActorInterceptor();
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		ActorInterceptor actorInterceptor = actorInterceptor();
		registry.addInterceptor(actorInterceptor);
	}
}
