package cs428.project.gather.controller.rest;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cs428.project.gather.GatherApplication;
import cs428.project.gather.data.RESTResponseData;
import cs428.project.gather.data.model.Registrant;
import cs428.project.gather.data.repo.EventRepository;
import cs428.project.gather.data.repo.RegistrantRepository;

import static org.junit.Assert.*;

import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(GatherApplication.class)
@WebIntegrationTest
public class SignOutControllerTest {

	public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private RestTemplate restTemplate = new TestRestTemplate();

	@Autowired
	RegistrantRepository registrantRepo;

	@Autowired
	EventRepository eventRepo;

	@Before
	public void setUp() {
		eventRepo.deleteAll();
		assertEquals(this.eventRepo.count(), 0);
		registrantRepo.deleteAll();
		assertEquals(this.registrantRepo.count(), 0);
		Registrant aUser = new Registrant("existed@email.com", "password", "existedName", 10L, 3, 10000);
		this.registrantRepo.save(aUser);
		assertEquals(this.registrantRepo.count(), 1);
	}
	
	//TODO Mock a session for logged in user to test the case of success
	
	@Test
	public void testSignOutUserFail() throws IOException {
		ResponseEntity<RESTResponseData> response = signOutUser();
		assertTrue(response.getStatusCode().equals(HttpStatus.BAD_REQUEST));

		RESTResponseData responseData = response.getBody();
		assertTrue(responseData.getMessage().equals("User is not in authenticated state"));

		//TODO Need to further confirm the session is updated correctly
	}
	
	private ResponseEntity<RESTResponseData> signOutUser() throws JsonProcessingException {
		
		// Invoking the API
		
		ResponseEntity<RESTResponseData> response = restTemplate.exchange("http://localhost:8888/api/sign-out", HttpMethod.GET, null, RESTResponseData.class);

		assertNotNull(response);
		
		// Asserting the response of the API.
		return response;

	}

}
