package cs428.project.gather.data.model;

//import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import cs428.project.gather.GatherApplication;
import cs428.project.gather.data.repo.EventRepository;
import cs428.project.gather.data.repo.LocationRepository;

import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(GatherApplication.class)
@WebAppConfiguration
@ActiveProfiles("scratch")
public class EventRepositoryIntegrationTest {
	@Autowired
	EventRepository eventRepo;
	
	@Autowired
	LocationRepository locationRepo;

	@Before
	public void setUp() {
		eventRepo.deleteAll();
		locationRepo.deleteAll();
	}
	
	@Test
	public void testSavesEventCorrectly() {

		Event testEvent = new Event("Test Event");
		Occurrence occur=new Occurrence("Single Occurrence",new Timestamp(Calendar.getInstance().getTime().getTime()));
		testEvent.addOccurrence(occur);
		Event result = this.eventRepo.save(testEvent);
		
		assertTrue(result.getDescription().equals("Test Event"));		
	}
	
	@Test
	public void testFindByDescription(){
		Event testEvent = new Event("Super Cool Test Event");
		Occurrence occur=new Occurrence("Single Occurrence",new Timestamp(Calendar.getInstance().getTime().getTime()));
		testEvent.addOccurrence(occur);
		this.eventRepo.save(testEvent);
			
		List<Event> foundEvents = eventRepo.findByDescription("Super Cool Test Event");
		
		assertEquals(foundEvents.size(), 1);
		assertTrue(foundEvents.get(0).getDescription().equals("Super Cool Test Event"));
	}
	
	@Test
	@Transactional
	public void testSaveLoadEventWithLocation(){
		Event testEvent = new Event("Test Event");
		Location location = new Location("Test Location", "6542 Nowhere Blvd", "Los Angeles", "CA", "90005", 34.0498, -118.2498);
		this.locationRepo.save(location);
		Occurrence occur=new Occurrence("Test Occurrence",new Timestamp(Calendar.getInstance().getTime().getTime()), location);
		testEvent.addOccurrence(occur);
		Event result = this.eventRepo.save(testEvent);
		
		Event foundEvent = this.eventRepo.findOne(result.getId());
		Set<Occurrence> occurrences = foundEvent.getOccurrences();
		assertEquals(occurrences.size(),1);
		
		Iterator<Occurrence> occurIt = occurrences.iterator();
		Occurrence testOccur = occurIt.next();
		assertEquals(testOccur.getDescription(),"Test Occurrence");
		assertEquals(testOccur.getLocation().getCity(),"Los Angeles");
	}
}
