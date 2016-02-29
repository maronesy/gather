package cs428.project.gather.config;

import java.util.HashMap;
import java.util.Map;

import org.h2.server.web.WebServlet;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
 
@Configuration
public class WebConfiguration {
    @Bean
    ServletRegistrationBean h2servletRegistration(){
    	ServletRegistrationBean registrationBean = new ServletRegistrationBean( new WebServlet());
    	Map<String,String> params = new HashMap<String,String>();
    	params.put("webAllowOthers","");
    	registrationBean.addUrlMappings("/console/*");
    	registrationBean.setInitParameters(params);
        return registrationBean;
    }
}
