package cs428.project.gather;

import java.sql.Timestamp;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import cs428.project.gather.model.*;

@Component
public class DatabaseLoader implements CommandLineRunner {

	private final RegisteredRepository repository;
	private final EventRepository eventRepo;

	@Autowired
	public DatabaseLoader(RegisteredRepository repository, EventRepository eventRepo) {
		this.repository = repository;
		this.eventRepo = eventRepo;
	}

	@Override
	public void run(String... strings) throws Exception {
		this.repository.save(new Registered("Frodo", "Baggins", 24, "Los Angeles"));
		Event testEvent = new Event("Test Event");
		Occurrence occur=new Occurrence("Single Occurrence",new Timestamp(Calendar.getInstance().getTime().getTime()));
		testEvent.add(occur);
		this.eventRepo.save(testEvent);
	}
}