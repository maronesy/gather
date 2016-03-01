package cs428.project.gather.data;

import java.sql.Timestamp;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import cs428.project.gather.data.model.Event;
import cs428.project.gather.data.model.EventRepository;
import cs428.project.gather.data.model.Location;
import cs428.project.gather.data.model.LocationRepository;
import cs428.project.gather.data.model.Occurrence;
import cs428.project.gather.data.model.Registered;
import cs428.project.gather.data.model.RegisteredRepository;

@Component
public class DatabaseLoader implements CommandLineRunner {

	private final RegisteredRepository registeredRepo;
	private final EventRepository eventRepo;
	private final LocationRepository locationRepo;

	@Autowired
	public DatabaseLoader(RegisteredRepository repository, EventRepository eventRepo, LocationRepository locationRepo) {
		this.registeredRepo = repository;
		this.eventRepo = eventRepo;
		this.locationRepo = locationRepo;
	}

	@Override
	public void run(String... strings) throws Exception {
		this.registeredRepo.save(new Registered("Frodo", "Baggins", 24, "Los Angeles"));

		Event testEvent = new Event("Test Event");
		Location location = new Location("Test Location", "6542 Nowhere Blvd", "Los Angeles", "CA", "90005", 34.0498, -118.2498);
		this.locationRepo.save(location);
		Occurrence occur=new Occurrence("Test Occurrence",new Timestamp(Calendar.getInstance().getTime().getTime()), location);
		testEvent.addOccurrence(occur);
		Event result = this.eventRepo.save(testEvent);
		assert(result.getDescription().isEmpty()==false);
	}
}