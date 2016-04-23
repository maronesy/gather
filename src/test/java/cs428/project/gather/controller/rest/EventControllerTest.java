package cs428.project.gather.controller.rest;

import java.sql.Timestamp;

import static org.junit.Assert.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cs428.project.gather.GatherApplication;
import cs428.project.gather.data.*;
import cs428.project.gather.data.model.*;
import cs428.project.gather.data.repo.*;
import cs428.project.gather.data.response.*;
import cs428.project.gather.utilities.GsonHelper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(GatherApplication.class)
@WebIntegrationTest
public class EventControllerTest {

	public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private RestTemplate restTemplate = new TestRestTemplate();

	@Autowired
	EventRepository eventRepo;

	@Autowired
	RegistrantRepository regRepo;

	@Autowired
	CategoryRepository categoryRepo;

	@Before
	public void setup() {
		//Event setup
		eventRepo.deleteAll();
		assertEquals(this.eventRepo.count(), 0);

		//Registrant setup
		regRepo.deleteAll();
		assertEquals(this.regRepo.count(), 0);
		List<Registrant> users = new ArrayList<Registrant>();
		users.add(new Registrant("existed@email.com", "password", "existedName", 10L, 3, 10000));
		users.add(new Registrant("participant@email.com", "password", "participantName", 10L, 3, 10000));
		users.add(new Registrant("newOwner@email.com", "password", "newOwner", 10L, 3, 10000));
		users.add(new Registrant("nonOwner@email.com", "password", "nonOwner", 10L, 3, 10000));
		this.regRepo.save(users);
		assertEquals(this.regRepo.count(), 4);

		//Category setup
		this.categoryRepo.deleteAll();
		Category testCat = new Category("Test");
		this.categoryRepo.save(testCat);
		Category swim = new Category("Swim");
		this.categoryRepo.save(swim);
		Category soccer = new Category("Soccer");
		this.categoryRepo.save(soccer);
	}
	
	@Test
	public void testGetEvent() throws JsonProcessingException {
		// Create events
		List<Event> events = createTestEvents(10);
		this.eventRepo.save(events);

		Coordinates eCoor = getTestCoordinates();
		ResponseEntity<String> apiResponse = attemptGetEvent(eCoor.getLatitude(), eCoor.getLongitude(), 10,
				500);
		assertTrue(apiResponse.getStatusCode().equals(HttpStatus.OK));
		
		// Parse the data back to RESTPaginatedResourcesResponseData<Event>
		RESTPaginatedResourcesResponseData<Event> resourceResponseData = parsePaginatedEventResponseData(
				apiResponse.getBody());

		// Make sure all events are returned
		assertEquals(10, resourceResponseData.getCount());
		List<Event> returnedEvents = resourceResponseData.getResults();
		assertEquals(10, returnedEvents.size());
		assertEquals("Single Occurrence", returnedEvents.get(0).getOccurrences().get(0).getDescription());
	}

	@Test
	public void testGetEventWrongRadius() throws JsonProcessingException {
		Coordinates eCoor = getTestCoordinates();
		// Using radius of 100, which is too large - Check EventsQueryData for
		// MAX_RADIUS
		ResponseEntity<String> apiResponse = attemptGetEvent(eCoor.getLatitude(), eCoor.getLongitude(), 100,
				500);
		assertTrue(apiResponse.getStatusCode().equals(HttpStatus.BAD_REQUEST));
	}
	
	@Test
	public void testGetEventManyEvents() throws JsonProcessingException {
		// Create events
		List<Event> events = createTestEvents(35);
		this.eventRepo.save(events);
		
		Coordinates eCoor = getTestCoordinates();
		ResponseEntity<String> apiResponse = attemptGetEvent(eCoor.getLatitude(), eCoor.getLongitude(), 10,
				500);
		assertTrue(apiResponse.getStatusCode().equals(HttpStatus.OK));
		
		// Parse the data back to RESTPaginatedResourcesResponseData<Event>
		RESTPaginatedResourcesResponseData<Event> resourceResponseData = parsePaginatedEventResponseData(
				apiResponse.getBody());

		// Make sure all events are returned
		assertEquals(35, resourceResponseData.getCount());
		
		// Make sure paginated data only returns the first 20 (as indicated by
		// RESTPaginatedResourceResponseData)
		List<Event> returnedEvents = resourceResponseData.getResults();
		assertEquals(20, returnedEvents.size());
		assertEquals("Single Occurrence", returnedEvents.get(0).getOccurrences().get(0).getDescription());
	}
	
	private ResponseEntity<String> attemptGetEvent(double lat, double lon, float radius, int hour)
			throws JsonProcessingException {
		// Building the Request body data
		Map<String, Object> requestBody = new HashMap<String, Object>();
		requestBody.put("latitude", lat);
		requestBody.put("longitude", lon);
		requestBody.put("radiusMi", radius);
		requestBody.put("hour", hour);
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);

		// Creating http entity object with request body and headers
		HttpEntity<String> httpEntity = new HttpEntity<String>(OBJECT_MAPPER.writeValueAsString(requestBody),
				requestHeaders);

		// Invoking the API
		ResponseEntity<String> apiResponse = restTemplate.exchange("http://localhost:8888/rest/events",
				HttpMethod.PUT, httpEntity, String.class);

		assertNotNull(apiResponse);
		return apiResponse;

	}

	@Test
	public void testAddNewEvent() throws JsonProcessingException {
		//Make sure event doesn't originally exist
		List<Event> listEvents = this.eventRepo.findByName("EventOne");
		assertEquals(0, listEvents.size());

		HttpEntity<String> requestEntity = signInAndCheckSession("existed@email.com", "password");

		//Setup coords
		Coordinates eCoor = getTestCoordinates();
		Coordinates uCoor = getTestCoordinates();

		attemptAddEvent("EventOne", eCoor, "DescOne", "Swim", System.nanoTime() + 10000L, uCoor,
				requestEntity.getHeaders());

		//Verify that event now exists in backend
		listEvents = this.eventRepo.findByName("EventOne");
		assertEquals(1, listEvents.size());
		Event anEvent = listEvents.get(0);
		assertEquals("EventOne", anEvent.getName());
		assertEquals("DescOne", anEvent.getDescription());

	}

	private Map<String, Object> attemptAddEvent(String name, Coordinates eCoor, String description, String category,
			long time, Coordinates uCoor, HttpHeaders header) throws JsonProcessingException {
		// Building the Request body data
		Map<String, Object> requestBody = new HashMap<String, Object>();
		List<Long> occurrences = new ArrayList<Long>();
		requestBody.put("eventName", name);
		requestBody.put("eventCoordinates", eCoor);
		requestBody.put("eventDescription", description);
		requestBody.put("eventCategory", category);
		occurrences.add(time);
		requestBody.put("eventOccurrences", occurrences);
		requestBody.put("callerCoordinates", uCoor);
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Cookie", header.getFirst("Cookie"));
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);

		Map<String, Object> apiResponse = postRequestBodyForObject(header, requestBody, "http://localhost:8888/rest/events");

		assertNotNull(apiResponse);
		return apiResponse;

	}

	@Test
	public void testJoinEvent() throws JsonProcessingException {
		// Create event
		Event event1 = createSingleTestEvent();
		this.eventRepo.save(event1);

		// Get user
		Registrant user = this.regRepo.findOneByEmail("existed@email.com");

		// Make sure user is not a participant
		assertFalse(event1.getParticipants().contains(user));

		HttpEntity<String> requestEntity = signInAndCheckSession("existed@email.com", "password");

		// Receive the request body as a string so that it can be parsed and
		// validated
		ResponseEntity<String> responseStr = attemptJoinEvent(event1.getId(), requestEntity.getHeaders());
		assertEquals(HttpStatus.CREATED, responseStr.getStatusCode());

		// Parse the data back to RESTPaginatedResourcesResponseData<Event>
		RESTResourceResponseData<Event> resourceResponseData = parseEventResponseData(responseStr.getBody());
		Event frontendEvent = resourceResponseData.getResult();

		// Make sure participant now listed in event returned to frontend
		assertEquals(event1.getId(), frontendEvent.getId());
		assertTrue(frontendEvent.getParticipants().contains(user));

		// Make sure participant now listed in event in backend
		Event backendEvent = this.eventRepo.findOne(event1.getId());
		assertTrue(backendEvent.getParticipants().contains(user));
	}

	@Test
	public void testJoinEventNotSignedIn() throws JsonProcessingException {
		// Create event
		Event event1 = createSingleTestEvent();
		this.eventRepo.save(event1);

		HttpEntity<String> requestEntity = new HttpEntity<String>(new HttpHeaders());
		ResponseEntity<String> response = attemptJoinEvent(event1.getId(), requestEntity.getHeaders());
		// Make sure the request is rejected since user is not signed in
		assertTrue(response.getStatusCode().equals(HttpStatus.BAD_REQUEST));

		// Check error message
		RESTPaginatedResourcesResponseData<Event> resourceResponseData = parsePaginatedEventResponseData(
				response.getBody());
		assertEquals("Incorrect User State. Only registered users can access /rest/events/join ",
				resourceResponseData.getMessage());
	}

	private ResponseEntity<String> attemptJoinEvent(Long Id, HttpHeaders headers) throws JsonProcessingException {
		// Building the Request body data
		Map<String, Object> requestBody = new HashMap<String, Object>();
		requestBody.put("eventId", Id);
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Cookie", headers.getFirst("Cookie"));
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> httpEntity = new HttpEntity<String>(OBJECT_MAPPER.writeValueAsString(requestBody),
				requestHeaders);

		// Invoking the API
		ResponseEntity<String> responseStr = restTemplate.postForEntity("http://localhost:8888/rest/events/join",
				httpEntity, String.class);

		assertNotNull(responseStr);
		return responseStr;
	}
	
	@Test
	public void testLeaveEvent() throws JsonProcessingException {
		
		Registrant user = this.regRepo.findOneByEmail("existed@email.com");
		
		Event event1 = createSingleTestEvent();
		event1.addParticipant(user);
		this.eventRepo.save(event1);

		// Make sure user is participant
		assertTrue(event1.getParticipants().contains(user));

		HttpEntity<String> requestEntity = signInAndCheckSession("existed@email.com", "password");

		// Receive the request body as a string so that it can be parsed and
		// validated
		ResponseEntity<String> responseStr = attemptLeaveEvent(event1.getId(), requestEntity.getHeaders());
		assertEquals(HttpStatus.OK, responseStr.getStatusCode());

		// Parse the data back to RESTPaginatedResourcesResponseData<Event>
		RESTResourceResponseData<Event> resourceResponseData = parseEventResponseData(responseStr.getBody());
		Event frontendEvent = resourceResponseData.getResult();

		// Make sure participant no longer listed in event returned to frontend
		assertEquals(event1.getId(), frontendEvent.getId());
		assertFalse(frontendEvent.getParticipants().contains(user));

		// Make sure participant no longer listed in event in backend
		Event backendEvent = this.eventRepo.findOne(event1.getId());
		assertFalse(backendEvent.getParticipants().contains(user));

	}
	
	@Test
	public void testLeaveEventNotSignedIn() throws JsonProcessingException{
		
		Registrant user = this.regRepo.findOneByEmail("existed@email.com");
		
		Event event1 = createSingleTestEvent();
		event1.addParticipant(user);
		this.eventRepo.save(event1);
		
		HttpEntity<String> requestEntity = new HttpEntity<String>(new HttpHeaders());
		ResponseEntity<String> response = attemptLeaveEvent(event1.getId(), requestEntity.getHeaders());
		// Make sure the request is rejected since user is not signed in
		assertTrue(response.getStatusCode().equals(HttpStatus.BAD_REQUEST));

		// Check error message
		RESTPaginatedResourcesResponseData<Event> resourceResponseData = parsePaginatedEventResponseData(
				response.getBody());
		assertEquals("Incorrect User State. Only registered users can access /rest/events/leave ",
				resourceResponseData.getMessage());
	}
	
	@Test
	public void testLeaveEventSoleOwner() throws JsonProcessingException{
		
		Registrant user = this.regRepo.findOneByEmail("existed@email.com");
		
		Event event1 = createSingleTestEvent();
		event1.addParticipant(user);
		event1.addOwner(user);
		this.eventRepo.save(event1);
		
		// Make sure user is participant and owner
		assertTrue(event1.getParticipants().contains(user));
		assertTrue(event1.getOwners().contains(user));
		
		HttpEntity<String> requestEntity = signInAndCheckSession("existed@email.com", "password");

		// Receive the request body as a string so that it can be parsed and
		// validated
		ResponseEntity<String> response = attemptLeaveEvent(event1.getId(), requestEntity.getHeaders());
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		
		// Check error message
		RESTPaginatedResourcesResponseData<Event> resourceResponseData = parsePaginatedEventResponseData(
				response.getBody());
		assertEquals("Cannot leave event. You are the sole owner. Add a co-owner or remove the event. ",
				resourceResponseData.getMessage());
		
	}
	
	@Test
	public void testLeaveEventOtherOwners() throws JsonProcessingException{
		
		Registrant user = this.regRepo.findOneByEmail("existed@email.com");
		Registrant otherOwner = this.regRepo.findOneByEmail("newOwner@email.com");
		
		Event event1 = createSingleTestEvent();
		event1.addParticipant(user);
		event1.addOwner(user);
		event1.addOwner(otherOwner);
		this.eventRepo.save(event1);

		// Make sure user is participant
		assertTrue(event1.getParticipants().contains(user));
		assertTrue(event1.getOwners().contains(user));
		
		HttpEntity<String> requestEntity = signInAndCheckSession("existed@email.com", "password");

		// Receive the request body as a string so that it can be parsed and
		// validated
		ResponseEntity<String> responseStr = attemptLeaveEvent(event1.getId(), requestEntity.getHeaders());
		assertEquals(HttpStatus.OK, responseStr.getStatusCode());

		// Parse the data back to RESTPaginatedResourcesResponseData<Event>
		RESTResourceResponseData<Event> resourceResponseData = parseEventResponseData(responseStr.getBody());
		Event frontendEvent = resourceResponseData.getResult();

		// Make sure participant no longer listed in event returned to frontend
		assertEquals(event1.getId(), frontendEvent.getId());
		assertFalse(frontendEvent.getParticipants().contains(user));
		assertFalse(frontendEvent.getOwners().contains(user));

		// Make sure participant no longer listed in event in backend
		Event backendEvent = this.eventRepo.findOne(event1.getId());
		assertFalse(backendEvent.getParticipants().contains(user));
		assertFalse(backendEvent.getOwners().contains(user));
	}

	private ResponseEntity<String> attemptLeaveEvent(Long Id, HttpHeaders headers) throws JsonProcessingException {
		// Building the Request body data
		Map<String, Object> requestBody = new HashMap<String, Object>();
		requestBody.put("eventId", Id);
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Cookie", headers.getFirst("Cookie"));
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> httpEntity = new HttpEntity<String>(OBJECT_MAPPER.writeValueAsString(requestBody),
				requestHeaders);

		// Invoking the API
		ResponseEntity<String> responseStr = restTemplate.postForEntity("http://localhost:8888/rest/events/leave",
				httpEntity, String.class);

		assertNotNull(responseStr);
		return responseStr;
	}
	
	

	@Test
	public void testUpdateEventBasic() throws JsonProcessingException {
		//Setup event with owner
		Registrant origOwner = this.regRepo.findOneByEmail("existed@email.com");
		Registrant newParticipant = regRepo.findOneByEmail("participant@email.com");
		
		Event event1 = createSingleTestEvent();
		event1.addOwner(origOwner);
		event1.addParticipant(origOwner);
		event1.addParticipant(newParticipant);
		event1 = eventRepo.save(event1);
		
		assertEquals(2, event1.getParticipants().size());

		HttpEntity<String> requestEntity = signInAndCheckSession("existed@email.com", "password");

		//Get new owner
		Registrant newOwner = regRepo.findOneByEmail("newOwner@email.com");

		//Get coords
		Coordinates eCoor = getTestCoordinates();
		Coordinates uCoor = getTestCoordinates();

		//Test the conditions before the update
		assertEquals("Event1", event1.getName());
		assertEquals("Test", event1.getCategory().getName());
		assertEquals(1, event1.getOccurrences().size());
		assertEquals(1, event1.getOwners().size());
		assertEquals(2, event1.getParticipants().size());
		assertTrue(event1.getParticipants().contains(newParticipant));
		assertFalse(event1.getOwners().contains(newOwner));
		assertTrue(event1.getOwners().contains(origOwner));

		// Test modifying the event
		attemptUpdateEvent(event1, "EventOneUpdated", eCoor, "DescOneUpdated", "Soccer", System.nanoTime() + 20000L,
				uCoor, requestEntity.getHeaders(), newParticipant, newOwner);

		// Verify the event got updated
		Event afterUpdate = eventRepo.findOne(event1.getId());
		assertEquals("EventOneUpdated", afterUpdate.getName());
		assertEquals("DescOneUpdated", afterUpdate.getDescription());
		assertEquals("Soccer", afterUpdate.getCategory().getName());
		assertEquals(2, afterUpdate.getOccurrences().size());
		assertEquals(2, afterUpdate.getOwners().size());
		assertEquals(1, afterUpdate.getParticipants().size());
		assertFalse(afterUpdate.getParticipants().contains(newParticipant));
		assertTrue(afterUpdate.getOwners().contains(newOwner));
		assertTrue(afterUpdate.getOwners().contains(origOwner));
	}
	
	@Test
	public void testUpdateEventNonOwner() throws JsonProcessingException {
		//Setup event with owner
		Registrant origOwner = this.regRepo.findOneByEmail("existed@email.com");
		Category swim = this.categoryRepo.findByName("Swim").get(0);
		assertTrue(swim != null);
		Event eventOne = new Event("EventOne");
		eventOne.setCategory(swim);
		eventOne.addOwner(origOwner);
		eventOne.addParticipant(origOwner);

		// Add the other participant
		Registrant newParticipant = regRepo.findOneByEmail("participant@email.com");
		eventOne.addParticipant(newParticipant);
		eventOne = eventRepo.save(eventOne);
		assertEquals(2, eventOne.getParticipants().size());

		//Sign in as ***NON-OWNER*** - Owner of target event is existed@email.com
		HttpEntity<String> requestEntity = signInAndCheckSession("nonOwner@email.com", "password");

		//Get new owner
		Registrant newOwner = regRepo.findOneByEmail("newOwner@email.com");

		//Get coords
		Coordinates eCoor = getTestCoordinates();
		Coordinates uCoor = getTestCoordinates();

		//Test the conditions before the update
		assertEquals("EventOne", eventOne.getName());
		assertEquals("Swim", eventOne.getCategory().getName());
		assertEquals(0, eventOne.getOccurrences().size());
		assertEquals(1, eventOne.getOwners().size());
		assertEquals(2, eventOne.getParticipants().size());
		assertTrue(eventOne.getParticipants().contains(newParticipant));
		assertFalse(eventOne.getOwners().contains(newOwner));
		assertTrue(eventOne.getOwners().contains(origOwner));

		// Test modifying the event
		attemptUpdateEvent(eventOne, "EventOneUpdated", eCoor, "DescOneUpdated", "Soccer", System.nanoTime() + 20000L,
				uCoor, requestEntity.getHeaders(), newParticipant, newOwner);

		// Verify that nothing changed
		Event afterUpdate = eventRepo.findOne(eventOne.getId());
		assertEquals("EventOne", afterUpdate.getName());
		assertEquals("Swim", afterUpdate.getCategory().getName());
		assertEquals(0, afterUpdate.getOccurrences().size());
		assertEquals(1, afterUpdate.getOwners().size());
		assertEquals(2, afterUpdate.getParticipants().size());
		assertTrue(afterUpdate.getParticipants().contains(newParticipant));
		assertFalse(afterUpdate.getOwners().contains(newOwner));
		assertTrue(afterUpdate.getOwners().contains(origOwner));
	}
	
	@Test
	public void testUpdateEventAddSingleOwner() throws JsonProcessingException {
		//Setup event with owner
		Registrant origOwner = this.regRepo.findOneByEmail("existed@email.com");
		Registrant newParticipant = regRepo.findOneByEmail("participant@email.com");
		
		Event event1 = createSingleTestEvent();
		event1.addOwner(origOwner);
		event1.addParticipant(origOwner);
		event1.addParticipant(newParticipant);
		event1 = eventRepo.save(event1);
		
		assertEquals(2, event1.getParticipants().size());

		HttpEntity<String> requestEntity = signInAndCheckSession("existed@email.com", "password");

		//Get new owner
		Registrant newOwner = regRepo.findOneByEmail("newOwner@email.com");

		//Test the conditions before the update
		assertEquals("Event1", event1.getName());
		assertEquals("Test", event1.getCategory().getName());
		assertEquals(1, event1.getOccurrences().size());
		assertEquals(1, event1.getOwners().size());
		assertEquals(2, event1.getParticipants().size());
		assertTrue(event1.getParticipants().contains(newParticipant));
		assertFalse(event1.getOwners().contains(newOwner));
		assertTrue(event1.getOwners().contains(origOwner));

		// Test modifying the event
		attemptUpdateEventAddOwner(event1, requestEntity.getHeaders(), newOwner);

		// Verify the event got updated
		Event afterUpdate = eventRepo.findOne(event1.getId());
		assertEquals("Event1", afterUpdate.getName());
		assertEquals("Test", afterUpdate.getCategory().getName());
		assertEquals(1, afterUpdate.getOccurrences().size());
		assertEquals(2, afterUpdate.getOwners().size());
		assertEquals(2, afterUpdate.getParticipants().size());
		assertTrue(afterUpdate.getParticipants().contains(newParticipant));
		assertTrue(afterUpdate.getOwners().contains(newOwner));
		assertTrue(afterUpdate.getOwners().contains(origOwner));
	}
	
	@Test
	public void testUpdateEventAddMultipleParticipants() throws JsonProcessingException {
		//Setup event with owner
		Registrant owner = this.regRepo.findOneByEmail("existed@email.com");
		Registrant participant = regRepo.findOneByEmail("participant@email.com");
		
		Event event1 = createSingleTestEvent();
		event1.addOwner(owner);
		event1.addParticipant(owner);
		event1.addParticipant(participant);
		event1 = eventRepo.save(event1);
		
		assertEquals(2, event1.getParticipants().size());

		HttpEntity<String> requestEntity = signInAndCheckSession("existed@email.com", "password");

		//Get new participants
		Registrant newParticipant = regRepo.findOneByEmail("newOwner@email.com");
		Registrant otherParticipant = regRepo.findOneByEmail("nonOwner@email.com");
		List<Registrant> participantsToAdd = new ArrayList<Registrant>();
		participantsToAdd.add(newParticipant);
		participantsToAdd.add(otherParticipant);

		//Test the conditions before the update
		assertEquals("Event1", event1.getName());
		assertEquals("Test", event1.getCategory().getName());
		assertEquals(1, event1.getOccurrences().size());
		assertEquals(1, event1.getOwners().size());
		assertEquals(2, event1.getParticipants().size());
		assertTrue(event1.getParticipants().contains(participant));
		assertTrue(event1.getOwners().contains(owner));

		// Test modifying the event
		attemptUpdateEventAddMultipleParticipants(event1, requestEntity.getHeaders(), participantsToAdd);

		// Verify the event got updated
		Event afterUpdate = eventRepo.findOne(event1.getId());
		assertEquals("Event1", afterUpdate.getName());
		assertEquals("Test", afterUpdate.getCategory().getName());
		assertEquals(1, afterUpdate.getOccurrences().size());
		assertEquals(1, afterUpdate.getOwners().size());
		assertEquals(4, afterUpdate.getParticipants().size());
		assertTrue(afterUpdate.getOwners().contains(owner));
		assertTrue(afterUpdate.getParticipants().contains(participant));
		assertTrue(afterUpdate.getParticipants().contains(newParticipant));
		assertTrue(afterUpdate.getParticipants().contains(otherParticipant));
	}
	
	@Test
	public void testUpdateEventAddMultipleOccurrences() throws JsonProcessingException {
		//Setup event with owner
		Registrant owner = this.regRepo.findOneByEmail("existed@email.com");
		
		Event event1 = createSingleTestEvent();
		event1.addOwner(owner);
		event1.addParticipant(owner);
		event1 = eventRepo.save(event1);
		
		HttpEntity<String> requestEntity = signInAndCheckSession("existed@email.com", "password");

		//Set up times for occurrences to add
		Long day2, day3, day4;
		day2 = DateTime.now().plusDays(2).getMillis();
		day3 = DateTime.now().plusDays(3).getMillis();
		day4 = DateTime.now().plusDays(4).getMillis();
		List<Long> newOccurrenceTimes = Arrays.asList(day2, day3, day4);	

		//Test the conditions before the update
		assertEquals("Event1", event1.getName());
		assertEquals("Test", event1.getCategory().getName());
		assertEquals(1, event1.getOccurrences().size());
		assertEquals(1, event1.getOwners().size());
		assertEquals(1, event1.getParticipants().size());

		// Test modifying the event
		attemptUpdateEventAddMultipleOccurrences(event1, requestEntity.getHeaders(), newOccurrenceTimes);

		// Verify the event got updated
		Event afterUpdate = eventRepo.findOne(event1.getId());
		assertEquals("Event1", afterUpdate.getName());
		assertEquals("Test", afterUpdate.getCategory().getName());
		assertEquals(4, afterUpdate.getOccurrences().size());
		assertEquals(1, afterUpdate.getOwners().size());
		assertEquals(1, afterUpdate.getParticipants().size());
		
		Long time = afterUpdate.getOccurrences().get(1).getTimestamp().getTime();
		assertEquals(day2, time);
	}
	
	private Map<String, Object> attemptUpdateEventAddOwner(Event event, HttpHeaders header, Registrant ownerToAdd) throws JsonProcessingException {
		long eventId=event.getId();
		
		List<String> owners = new ArrayList<String>();
		for(Registrant owner : event.getOwners()){
			owners.add(owner.getDisplayName());
		}

		owners.add(ownerToAdd.getDisplayName());

		// Building the Request body data
		Map<String, Object> requestBody = new HashMap<String, Object>();
		requestBody.put("eventId", eventId);
		requestBody.put("owners", owners);

		Map<String, Object> apiResponse = postRequestBodyForObject(header, requestBody, "http://localhost:8888/rest/events/update");

		assertNotNull(apiResponse);

		return apiResponse;
	}
	
	private Map<String, Object> attemptUpdateEventAddMultipleParticipants(Event event, HttpHeaders header, List<Registrant> participantsToAdd) throws JsonProcessingException {
		long eventId=event.getId();
		
		List<String> participants = new ArrayList<String>();
		for(Registrant participant : event.getParticipants()){
			participants.add(participant.getDisplayName());
		}

		for(Registrant participant : participantsToAdd){
			participants.add(participant.getDisplayName());
		}
		
		// Building the Request body data
		Map<String, Object> requestBody = new HashMap<String, Object>();
		requestBody.put("eventId", eventId);
		requestBody.put("participants", participants);

		Map<String, Object> apiResponse = postRequestBodyForObject(header, requestBody, "http://localhost:8888/rest/events/update");

		assertNotNull(apiResponse);

		return apiResponse;
	}
	
	private Map<String, Object> attemptUpdateEventAddMultipleOccurrences(Event event, HttpHeaders header, List<Long> occurrenceTimesToAdd) throws JsonProcessingException {
		long eventId=event.getId();
		
		List<Long> occurrenceTimes = new ArrayList<Long>();
		for(Occurrence occurrence : event.getOccurrences()){
			occurrenceTimes.add(occurrence.getTimestamp().getTime());
		}
		
		occurrenceTimes.addAll(occurrenceTimesToAdd);
		
		// Building the Request body data
		Map<String, Object> requestBody = new HashMap<String, Object>();
		requestBody.put("eventId", eventId);
		requestBody.put("eventOccurrences", occurrenceTimes);

		Map<String, Object> apiResponse = postRequestBodyForObject(header, requestBody, "http://localhost:8888/rest/events/update");

		assertNotNull(apiResponse);

		return apiResponse;
	}

	private Map<String, Object> attemptUpdateEvent(Event event, String name, Coordinates eCoor, String description,
			String category, long time, Coordinates uCoor, HttpHeaders header, Registrant participantToRemove,
			Registrant ownerToAdd) throws JsonProcessingException {
		
		long eventId=event.getId();
		List<Long> occurrences= new ArrayList<Long>();
		for(int i=0; i<event.getOccurrences().size(); i++){
			occurrences.add(event.getOccurrences().get(i).getTimestamp().getTime());
		}
		List<String> owners = new ArrayList<String>();
		List<String> participants = new ArrayList<String>();
		Iterator<Registrant> ownerIter = event.getOwners().iterator();
		while (ownerIter.hasNext()){
			owners.add(ownerIter.next().getDisplayName());
		}
		Iterator<Registrant> partIter = event.getParticipants().iterator();
		while (partIter.hasNext()){
			participants.add(partIter.next().getDisplayName());
		}
		owners.add(ownerToAdd.getDisplayName());
		participants.remove(participantToRemove.getDisplayName());
		occurrences.add(time);
		// Building the Request body data
		Map<String, Object> requestBody = new HashMap<String, Object>();
		requestBody.put("eventId", eventId);
		requestBody.put("eventName", name);
		requestBody.put("eventCoordinates", eCoor);
		requestBody.put("eventDescription", description);
		requestBody.put("eventCategory", category);
		requestBody.put("callerCoordinates", uCoor);
		requestBody.put("eventOccurrences", occurrences);
		requestBody.put("owners", owners);
		requestBody.put("participants", participants);

		Map<String, Object> apiResponse = postRequestBodyForObject(header, requestBody, "http://localhost:8888/rest/events/update");

		assertNotNull(apiResponse);

		return apiResponse;
	}

	@Test
	public void testGetJoinedEventListSingleEvent() throws JsonProcessingException {
		
		Registrant user = this.regRepo.findOneByEmail("existed@email.com");
		
		Event event1 = createSingleTestEvent();
		event1.addParticipant(user);
		this.eventRepo.save(event1);

		HttpEntity<String> requestEntity = signInAndCheckSession("existed@email.com", "password");

		RESTPaginatedResourcesResponseData<Event> resourceResponseData = getPaginatedResponse("http://localhost:8888/rest/events/userJoined", requestEntity);

		// Make sure event returned
		List<Event> returnedEvents = resourceResponseData.getResults();
		assertEquals(1, returnedEvents.size());

		// Check event
		Event event = returnedEvents.get(0);
		assertEquals("Event1", event.getName());
	}

	@Test
	public void testGetJoinedEventListMultipleEvents() throws JsonProcessingException {
		
		Registrant user = this.regRepo.findOneByEmail("existed@email.com");
		
		List<Event> events = createTestEvents(3);
		addParticipant(events, user);
		this.eventRepo.save(events);

		HttpEntity<String> requestEntity = signInAndCheckSession("existed@email.com", "password");

		RESTPaginatedResourcesResponseData<Event> resourceResponseData = getPaginatedResponse("http://localhost:8888/rest/events/userJoined", requestEntity);

		// Make sure all 3 events returned
		List<Event> returnedEvents = resourceResponseData.getResults();
		assertEquals(3, returnedEvents.size());
	}

	@Test
	public void testGetJoinedEventListManyEvents() throws JsonProcessingException {
		
		Registrant user = this.regRepo.findOneByEmail("existed@email.com");
		
		List<Event> events = createTestEvents(35);
		addParticipant(events, user);
		this.eventRepo.save(events);

		HttpEntity<String> requestEntity = signInAndCheckSession("existed@email.com", "password");

		RESTPaginatedResourcesResponseData<Event> resourceResponseData = getPaginatedResponse("http://localhost:8888/rest/events/userJoined", requestEntity);

		// Make sure all events are returned
		assertEquals(35, resourceResponseData.getCount());

		// Make sure paginated data only returns the first 20 (as indicated by
		// RESTPaginatedResourceResponseData)
		List<Event> returnedEvents = resourceResponseData.getResults();
		assertEquals(20, returnedEvents.size());

		// Check second page for remaining 15
		resourceResponseData = getPaginatedResponse("http://localhost:8888/rest/events/userJoined?page=2", requestEntity);
		returnedEvents = resourceResponseData.getResults();
		assertEquals(15, returnedEvents.size());
	}

	@Test
	public void testGetJoinedEventListZeroEvents() throws JsonProcessingException {

		HttpEntity<String> requestEntity = signInAndCheckSession("existed@email.com", "password");

		RESTPaginatedResourcesResponseData<Event> resourceResponseData = getPaginatedResponse("http://localhost:8888/rest/events/userJoined", requestEntity);

		// Make sure event returned
		List<Event> returnedEvents = resourceResponseData.getResults();
		assertEquals(0, returnedEvents.size());

	}

	@Test
	public void testGetJoinedEventListNotSignedIn() {
		HttpEntity<String> requestEntity = new HttpEntity<String>(new HttpHeaders());
		RESTPaginatedResourcesResponseData<Event> resourceResponseData = getPaginatedResponse("http://localhost:8888/rest/events/userOwned", requestEntity);
		assertEquals("Incorrect User State. Only registered users can access /rest/events/userOwned ",
				resourceResponseData.getMessage());
	}

	@Test
	public void testGetOwnedEventListSingleEvent() throws JsonProcessingException {
		
		Registrant user = this.regRepo.findOneByEmail("existed@email.com");
		
		Event event1 = createSingleTestEvent();
		event1.addOwner(user);
		this.eventRepo.save(event1);

		HttpEntity<String> requestEntity = signInAndCheckSession("existed@email.com", "password");

		RESTPaginatedResourcesResponseData<Event> resourceResponseData = getPaginatedResponse("http://localhost:8888/rest/events/userOwned", requestEntity);

		// Make sure event returned
		List<Event> returnedEvents = resourceResponseData.getResults();
		assertEquals(1, returnedEvents.size());

		// Check event
		Event event = returnedEvents.get(0);
		assertEquals("Event1", event.getName());
	}

	@Test
	public void testGetOwnedEventListMultipleEvents() throws JsonProcessingException {
		
		Registrant user = this.regRepo.findOneByEmail("existed@email.com");
		
		List<Event> events = createTestEvents(3);
		addOwner(events, user);
		
		this.eventRepo.save(events);

		HttpEntity<String> requestEntity = signInAndCheckSession("existed@email.com", "password");

		RESTPaginatedResourcesResponseData<Event> resourceResponseData = getPaginatedResponse("http://localhost:8888/rest/events/userOwned", requestEntity);

		// Make sure all 3 events returned
		List<Event> returnedEvents = resourceResponseData.getResults();
		assertEquals(3, returnedEvents.size());
	}

	@Test
	public void testGetOwnedEventListManyEvents() throws JsonProcessingException {
		Category swim = this.categoryRepo.findByName("Swim").get(0);
		assertTrue(swim != null);

		Registrant user = this.regRepo.findOneByEmail("existed@email.com");

		// Create events, add participant
		List<Event> eventsToSave = new ArrayList<Event>();
		for (int i = 0; i < 35; i++) {
			Event event = new Event("Event" + i);
			event.setCategory(swim);
			event.addOwner(user);
			eventsToSave.add(event);
		}

		// Save events to DB
		this.eventRepo.save(eventsToSave);

		HttpEntity<String> requestEntity = signInAndCheckSession("existed@email.com", "password");

		RESTPaginatedResourcesResponseData<Event> resourceResponseData = getPaginatedResponse("http://localhost:8888/rest/events/userOwned", requestEntity);

		// Make sure all events are returned
		assertEquals(35, resourceResponseData.getCount());

		// Make sure paginated data only returns the first 20 (as indicated by
		// RESTPaginatedResourceResponseData)
		List<Event> returnedEvents = resourceResponseData.getResults();
		assertEquals(20, returnedEvents.size());

		// Check second page for remaining 15
		resourceResponseData = getPaginatedResponse("http://localhost:8888/rest/events/userOwned?page=2", requestEntity);
		returnedEvents = resourceResponseData.getResults();
		assertEquals(15, returnedEvents.size());
	}

	@Test
	public void testGetOwnedEventListZeroEvents() throws JsonProcessingException {

		HttpEntity<String> requestEntity = signInAndCheckSession("existed@email.com", "password");

		RESTPaginatedResourcesResponseData<Event> resourceResponseData = getPaginatedResponse("http://localhost:8888/rest/events/userOwned", requestEntity);

		// Make sure event returned
		List<Event> returnedEvents = resourceResponseData.getResults();
		assertEquals(0, returnedEvents.size());

	}

	@Test
	public void testGetOwnedEventListNotSignedIn() {
		HttpEntity<String> requestEntity = new HttpEntity<String>(new HttpHeaders());
		RESTPaginatedResourcesResponseData<Event> resourceResponseData = getPaginatedResponse("http://localhost:8888/rest/events/userOwned", requestEntity);
		assertEquals("Incorrect User State. Only registered users can access /rest/events/userOwned ",
				resourceResponseData.getMessage());
	}
	
	private RESTPaginatedResourcesResponseData<Event> getPaginatedResponse(String url, HttpEntity<String> requestEntity){
		ResponseEntity<String> responseStr = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
		return parsePaginatedEventResponseData(responseStr.getBody());
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

		// Invoking the API
		@SuppressWarnings("unchecked")
		ResponseEntity<RESTResponseData> result = restTemplate.exchange("http://localhost:8888/rest/registrants/signin",
				HttpMethod.POST, httpEntity, Map.class, Collections.EMPTY_MAP);

		assertNotNull(result);
		return result;

	}
	
	private ResponseEntity<RESTResponseData> checkSession(HttpEntity<String> requestEntity) throws JsonProcessingException {
		// Invoking the API
		ResponseEntity<RESTResponseData> response = restTemplate.exchange("http://localhost:8888/rest/session",
				HttpMethod.GET, requestEntity, RESTResponseData.class);

		assertNotNull(response);
		return response;

	}
	
	private HttpEntity<String> signInAndCheckSession(String email, String password) throws JsonProcessingException {
		// Sign in
		ResponseEntity<RESTResponseData> signInResponse = authenticateUser(email, password);
		List<String> cookies = signInResponse.getHeaders().get("Set-Cookie");

		// Check session
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Cookie", StringUtils.join(cookies, ';'));
		HttpEntity<String> requestEntity = new HttpEntity<String>(requestHeaders);
		ResponseEntity<RESTResponseData> response = checkSession(requestEntity);
		assertTrue(response.getStatusCode().equals(HttpStatus.OK));
		RESTResponseData responseData = response.getBody();
		assertTrue(responseData.getMessage().equals("Session Found"));

		// Return validated requestEntity
		return requestEntity;
	}
	
	private Map<String, Object> postRequestBodyForObject(HttpHeaders header, Map<String, Object> requestBody, String url)
			throws JsonProcessingException {
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Cookie", header.getFirst("Cookie"));
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);

		// Creating http entity object with request body and headers
		HttpEntity<String> httpEntity = new HttpEntity<String>(OBJECT_MAPPER.writeValueAsString(requestBody),
				requestHeaders);

		// Invoking the API
		@SuppressWarnings("unchecked")
		Map<String, Object> apiResponse = restTemplate.postForObject(url,
				httpEntity, Map.class, Collections.EMPTY_MAP);
		return apiResponse;
	}
	
	private RESTPaginatedResourcesResponseData<Event> parsePaginatedEventResponseData(String json) {
		Gson gson = GsonHelper.getGson();
		Type resourceType = new TypeToken<RESTPaginatedResourcesResponseData<Event>>() {
		}.getType();
		return gson.fromJson(json, resourceType);
	}

	private RESTResourceResponseData<Event> parseEventResponseData(String json) {
		Gson gson = GsonHelper.getGson();
		Type resourceType = new TypeToken<RESTResourceResponseData<Event>>() {
		}.getType();
		return gson.fromJson(json, resourceType);
	}

	private Coordinates getTestCoordinates() {
		Coordinates coor = new Coordinates();
		coor.setLatitude(12.342);
		coor.setLongitude(111.232);
		return coor;
	}

	private Category getCategory(String name){
		Category cat = this.categoryRepo.findByName(name).get(0);
		assertTrue(cat != null);
		return cat;
	}
	
	private Event createSingleTestEvent(){
		Category testCat = getCategory("Test");
		Coordinates testCoords = getTestCoordinates();
		
		Event event = new Event("Event1");
		event.setCategory(testCat);
		event.setLocation(new Location(testCoords));
		event.addOccurrence(new Occurrence("Single Occurrence",new Timestamp(DateTime.now().plusDays(1).getMillis())));
		return event;
	}
	
	private List<Event> createTestEvents(int numEvents){
		Category testCat = getCategory("Test");
		Coordinates testCoords = getTestCoordinates();
		
		List<Event> events = new ArrayList<Event>();
		for (int i = 1; i <= numEvents; i++) {
			Event event = new Event("Event" + i);
			event.setCategory(testCat);
			event.setLocation(new Location(testCoords));
			event.addOccurrence(new Occurrence("Single Occurrence",new Timestamp(DateTime.now().plusDays(1).getMillis())));
			events.add(event);
		}	
		return events;
	}
	
	private void addParticipant(List<Event> eventList, Registrant participant){
		for(Event e : eventList){
			e.addParticipant(participant);
		}
	}
	
	private void addOwner(List<Event> eventList, Registrant owner){
		for(Event e : eventList){
			e.addOwner(owner);
		}
	}
}
