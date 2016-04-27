package cs428.project.gather.controllers;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cs428.project.gather.GatherApplication;
import cs428.project.gather.data.form.RegistrationData;
import cs428.project.gather.data.model.Registrant;
import cs428.project.gather.data.repo.EventRepository;
import cs428.project.gather.data.repo.RegistrantRepository;
import cs428.project.gather.data.response.RESTResourceResponseData;
import cs428.project.gather.utilities.GsonHelper;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(GatherApplication.class)
@WebIntegrationTest
public class RegistrantsControllerTest {

	private ObjectMapper OBJECT_MAPPER = ControllerTestHelper.OBJECT_MAPPER;
	private RestTemplate restTemplate = ControllerTestHelper.restTemplate;

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
		Registrant aUser = new Registrant("existed@email.com", "password", "existedName", 3, 10000);
		this.registrantRepo.save(aUser);
		assertEquals(this.registrantRepo.count(), 1);
	}

	@Test
	public void testRegisterNewUser() throws JsonProcessingException {
		Map<String, Object> apiResponse = attemptAddUser("newEmail@email.com", "QWER1234", "testingNewUser");
		String message = apiResponse.get("message").toString();
		Integer status = (Integer) (apiResponse.get("status"));
		assertEquals("success", message);
		assertEquals((Integer)0, status); //success

		// Fetching the Registrant details directly from the DB to verify the
		// API succeeded
		Registrant aUser = this.registrantRepo.findByDisplayName("testingNewUser");
		assertTrue(aUser!=null);
		assertEquals("testingNewUser", aUser.getDisplayName());
		assertEquals("newEmail@email.com", aUser.getEmail());
		assertEquals("QWER1234", aUser.getPassword());

	}

	@Test
	public void testRegisterDuplicatedUserEmail() throws JsonProcessingException {

		Map<String, Object> apiResponse = attemptAddUser("existed@email.com", "QWER1234", "testingNewUser");
		String message = apiResponse.get("message").toString();
		assertEquals("Field invalid-email:The email address already exists.  Please enter another email address. ",
				message);
		Integer status = (Integer) (apiResponse.get("status"));
		assertEquals((Integer)(-4), status); //duplicated entries

		// Fetching the Registrant details directly from the DB to verify the
		// API succeeded
		Registrant testUser = this.registrantRepo.findByDisplayName("testingNewUser");
		assertTrue(testUser==null);

	}

	@Test
	public void testRegisterDuplicatedUserDisplayName() throws JsonProcessingException {

		Map<String, Object> apiResponse = attemptAddUser("newEmail@email.com", "QWER1234", "existedName");
		String message = apiResponse.get("message").toString();
		assertEquals("Field invalid-displayName:The display name already exists.  Please enter another display name. ",
				message);
		Integer status = (Integer) (apiResponse.get("status"));
		assertEquals((Integer)(-4), status); //duplicated entries

		// Fetching the Registrant details directly from the DB to verify nothing added
		assertEquals(1,this.registrantRepo.count());
	}

	@Test
	public void testRegisterDisplayNameTooLong() throws JsonProcessingException {

		Map<String, Object> apiResponse = attemptAddUser("newEmail@email.com", "QWER1234", "ThisDisplayNameIsTooLongAndItShouldNotBeAnValidUserNameSoTheTestShouldFail");
		String message = apiResponse.get("message").toString();
		assertEquals("Field invalid-displayName:The display name length must be 64 characters or less. ",
				message);
		Integer status = (Integer) (apiResponse.get("status"));
		assertEquals((Integer)(-2), status); //multiple error messages

		// Fetching the Registrant details directly from the DB to verify nothing added
		assertEquals(1,this.registrantRepo.count());
	}

	@Test
	public void testRegisterInvalidEmailAddress() throws JsonProcessingException {

		Map<String, Object> apiResponse = attemptAddUser(".Thi$IsN0TaEM@il", "QWER1234", "newUser");
		String message = apiResponse.get("message").toString();
		assertEquals("Field invalid-email:Please enter a valid email address. ",
				message);
		Integer status = (Integer) (apiResponse.get("status"));
		assertEquals((Integer)(-3), status); //multiple error messages

		// Fetching the Registrant details directly from the DB to verify nothing added
		assertEquals(1,this.registrantRepo.count());
	}

	private Map<String, Object> attemptAddUser(String email, String password, String displayName) throws JsonProcessingException {
		// Building the Request body data
		Map<String, Object> requestBody = new HashMap<String, Object>();
		requestBody.put("email", email);
		requestBody.put("password", password);
		requestBody.put("displayName", displayName);
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);

		// Creating http entity object with request body and headers
		HttpEntity<String> httpEntity = new HttpEntity<String>(OBJECT_MAPPER.writeValueAsString(requestBody),
				requestHeaders);

		// Invoking the API
		@SuppressWarnings("unchecked")
		Map<String, Object> apiResponse = restTemplate.postForObject("http://localhost:8888/rest/registrants", httpEntity,
				Map.class, Collections.EMPTY_MAP);

		assertNotNull(apiResponse);

		// Asserting the response of the API.
		return apiResponse;

	}
	
	@Test
	public void testGetRegistrant() throws JsonProcessingException{
		HttpEntity<String> requestEntity = ControllerTestHelper.signInAndCheckSession("existed@email.com", "password");
		
		//Controller should be changed to simple GET instead of a PUT, then none of this is necessary
		Map<String, Object> requestBody = new HashMap<String, Object>();
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Cookie", requestEntity.getHeaders().getFirst("Cookie"));
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> httpEntity = new HttpEntity<String>(OBJECT_MAPPER.writeValueAsString(requestBody),
				requestHeaders);
		
		ResponseEntity<String> responseStr = restTemplate.exchange("http://localhost:8888/rest/registrants/info", HttpMethod.PUT, httpEntity, String.class);
		
		RESTResourceResponseData<Registrant> responseData = parseRegistrantResponseData(responseStr.getBody());
		Registrant user = responseData.getResult();
		
		assertEquals("existedName", user.getDisplayName());
		assertEquals(10000, user.getDefaultZip());
	}
	
	private RESTResourceResponseData<Registrant> parseRegistrantResponseData(String json) {
		Gson gson = GsonHelper.getGson();
		Type resourceType = new TypeToken<RESTResourceResponseData<Registrant>>() {
		}.getType();
		return gson.fromJson(json, resourceType);
	}
	
	@Test
	public void testUpdateUser() throws JsonProcessingException {
		HttpEntity<String> requestEntity = ControllerTestHelper.signInAndCheckSession("existed@email.com", "password");
		
		Registrant user = registrantRepo.findOneByEmail("existed@email.com");
		
		//Build form data
		RegistrationData requestBody = new RegistrationData();
		requestBody.setDefaultTimeWindow(20);
		requestBody.setDefaultZip(11111);
		requestBody.setDisplayName("newDisplayName");
		requestBody.setOldPassword("password");
		requestBody.setPassword("newpassword");
		requestBody.setEmail("new@email.com");
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Cookie", requestEntity.getHeaders().getFirst("Cookie"));
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> httpEntity = new HttpEntity<String>(OBJECT_MAPPER.writeValueAsString(requestBody),
				requestHeaders);
		
		ResponseEntity<String> responseStr = restTemplate.exchange("http://localhost:8888/rest/registrants/update", HttpMethod.PUT, httpEntity, String.class);
		
		RESTResourceResponseData<Registrant> responseData = parseRegistrantResponseData(responseStr.getBody());
		Registrant frontendUser = responseData.getResult();
		
		//Check user data returned to frontend
		assertEquals("newDisplayName", frontendUser.getDisplayName());
		assertEquals(11111, frontendUser.getDefaultZip());
		
		//Check data in DB
		Registrant backendUser = registrantRepo.findOne(user.getActorID());
		assertEquals("newDisplayName", frontendUser.getDisplayName());
		assertEquals(11111, frontendUser.getDefaultZip());
	}

}
