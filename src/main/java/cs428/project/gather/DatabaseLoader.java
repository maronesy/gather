package cs428.project.gather;

import java.sql.Timestamp;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import cs428.project.gather.data.model.*;

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
//		Event testEvent = new Event("Test Event");
//		Occurrence occur=new Occurrence("Single Occurrence",new Timestamp(Calendar.getInstance().getTime().getTime()));
//		testEvent.addOccurrence(occur);
//		this.repository.save(testEvent);
		
		Event testEvent = new Event("Test Event");
		
		Location location = new Location("Test Location");
		location.setCity("Los Angeles");
		location.setState("CA");
		location.setLatitude(34.0498);
		location.setLongtitude(-118.2498);
		location.setStreetAddr("6542 Nowhere Blvd");
		location.setZipCode("90005");
		this.locationRepo.save(location);
		
		Occurrence occur=new Occurrence("Single Occurrence",new Timestamp(Calendar.getInstance().getTime().getTime()));
		occur.setLocation(location);
		
		testEvent.addOccurrence(occur);
		this.eventRepo.save(testEvent);
	}
}