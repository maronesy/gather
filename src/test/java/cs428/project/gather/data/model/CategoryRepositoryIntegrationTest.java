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

import cs428.project.gather.GatherApplication;
import cs428.project.gather.data.repo.CategoryRepository;
import cs428.project.gather.data.repo.EventRepository;
import cs428.project.gather.data.repo.RegistrantRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(GatherApplication.class)
@WebAppConfiguration
@ActiveProfiles("scratch")
public class CategoryRepositoryIntegrationTest {
	
	@Autowired
	EventRepository eventRepo;
	
	@Autowired
	RegistrantRepository registrantRepo;
	
	@Autowired
	CategoryRepository categoryRepo;
	
	@Before
	public void setUp() {
		//NOTE: Since Event currently owns the relationship, you must delete the events prior to deleting registrants
		eventRepo.deleteAll();
		registrantRepo.deleteAll();
		categoryRepo.deleteAll();
		
		//Getting the count from the repo has some effect on flushing the tables. 
		//If we don't ask for this count, we get a DataIntegrityViolationException from what seems like a constraint that isn't removed in deleteAll().
		assertEquals(this.eventRepo.count(),0);
		assertEquals(this.registrantRepo.count(),0);
		assertEquals(this.categoryRepo.count(),0);
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
	public void testSavesCategoryCorrectly() {
		
		assertEquals(this.categoryRepo.count(),3);	
	}
	
	@Test
	public void testDuplicatedCategory() {
		Category sports = new Category("Sports");
		
		try{
			this.categoryRepo.save(sports);
			fail();
		}catch(org.springframework.dao.DataIntegrityViolationException e){
			
		}
	}
	
	@Test
	public void testSearchCategoryByName() {
			
		List<Category> foundCategory = categoryRepo.findByName("Dining");
		
		assertEquals(1,foundCategory.size());
		assertTrue(foundCategory.get(0).getName().equals("Dining"));
	}
	
}
