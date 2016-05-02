package cs428.project.gather;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;


/**
 * 
 * @author Team Gather
 * The class for program entry point
 * 
 */
@EnableAutoConfiguration(exclude = { org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration.class,
		org.springframework.boot.actuate.autoconfigure.ManagementWebSecurityAutoConfiguration.class })

@SpringBootApplication
public class GatherApplication extends SpringBootServletInitializer {
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(GatherApplication.class);
	}

	/**
	 * main() function for program entry point
	 *
	 * @param args main() arguments.
	 */
	public static void main(String[] args) {
		SpringApplication.run(GatherApplication.class, args);
	}
}
