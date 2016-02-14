package cs428.project.gather;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

@EnableAutoConfiguration(exclude = {
    org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration.class,
    org.springframework.boot.actuate.autoconfigure.ManagementWebSecurityAutoConfiguration.class
})

@SpringBootApplication
public class GatherApplication extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(GatherApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(GatherApplication.class, args);
    }
}
