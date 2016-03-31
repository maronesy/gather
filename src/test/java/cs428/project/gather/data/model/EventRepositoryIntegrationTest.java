package cs428.project.gather.data.model;

//import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.AssertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import cs428.project.gather.GatherApplication;
import cs428.project.gather.data.repo.CategoryRepository;
import cs428.project.gather.data.repo.EventRepository;
import cs428.project.gather.data.repo.LocationRepository;
import cs428.project.gather.data.repo.RegistrantRepository;

import org.joda.time.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(GatherApplication.class)
@WebAppConfiguration
@ActiveProfiles("scratch")
public class EventRepositoryIntegrationTest {
	@Autowired
	EventRepository eventRepo;
	
	@Autowired
	CategoryRepository categoryRepo;
	
	@Autowired
	RegistrantRepository registrantRepo;
	
//	@Autowired
//	LocationRepository locationRepo;

	@Before
	public void setUp() {
		eventRepo.deleteAll();
		categoryRepo.deleteAll();
		registrantRepo.deleteAll();
//		locationRepo.deleteAll();
		
		//Getting the count from the repo has some effect on flushing the tables. 
		//If we don't ask for this count, we get a DataIntegrityViolationException from what seems like a constraint that isn't removed in deleteAll().
		assertEquals(this.eventRepo.count(),0);
//		assertEquals(this.locationRepo.count(),0);
		assertEquals(this.categoryRepo.count(),0);
		assertEquals(this.registrantRepo.count(),0);
		addThreeCategories();	
	}
	
	private void addThreeCategories() {
		Category sports = new Category("Sports");
		Category dining = new Category("Dining");
		Category others = new Category("Others");
		this.categoryRepo.save(sports);
		this.categoryRepo.save(dining);
		this.categoryRepo.save(others);
	}
	
	@Test
	public void testSavesEventCorrectly() {

		Event testEvent = new Event("Test Event");
		Occurrence occur=new Occurrence("Single Occurrence",new Timestamp(DateTime.now().getMillis()));
		testEvent.addOccurrence(occur);
		List<Category> foundCategory = categoryRepo.findByName("Dining");
		testEvent.setCategory(foundCategory.get(0));
		Event result = this.eventRepo.save(testEvent);
		
		assertTrue(result.getName().equals("Test Event"));		
		assertTrue(result.getCategory().getName().equals("Dining"));		
	}
	
	@Test
	public void testFindByDescription(){
		Event testEvent = new Event("Super Cool Test Event");
		Occurrence occur=new Occurrence("Single Occurrence",new Timestamp(DateTime.now().getMillis()));
		testEvent.addOccurrence(occur);
		List<Category> foundCategory = categoryRepo.findByName("Sports");
		testEvent.setCategory(foundCategory.get(0));
		this.eventRepo.save(testEvent);
			
		List<Event> foundEvents = eventRepo.findByName("Super Cool Test Event");
		
		assertEquals(foundEvents.size(), 1);
		assertTrue(foundEvents.get(0).getName().equals("Super Cool Test Event"));
		assertTrue(foundEvents.get(0).getCategory().getName().equals("Sports"));
	}
	
	@Test
	@Transactional
	public void testSaveLoadEventWithLocation(){
		Event testEvent = new Event("Test Event");
		Location location = new Location("Test Location", "6542 Nowhere Blvd", "Los Angeles", "CA", "90005", 34.0498, -118.2498);
//		this.locationRepo.save(location);
		Occurrence occur=new Occurrence("Test Occurrence",new Timestamp(DateTime.now().getMillis()));
		testEvent.addOccurrence(occur);
		testEvent.setLocation(location);
		List<Category> foundCategory = categoryRepo.findByName("Others");
		testEvent.setCategory(foundCategory.get(0));
		Event result = this.eventRepo.save(testEvent);
		
		Event foundEvent = this.eventRepo.findOne(result.getId());
		List<Occurrence> occurrences = foundEvent.getOccurrences();
		assertEquals(occurrences.size(),1);
		
		Iterator<Occurrence> occurIt = occurrences.iterator();
		Occurrence testOccur = occurIt.next();
		assertEquals(testOccur.getDescription(),"Test Occurrence");
		assertEquals(testEvent.getLocation().getCity(),"Los Angeles");
		assertTrue(result.getCategory().getName().equals("Others"));		
	}
	
	@Test
	@Transactional
	public void testSaveLoadEventWithFeedback(){
		Event testEvent = new Event("Test Event");
		Timestamp feedbackTime = new Timestamp(DateTime.now().getMillis());
		Feedback feedback = new Feedback("What a great event!",5,feedbackTime);
		Occurrence occur=new Occurrence("Test Occurrence",feedbackTime);
		testEvent.addOccurrence(occur);
		testEvent.addFeedback(feedback);
		List<Category> foundCategory = categoryRepo.findByName("Others");
		testEvent.setCategory(foundCategory.get(0));
		Event result = this.eventRepo.save(testEvent);
		
		Event foundEvent = this.eventRepo.findOne(result.getId());
		Set<Feedback> feedbacks = foundEvent.getFeedbacks();
		assertEquals(feedbacks.size(),1);
		
		Iterator<Feedback> feedbackIt = feedbacks.iterator();
		Feedback testFeedback = feedbackIt.next();
		assertEquals(testFeedback.getRating(), 5);
		assertEquals(testFeedback.getReview(), "What a great event!");
		assertEquals(testFeedback.getDatetime(),feedbackTime);
		assertTrue(result.getCategory().getName().equals("Others"));		
	}
	
	
	@Test
	@Transactional
	public void testSaveLoadEventWithChangeLog(){
		Event testEvent = new Event("Test Event");
		Timestamp changeTime = new Timestamp(Calendar.getInstance().getTime().getTime());
		ChangeLog change = new ChangeLog("Description Modified", "Soccer in the park. Everyone is welcome.", changeTime);
		Occurrence occur=new Occurrence("Test Occurrence",changeTime);
		testEvent.addOccurrence(occur);
		testEvent.addChangeLog(change);
		List<Category> foundCategory = categoryRepo.findByName("Others");
		testEvent.setCategory(foundCategory.get(0));
		Event result = this.eventRepo.save(testEvent);
		
		Event foundEvent = this.eventRepo.findOne(result.getId());
		Set<ChangeLog> changeLog = foundEvent.getChangeLog();
		assertEquals(changeLog.size(),1);
		
		Iterator<ChangeLog> changeLogIt = changeLog.iterator();
		ChangeLog testChangeLogEntry = changeLogIt.next();
		assertEquals(testChangeLogEntry.getChangeType(), "Description Modified");
		assertEquals(testChangeLogEntry.getAdditionalInfo(), "Soccer in the park. Everyone is welcome.");
		assertEquals(testChangeLogEntry.getDatetime(),changeTime);
		assertTrue(result.getCategory().getName().equals("Others"));		
	}
	
	@Test
	public void testFindByLocationWithin(){
		Event testEvent = new Event("Test Event with Location");
		Location location = new Location("Test Location", "6542 Nowhere Blvd", "Los Angeles", "CA", "90005", 34.0498, -118.2498);
//		this.locationRepo.save(location);
		testEvent.setLocation(location);
		List<Category> foundCategory = categoryRepo.findByName("Others");
		testEvent.setCategory(foundCategory.get(0));
		Event result = this.eventRepo.save(testEvent);
		
		List<Event> foundEvents = this.eventRepo.findByLocationWithin(30, 35, -120, -115);
		assertEquals(foundEvents.size(), 1);
		assertTrue(foundEvents.get(0).getName().equals("Test Event with Location"));
		
		Event anotherEvent = new Event("Event at similar location");
		Location similarLocation = new Location("Test Location2", "6543 Nowhere Blvd", "Los Angeles", "CA", "90005", 34.0498, -118.2498);
		anotherEvent.setLocation(similarLocation);
		List<Category> anotherFoundCategory = categoryRepo.findByName("Others");
		anotherEvent.setCategory(anotherFoundCategory.get(0));
		result = this.eventRepo.save(anotherEvent);
		
		foundEvents = this.eventRepo.findByLocationWithin(30, 35, -120, -115);
		assertEquals(foundEvents.size(), 2);
		assertTrue(result.getCategory().getName().equals("Others"));		
	}
	
	@Test
	@Transactional
	public void testFindByOccurrenceTimeWithin(){
		Event testEvent = new Event("Test Event");
		
		//Occurence for tomorrow
		DateTime dt = DateTime.now().plusDays(1);
		Timestamp timestamp = new Timestamp(dt.getMillis());
		
		Occurrence occur=new Occurrence("Test Occurrence", timestamp);
		testEvent.addOccurrence(occur);
		List<Category> foundCategory = categoryRepo.findByName("Others");
		testEvent.setCategory(foundCategory.get(0));
		Event result = this.eventRepo.save(testEvent);
		
		Event foundEvent = this.eventRepo.findOne(result.getId());
		List<Occurrence> occurrences = foundEvent.getOccurrences();
		assertEquals(occurrences.size(),1);
	
		Iterator<Occurrence> occurIt = occurrences.iterator();
		Occurrence testOccur = occurIt.next();
		assertEquals(testOccur.getDescription(),"Test Occurrence");

		new DateTime();
		
		//Set upper bound for events in the next 2 days, should return event created above
		dt = DateTime.now().plusDays(2);
		Timestamp upperBound = new Timestamp(dt.getMillis());
		
		List<Event> foundEvents = this.eventRepo.findByOccurrenceTimeWithin(upperBound);
		assertEquals(foundEvents.size(), 1);
		assertTrue(foundEvents.get(0).getName().equals("Test Event"));
		
		//Set upper bound for events in the next 4 hrs, should return no events
		dt = DateTime.now().plusHours(4);
		upperBound = new Timestamp(dt.getMillis());
		
		foundEvents = this.eventRepo.findByOccurrenceTimeWithin(upperBound);
		assertEquals(foundEvents.size(), 0);	
		assertTrue(result.getCategory().getName().equals("Others"));		
	}
	
	@Test
	@Transactional
	public void testFindByLocationAndOccurrenceTimeWithin(){		
		Location location = new Location("Test Location", "6542 Nowhere Blvd", "Los Angeles", "CA", "90005", 34.0498, -118.2498);
//		this.locationRepo.save(location);
		
		Event testEvent = new Event("Test Event with Location");
		//Create occurrences 2 and 5 days in the future
		DateTime dt = DateTime.now().plusDays(2);
		Timestamp timestamp = new Timestamp(dt.getMillis());
		dt = DateTime.now().plusDays(5);
		Timestamp otherTimestamp = new Timestamp(dt.getMillis());
		Occurrence occur=new Occurrence("Test Occurrence",timestamp);
		Occurrence otherOccur = new Occurrence("Other Occurrence", otherTimestamp);
		testEvent.addOccurrence(occur);
		testEvent.addOccurrence(otherOccur);
		testEvent.setLocation(location);
		List<Category> foundCategory = categoryRepo.findByName("Others");
		testEvent.setCategory(foundCategory.get(0));
		Event result = this.eventRepo.save(testEvent);
		
		//Search for events one day in the future, find none
		dt = DateTime.now().plusDays(1);
		Timestamp upperBound = new Timestamp(dt.getMillis());
		List<Event> foundEvents = this.eventRepo.findByLocationAndOccurrenceTimeWithin(30, 35, -120, -115, upperBound);
		assertEquals(foundEvents.size(), 0);
		
		//Search for events three days in the future, find one
		dt = DateTime.now().plusDays(3);
		upperBound = new Timestamp(dt.getMillis());
		foundEvents = this.eventRepo.findByLocationAndOccurrenceTimeWithin(30, 35, -120, -115, upperBound);
		assertEquals(foundEvents.size(), 1);
		
		//Search for events eight days in the future, find one
		dt = DateTime.now().plusDays(8);
		upperBound = new Timestamp(dt.getMillis());
		foundEvents = this.eventRepo.findByLocationAndOccurrenceTimeWithin(30, 35, -120, -115, upperBound);
		assertEquals(foundEvents.size(), 1);
		assertTrue(result.getCategory().getName().equals("Others"));		
	}
		
	@Test
	@Transactional
	public void testRemoveEvent(){		
		//Setting up event
		Event testEvent = new Event("Test Event");
		Location location = new Location("Test Location", "6542 Nowhere Blvd", "Los Angeles", "CA", "90005", 34.0498, -118.2498);
		Occurrence occur=new Occurrence("Test Occurrence",new Timestamp(DateTime.now().getMillis()));
		testEvent.addOccurrence(occur);
		testEvent.setLocation(location);
		List<Category> foundCategory = categoryRepo.findByName("Others");
		testEvent.setCategory(foundCategory.get(0));
		
		//Creating users and join
		Registrant aUser = new Registrant("testuser@email.com","password","testDisplayName",10L,3,10000);
		Registrant participant = this.registrantRepo.save(aUser);
		aUser = new Registrant("owner@email.com","password","owner",10L,3,10000);
		Registrant owner = this.registrantRepo.save(aUser);
		testEvent.addParticipant(participant);
		testEvent.addOwner(owner);
		
		Event result = this.eventRepo.save(testEvent);
		Event foundEvent = this.eventRepo.findOne(result.getId());
		assertTrue(foundEvent.getName().equals("Test Event"));
		Registrant foundParticipant = registrantRepo.findOneByEmail("testuser@email.com");
		Registrant foundSubscriber = registrantRepo.findOneByEmail("subscriber@email.com");
		Registrant foundOwner = registrantRepo.findOneByEmail("owner@email.com");
		assertTrue(foundParticipant!=null);
		assertTrue(foundSubscriber!=null);
		assertTrue(foundOwner!=null);
		
		eventRepo.delete(result);

		Event afterDelete = this.eventRepo.findOne(result.getId());
		assertTrue(afterDelete==null);
		foundParticipant = registrantRepo.findOneByEmail("testuser@email.com");
		foundSubscriber = registrantRepo.findOneByEmail("subscriber@email.com");
		foundOwner = registrantRepo.findOneByEmail("owner@email.com");
		assertTrue(foundParticipant!=null);
		assertTrue(foundSubscriber!=null);
		assertTrue(foundOwner!=null);

		
	}
}
