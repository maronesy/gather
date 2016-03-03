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
	}
	
	@Test
	public void testSavesRegistrantCorrectly() {
		
		Registrant aUser = new Registrant("testuser","password","testDisplayName","testuser@email.com",10L,3,10000);
		Registrant result = this.registrantRepo.save(aUser);
		
		assertTrue(result.getUsername().equals("testuser"));	
		assertTrue(result.getPassword().equals("password"));	

	}
	
	@Test
	public void testSearchRegistrantByUsername() {
	
		Registrant aUser = new Registrant("testuser","password","testDisplayName","testuser@email.com",10L,3,10000);
		this.registrantRepo.save(aUser);
		
		List<Registrant> foundUser = registrantRepo.findByUsername("testuser");
		
		assertEquals(1,foundUser.size());
		assertTrue(foundUser.get(0).getUsername().equals("testuser"));
	}
	
	@Test
	public void testDuplicatedRegistrant() {
	
		Registrant aUser = new Registrant("testuser","password","testDisplayName","testuser@email.com",10L,3,10000);
		Registrant aUser2 = new Registrant("testuser","diffPassword","otherDisplayName","different@email.com",10L,3,10000);
		
		this.registrantRepo.save(aUser);
		
		try{
			this.registrantRepo.save(aUser2);
			fail();
		}catch(org.springframework.dao.DataIntegrityViolationException e){
			
		}

	}
	
	@Test
	public void testSearchRegistrantByDisplayName() {
	
		Registrant aUser = new Registrant("testuser","password","testDisplayName","testuser@email.com",10L,3,10000);
		Registrant aDiffUser = new Registrant("diffUser","password","foobar","diffuser@email.com",10L,3,10000);
		Registrant fooBar = new Registrant("foobar","foobarPassword","foobar","foobar@email.com",10L,3,90000);
		this.registrantRepo.save(aUser);
		this.registrantRepo.save(aDiffUser);
		this.registrantRepo.save(fooBar);
		
		List<Registrant> foundUser = registrantRepo.findByDisplayName("foobar");
		
		assertEquals(2,foundUser.size());
		assertTrue(foundUser.get(0).getDisplayName().equals("foobar"));
	}
	
}
