package cs428.project.gather;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableAutoConfiguration(exclude = {
org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration.class,
org.springframework.boot.actuate.autoconfigure.ManagementWebSecurityAutoConfiguration.class
})

@SpringBootApplication
public class GatherApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatherApplication.class, args);
	}
}
