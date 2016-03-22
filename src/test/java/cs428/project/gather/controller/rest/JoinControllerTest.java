package cs428.project.gather.controller.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cs428.project.gather.GatherApplication;
import cs428.project.gather.data.Coordinates;
import cs428.project.gather.data.RESTResponseData;
import cs428.project.gather.data.model.Category;
import cs428.project.gather.data.model.Event;
import cs428.project.gather.data.model.Registrant;
import cs428.project.gather.data.repo.CategoryRepository;
import cs428.project.gather.data.repo.EventRepository;
import cs428.project.gather.data.repo.RegistrantRepository;
import cs428.project.gather.validator.EventsQueryDataValidator;
import cs428.project.gather.validator.NewEventDataValidator;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(GatherApplication.class)
@WebIntegrationTest
public class JoinControllerTest {

	public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private RestTemplate restTemplate = new TestRestTemplate();

    @Autowired
    EventRepository eventRepo;

    @Autowired
    RegistrantRepository regRepo;
    
    @Autowired
    CategoryRepository categoryRepo;

    @Autowired
    private EventsQueryDataValidator eventsQueryDataValidator;

    @Autowired
    private NewEventDataValidator newEventDataValidator;

	@Before
	public void setUp() {
		eventRepo.deleteAll();
		assertEquals(this.eventRepo.count(), 0);
		regRepo.deleteAll();
		assertEquals(this.regRepo.count(), 0);
		Registrant aUser = new Registrant("existed@email.com", "password", "existedName", 10L, 3, 10000);
		this.regRepo.save(aUser);
		assertEquals(this.regRepo.count(), 1);
		this.categoryRepo.deleteAll();
		Category swim= new Category("Swim");
		this.categoryRepo.save(swim);
		
	}


	@Test
	public void testJoinEvent() throws JsonProcessingException {
		ResponseEntity<RESTResponseData> signInResponse = authenticateUser("existed@email.com", "password");
		List<String> cookies = signInResponse.getHeaders().get("Set-Cookie");

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Cookie",StringUtils.join(cookies,';'));
		HttpEntity<String> requestEntity = new HttpEntity<String>(requestHeaders);
		
		// Invoking the API
		
		ResponseEntity<RESTResponseData> response = checkSesseion(requestEntity);
		assertTrue(response.getStatusCode().equals(HttpStatus.OK));

		RESTResponseData responseData = response.getBody();
		assertTrue(responseData.getMessage().equals("Session Found"));
		
		Coordinates eCoor = new Coordinates();
		eCoor.setLatitude(12.34);
		eCoor.setLongitude(111.23);
		
		Coordinates uCoor = new Coordinates();
		uCoor.setLatitude(12.33);
		uCoor.setLongitude(111.24);

		Map<String, Object> apiResponse = attemptAddEvent("EventOne", eCoor, "DescOne", "Swim", System.nanoTime()+10000L, uCoor, StringUtils.join(cookies,';'));
		Object events = apiResponse.get("events");
		List<Event> listEvents = this.eventRepo.findByName("EventOne");
		assertEquals(1, listEvents.size());
		Event anEvent = listEvents.get(0);
		assertEquals("EventOne", anEvent.getName());
		assertEquals("DescOne", anEvent.getDescription());
		
		Long eventId = anEvent.getId();
		Map<String, Object> apiResponse2 = attemptJoinEvent(eventId, StringUtils.join(cookies,';'));
		Set<Registrant> listParticipant = anEvent.getParticipants();
		Registrant participant = null;
		
		//Registrant participant = findParticipant(listParticipant);		
		
		for(Registrant partic: listParticipant){
			if (partic.getEmail().toString() == "existed@email.com"){
				participant = partic;
			}
		}
		assertEquals(true, listParticipant.contains(participant));
	}
	
	private ResponseEntity<RESTResponseData> authenticateUser(String email, String password) throws JsonProcessingException {
		// Building the Request body data
		Map<String, Object> requestBody = new HashMap<String, Object>();
		requestBody.put("email", email);
		requestBody.put("password", password);
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);
		
		// Creating http entity object with request body and headers
		HttpEntity<String> httpEntity = new HttpEntity<String>(OBJECT_MAPPER.writeValueAsString(requestBody),
				requestHeaders);

		@SuppressWarnings("unchecked")
		ResponseEntity<RESTResponseData> result = restTemplate.exchange("http://localhost:8888/rest/registrants/signin", HttpMethod.POST, httpEntity,
				Map.class, Collections.EMPTY_MAP);
		
		assertNotNull(result);
		// Asserting the response of the API.
		//return apiResponse;
		return result;

	}
	
	private ResponseEntity<RESTResponseData> checkSesseion(HttpEntity<String> requestEntity) throws JsonProcessingException {

		// Invoking the API
		
		ResponseEntity<RESTResponseData> response = restTemplate.exchange("http://localhost:8888/rest/session", HttpMethod.GET, requestEntity, RESTResponseData.class);

		assertNotNull(response);
		
		// Asserting the response of the API.
		return response;

	}
	
	private Map<String, Object> attemptAddEvent(String name, Coordinates eCoor, String description, String category, long time, Coordinates uCoor, String session) throws JsonProcessingException {
		// Building the Request body data
		
		Map<String, Object> requestBody = new HashMap<String, Object>();
		requestBody.put("eventName", name);
		requestBody.put("eventCoordinates", eCoor);
		requestBody.put("eventDescription", description);
		requestBody.put("eventCategory", category);
		requestBody.put("eventTime", time);
		requestBody.put("callerCoordinates", uCoor);
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Cookie", session);
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);

		// Creating http entity object with request body and headers
		HttpEntity<String> httpEntity = new HttpEntity<String>(OBJECT_MAPPER.writeValueAsString(requestBody),
				requestHeaders);

		// Invoking the API
		@SuppressWarnings("unchecked")
		Map<String, Object> apiResponse = restTemplate.postForObject("http://localhost:8888/rest/events", httpEntity,
				Map.class, Collections.EMPTY_MAP);


		assertNotNull(apiResponse);

		// Asserting the response of the API.
		return apiResponse;

	}
	
	private Map<String, Object> attemptJoinEvent(Long Id, String session) throws JsonProcessingException {
		// Building the Request body data
		
		Map<String, Object> requestBody = new HashMap<String, Object>();
		requestBody.put("EventId", Id);
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Cookie", session);
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);

		// Creating http entity object with request body and headers
		HttpEntity<String> httpEntity = new HttpEntity<String>(OBJECT_MAPPER.writeValueAsString(requestBody),
				requestHeaders);

		// Invoking the API
		@SuppressWarnings("unchecked")
		Map<String, Object> apiResponse = restTemplate.postForObject("http://localhost:8888/rest/join", httpEntity,
				Map.class, Collections.EMPTY_MAP);


		assertNotNull(apiResponse);

		// Asserting the response of the API.
		return apiResponse;

	}
	
	@Transactional
	public Registrant findParticipant(Set<Registrant> listParticipant){	
		for(Registrant partic: listParticipant){
			if (partic.getEmail().toString() == "existed@email.com"){
				return partic;
			}
		}
		return null;
	}
}
