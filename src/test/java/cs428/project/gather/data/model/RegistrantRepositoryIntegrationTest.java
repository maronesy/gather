package cs428.project.gather.data.model;

//import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

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
import cs428.project.gather.data.repo.EventRepository;
import cs428.project.gather.data.repo.RegistrantRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(GatherApplication.class)
@WebAppConfiguration
@ActiveProfiles("scratch")
public class RegistrantRepositoryIntegrationTest {
	
	@Autowired
	EventRepository eventRepo;
	
	@Autowired
	RegistrantRepository registrantRepo;
	
	@Before
	public void setUp() {
		//NOTE: Since Event currently owns the relationship, you must delete the events prior to deleting registrants
		eventRepo.deleteAll();
		registrantRepo.deleteAll();
		
		//Getting the count from the repo has some effect on flushing the tables. 
		//If we don't ask for this count, we get a DataIntegrityViolationException from what seems like a constraint that isn't removed in deleteAll().
		assertEquals(this.eventRepo.count(),0);
		assertEquals(this.registrantRepo.count(),0);
	}
	
	@Test
	public void testSavesRegistrantCorrectly() {
		
		Registrant aUser = new Registrant("testuser@email.com","password","testDisplayName",10L,3,10000);
		Registrant result = this.registrantRepo.save(aUser);
		
		assertTrue(result.getEmail().equals("testuser@email.com"));	
		assertTrue(result.getPassword().equals("password"));	

	}
	
	@Test
	public void testDuplicatedRegistrant() {
	
		Registrant aUser = new Registrant("testuser@email.com","password","testDisplayName",10L,3,10000);
		Registrant aUser2 = new Registrant("testuser@email.com","otherPassword","otherDisplayName",10L,3,10000);
		
		this.registrantRepo.save(aUser);
		
		try{
			this.registrantRepo.save(aUser2);
			fail();
		}catch(org.springframework.dao.DataIntegrityViolationException e){
			
		}

	}
	
	@Test
	public void testSearchRegistrantByDisplayName() {
	
		Registrant aUser = new Registrant("testuser@email.com","password","testDisplayName",10L,3,10000);
		Registrant aDiffUser = new Registrant("diffuser@email.com","password","foobar",10L,3,10000);
		Registrant fooBar = new Registrant("foobar@email.com","foobarPassword","other",10L,3,90000);
		this.registrantRepo.save(aUser);
		this.registrantRepo.save(aDiffUser);
		this.registrantRepo.save(fooBar);
		
		List<Registrant> foundUser = registrantRepo.findByDisplayName("foobar");
		
		assertEquals(1,foundUser.size());
		assertTrue(foundUser.get(0).getDisplayName().equals("foobar"));
	}
	
	@Test
	public void testSearchRegistrantByEmail(){
		Registrant aUser = new Registrant("testuser@email.com","password","testDisplayName",10L,3,10000);
		Registrant aDiffUser = new Registrant("diffuser@email.com","password","foobar",10L,3,10000);
		Registrant fooBar = new Registrant("foobar@email.com","foobarPassword","other",10L,3,90000);
		this.registrantRepo.save(aUser);
		this.registrantRepo.save(aDiffUser);
		this.registrantRepo.save(fooBar);
		
		Registrant foundUser = registrantRepo.findOneByEmail("foobar@email.com");
		
		assertTrue(foundUser.getEmail().equals("foobar@email.com"));
	}
	
}
