package cs428.project.gather.data.model;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import cs428.project.gather.GatherApplication;
import cs428.project.gather.data.repo.RegistrantRepository;
import cs428.project.gather.data.repo.CategoryRepository;
import cs428.project.gather.data.repo.EventRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(GatherApplication.class)
@WebAppConfiguration
@ActiveProfiles("scratch")
public class ModelIntegrationTest {
	
	@Autowired
	RegistrantRepository registrantRepo;
	
	@Autowired
	EventRepository eventRepo;
	
	@Autowired
	CategoryRepository categoryRepo;
	
//	@Autowired
//	LocationRepository locationRepo;
	
	@Before
	public void setUp() {
		//NOTE: Since Event currently owns the relationship, you must delete the events prior to deleting registrants
		eventRepo.deleteAll();
		registrantRepo.deleteAll();
		categoryRepo.deleteAll();
//		locationRepo.deleteAll();
		
		//Getting the count from the repo has some effect on flushing the tables. 
		//If we don't ask for this count, we get a DataIntegrityViolationException from what seems like a constraint that isn't removed in deleteAll().
		assertEquals(this.eventRepo.count(),0);
		assertEquals(this.registrantRepo.count(),0);
		assertEquals(this.categoryRepo.count(),0);
		this.addThreeCategories();
//		assertEquals(this.locationRepo.count(),0);
		
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
	public void testSaveLoadParticipants(){
		
		Registrant aUser = new Registrant("testuser@email.com","password","testDisplayName",3,10000);
		Registrant registrantResult = this.registrantRepo.save(aUser);
		
		Event testEvent = new Event("Test Event");
		Location location = new Location("Test Location", "6542 Nowhere Blvd", "Los Angeles", "CA", "90005", 34.0498, -118.2498);
//		this.locationRepo.save(location);
		Occurrence occur=new Occurrence("Test Occurrence",new Timestamp(Calendar.getInstance().getTime().getTime()));
//		testEvent.addOccurrence(occur);
		List<Category> foundCategory = categoryRepo.findByName("Others");
		testEvent.setCategory(foundCategory.get(0));
		Event eventResult = this.eventRepo.save(testEvent);
		
		//Right now, Event owns all relationships, so Event must be saved for data to be put in DB.
		testEvent.addParticipant(aUser);
		//It is recommended you also add the Event to the Registrant, so that the in memory state of the objects is consistent with the DB
		//We can make either function do the opposite add if we wish, to simplify usage elsewhere
//		aUser.joinEvent(testEvent);
		eventResult = this.eventRepo.save(testEvent);
		
		//Make sure the event has a participant
		Event foundEvent = this.eventRepo.findOne(eventResult.getId());
		Set<Registrant> participants = foundEvent.getParticipants();
		assertEquals(participants.size(),1);
		
		//Make sure the participant is the correct user
		Iterator<Registrant> participantIt = participants.iterator();
		Registrant testParticipant = participantIt.next();
		assertEquals(testParticipant.getDisplayName(),"testDisplayName");
		assertTrue(eventResult.getCategory().getName().equals("Others"));		
		
		//Make sure the user has reference to the event they joined
		Registrant participant = this.registrantRepo.findOne(registrantResult.getActorID());
		assertEquals(participant.getJoinedEvents().size(), 1);
		
	}

}
