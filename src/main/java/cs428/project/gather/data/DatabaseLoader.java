package cs428.project.gather.data;

import java.sql.Timestamp;
import java.util.Calendar;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import cs428.project.gather.data.model.Event;
import cs428.project.gather.data.model.Location;
import cs428.project.gather.data.model.Occurrence;
import cs428.project.gather.data.model.Registered;
import cs428.project.gather.data.model.Registrant;
import cs428.project.gather.data.repo.EventRepository;
import cs428.project.gather.data.repo.LocationRepository;
import cs428.project.gather.data.repo.RegisteredRepository;
import cs428.project.gather.data.repo.RegistrantRepository;

@Component
public class DatabaseLoader implements CommandLineRunner {

	private final RegisteredRepository registeredRepo;
	private final EventRepository eventRepo;
//	private final LocationRepository locationRepo;
	private final RegistrantRepository registrantRepo;

	@Autowired
	public DatabaseLoader(RegisteredRepository repository, EventRepository eventRepo, LocationRepository locationRepo, RegistrantRepository registrantRepo) {
		this.registeredRepo = repository;
		this.eventRepo = eventRepo;
//		this.locationRepo = locationRepo;
		this.registrantRepo = registrantRepo;
	}

	@Override
	public void run(String... strings) throws Exception {
		this.registeredRepo.save(new Registered("Frodo", "Baggins", 24, "Los Angeles"));
		
		Registrant aUser = new Registrant("testuser@email.com","password","testDisplayName",10L,3,10000);
		Registrant registrantResult = this.registrantRepo.save(aUser);
		
		Event testEvent = new Event("Test Event");
		Location location = new Location("Test Location", "6542 Nowhere Blvd", "Los Angeles", "CA", "90005", 34.0498, -118.2498);
//		this.locationRepo.save(location);
		Occurrence occur=new Occurrence("Test Occurrence",new Timestamp(DateTime.now().getMillis()));
		testEvent.addOccurrence(occur);
		Event eventResult = this.eventRepo.save(testEvent);
		
		//Right now, Event owns all relationships, so Event must be saved for data to be put in DB.
		testEvent.addParticipant(aUser);
		//It is recommended you also add the Event to the Registrant, so that the in memory state of the objects is consistent with the DB
		//We can make either function do the opposite add if we wish, to simplify usage elsewhere
		aUser.joinEvent(testEvent);
		this.eventRepo.save(testEvent);
		
		Event newEvent = new Event("Test1");
		Location newLoc = new Location("Test Location", "6000 Yeswhere Blvd", "Los Angeles", "CA", "90007", 32.770, -117.04);

		newEvent.setLocation(newLoc);
		Occurrence newOccur = new Occurrence("First", new Timestamp(DateTime.now().plusDays(1).getMillis()));
		newEvent.addOccurrence(newOccur);
		newEvent.setDescription("lets play soccer!");
		newEvent.setCategory("Soccer");
		this.eventRepo.save(newEvent);
		
		Event newEvent2 = new Event("Test2");
		Location newLoc2 = new Location("Test Location", "6542 Nowhere Blvd", "Los Angeles", "CA", "90005", 32.780, -117.03);

		newEvent2.setLocation(newLoc2);
		Occurrence newOccur2 = new Occurrence("Second", new Timestamp(DateTime.now().plusDays(2).getMillis()));
		newEvent2.addOccurrence(newOccur2);	
		Occurrence newOccur3 = new Occurrence("Second2", new Timestamp(DateTime.now().plusDays(5).getMillis()));
		newEvent2.addOccurrence(newOccur3);	
		newEvent2.setDescription("lets swim!");
		newEvent2.setCategory("Swim");
		this.eventRepo.save(newEvent2);
	}
}