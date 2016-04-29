package cs428.project.gather.controllers;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Type;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cs428.project.gather.GatherApplication;
import cs428.project.gather.data.model.Category;
import cs428.project.gather.data.repo.CategoryRepository;
import cs428.project.gather.data.repo.EventRepository;
import cs428.project.gather.data.repo.RegistrantRepository;
import cs428.project.gather.data.response.RESTPaginatedResourcesResponseData;
import cs428.project.gather.utilities.GsonHelper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(GatherApplication.class)
@WebIntegrationTest
public class CategoryControllerTest extends ControllerTest {
	
	@Autowired
	RegistrantRepository registrantRepo;
	
	@Autowired
	CategoryRepository categoryRepo;
	
	@Autowired
	EventRepository eventRepo;
	
	@Test
	public void testGetCategories(){
		eventRepo.deleteAll();
		registrantRepo.deleteAll();
		categoryRepo.deleteAll();
		assertEquals(0, categoryRepo.count());
		Category cat1 = new Category("Cat1");
		this.categoryRepo.save(cat1);
		Category cat2 = new Category("Cat2");
		this.categoryRepo.save(cat2);
		Category cat3 = new Category("Cat3");
		this.categoryRepo.save(cat3);
		
		HttpEntity<String> requestEntity = new HttpEntity<String>(new HttpHeaders());
		ResponseEntity<String> responseStr = restTemplate.exchange("http://localhost:8888/rest/categories", HttpMethod.GET, requestEntity, String.class);
		RESTPaginatedResourcesResponseData<Category> responseData = parsePaginatedCategoryResponseData(responseStr.getBody());
		
		assertEquals(3, responseData.getResults().size());
		assertEquals("Cat1", responseData.getResults().get(0).getName());
	}
	
	private RESTPaginatedResourcesResponseData<Category> parsePaginatedCategoryResponseData(String json) {
		Gson gson = GsonHelper.getGson();
		Type resourceType = new TypeToken<RESTPaginatedResourcesResponseData<Category>>() {
		}.getType();
		return gson.fromJson(json, resourceType);
	}

}
