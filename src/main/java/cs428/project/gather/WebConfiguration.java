package cs428.project.gather;

import java.util.HashMap;
import java.util.Map;

import org.h2.server.web.WebServlet;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import cs428.project.gather.utilities.ActorInterceptor;

 
@Configuration
public class WebConfiguration extends WebMvcConfigurerAdapter {
    @Bean
    ServletRegistrationBean h2servletRegistration(){
    	ServletRegistrationBean registrationBean = new ServletRegistrationBean( new WebServlet());
    	Map<String,String> params = new HashMap<String,String>();
    	params.put("webAllowOthers","");
    	registrationBean.addUrlMappings("/console/*");
    	registrationBean.setInitParameters(params);
        return registrationBean;
    }
    

	@Bean
	public ActorInterceptor actorInterceptor()
	{
		ActorInterceptor actorInterceptor = new ActorInterceptor();

		return actorInterceptor;
	}
	@Override
	public void addInterceptors(InterceptorRegistry registry)
	{
		ActorInterceptor actorInterceptor = actorInterceptor();
		registry.addInterceptor(actorInterceptor);

//		BasePathInterceptor basePathInterceptor = basePathInterceptor();
//		registry.addInterceptor(basePathInterceptor);
	}
    
}
