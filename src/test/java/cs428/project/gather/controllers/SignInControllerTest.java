package cs428.project.gather.controllers;

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
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cs428.project.gather.GatherApplication;
import cs428.project.gather.data.model.*;
import cs428.project.gather.data.repo.*;
import cs428.project.gather.data.response.*;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(GatherApplication.class)
@WebIntegrationTest
public class SignInControllerTest {

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
		Registrant aUser = new Registrant("existed@email.com", "password", "existedName", 3, 10000);
		this.registrantRepo.save(aUser);
		assertEquals(this.registrantRepo.count(), 1);
	}

	@Test
	public void testSignInUserSuccess() throws JsonProcessingException {
		Map<String, Object> apiResponse = authenticateUser("existed@email.com", "password");
		String message = apiResponse.get("message").toString();
		String displayName = apiResponse.get("displayName").toString();
		Integer status = (Integer) (apiResponse.get("status"));
		assertEquals("success", message);
		assertEquals("existedName", displayName);
		assertEquals((Integer)0, status); //success
	}

	@Test
	public void testSignInUserWrongPassword() throws JsonProcessingException {
		Map<String, Object> apiResponse = authenticateUser("existed@email.com", "wrongpassword");
		String message = apiResponse.get("message").toString();
		Integer status = (Integer) (apiResponse.get("status"));
		assertEquals("The password is incorrect.  Please enter the correct password. ", message);
		assertEquals((Integer)(-6), status); //success
	}

	@Test
	public void testSignInInvalidEmail() throws JsonProcessingException {
		Map<String, Object> apiResponse = authenticateUser(".Thi$IsN0TaEM@il", "password");
		String message = apiResponse.get("message").toString();
		Integer status = (Integer) (apiResponse.get("status"));
		assertEquals("Field invalid-email:Please enter a valid email address. ", message);
		assertEquals((Integer)(-3), status); //success
	}

	@Test
	public void testSignInInvalidPassword() throws JsonProcessingException {
		Map<String, Object> apiResponse = authenticateUser("existed@email.com", "ThisPasswordIsTooLongAndItShouldNotBeAnValidPasswordSoTheTestShouldFail");
		String message = apiResponse.get("message").toString();
		Integer status = (Integer) (apiResponse.get("status"));
		assertEquals("Field invalid-password:The password length must be 64 characters or less. ", message);
		assertEquals((Integer)(-2), status); //success
	}

	@Test
	public void testSignInUserNotExist() throws JsonProcessingException {
		Map<String, Object> apiResponse = authenticateUser("notexisted@email.com", "password");
		String message = apiResponse.get("message").toString();
		Integer status = (Integer) (apiResponse.get("status"));
		assertEquals("Field invalid-email:The email address doesn't exist.  Please enter another email address. ", message);
		assertEquals((Integer)(-5), status); //success
	}

	private Map<String, Object> authenticateUser(String email, String password) throws JsonProcessingException {
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
		Map<String, Object> apiResponse = restTemplate.postForObject("http://localhost:8888/rest/registrants/signin", httpEntity,
				Map.class, Collections.EMPTY_MAP);

		assertNotNull(apiResponse);
		// Asserting the response of the API.
		return apiResponse;

	}

}
