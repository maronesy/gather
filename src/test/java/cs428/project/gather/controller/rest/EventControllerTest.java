package cs428.project.gather.controller.rest;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
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
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cs428.project.gather.GatherApplication;
import cs428.project.gather.data.Coordinates;
import cs428.project.gather.data.RESTResponseData;
import cs428.project.gather.data.model.Category;
import cs428.project.gather.data.model.Event;
import cs428.project.gather.data.model.Location;
import cs428.project.gather.data.model.Occurrence;
import cs428.project.gather.data.model.Registrant;
import cs428.project.gather.data.repo.CategoryRepository;
import cs428.project.gather.data.repo.EventRepository;
import cs428.project.gather.data.repo.RegistrantRepository;
import cs428.project.gather.validator.EventsQueryDataValidator;
import cs428.project.gather.validator.NewEventDataValidator;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(GatherApplication.class)
@WebIntegrationTest
public class EventControllerTest {

	public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private RestTemplate restTemplate = new TestRestTemplate();

	private static final double ONE_MILE_IN_DEGREES_LATITUDE = 0.014554;
	private static final double ONE_MILE_IN_DEGREES_LONGITUDE = 0.014457;

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
	public void testAddNewEvent() throws JsonProcessingException {
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

		Map<String, Object> apiResponse = attemptAddEvent("EventOne", eCoor, "DescOne", "Swim", System.currentTimeMillis()+10000L, uCoor, StringUtils.join(cookies,';'));

		List<Event> listEvents = this.eventRepo.findByName("EventOne");
		assertEquals(1, listEvents.size());
		Event anEvent = listEvents.get(0);
		assertEquals("EventOne", anEvent.getName());
		assertEquals("DescOne", anEvent.getDescription());

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
	
//	private Map<String, Object> attemptGetEvent(float lat, float lon, float radius, int hour) throws JsonProcessingException {
//		// Building the Request body data
//		Map<String, Object> requestBody = new HashMap<String, Object>();
//		requestBody.put("latitude", lat);
//		requestBody.put("longitude", lon);
//		requestBody.put("radius", radius);
//		requestBody.put("hour", hour);
//		HttpHeaders requestHeaders = new HttpHeaders();
//		requestHeaders.setContentType(MediaType.APPLICATION_JSON);
//
//		// Creating http entity object with request body and headers
//		HttpEntity<String> httpEntity = new HttpEntity<String>(OBJECT_MAPPER.writeValueAsString(requestBody),
//				requestHeaders);
//
//		Object urlVariables = null;
//		// Invoking the API
//	ResponseEntity<RESTResponseData> result = restTemplate.exchange("http://localhost:8888/rest/registrants/signin", HttpMethod.POST, httpEntity,
//	Map.class, Collections.EMPTY_MAP);
////		@SuppressWarnings("unchecked")
////		Map<String, Object> apiResponse = restTemplate.exchange("http://localhost:8888/rest/session", HttpMethod.GET, requestEntity, RESTResponseData.class);
//
//
////		assertNotNull(apiResponse);
//
//		// Asserting the response of the API.
////		return apiResponse;
//
//	}

}
