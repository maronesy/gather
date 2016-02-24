package cs428.project.gather;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import cs428.project.gather.model.*;

@Component
public class DatabaseLoader implements CommandLineRunner {

	private final RegisteredRepository repository;

	@Autowired
	public DatabaseLoader(RegisteredRepository repository) {
		this.repository = repository;
	}

	@Override
	public void run(String... strings) throws Exception {
		this.repository.save(new Registered("Frodo", "Baggins", 24, "Los Angeles"));
	}
}