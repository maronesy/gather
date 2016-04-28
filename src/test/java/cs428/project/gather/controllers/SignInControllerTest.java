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
import org.springframework.http.ResponseEntity;
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
public class SignInControllerTest extends ControllerTest {

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
		ResponseEntity<RESTResponseData> apiResponse = authenticateUser("existed@email.com", "password", null);
		RESTResponseData responseData = apiResponse.getBody();
		assertEquals("success", responseData.getMessage());
		assertEquals(0, responseData.getSTATUS()); //success
	}

	@Test
	public void testSignInUserWrongPassword() throws JsonProcessingException {
		ResponseEntity<RESTResponseData> apiResponse = authenticateUser("existed@email.com", "wrongpassword", null);
		RESTResponseData responseData = apiResponse.getBody();
		assertEquals("The password is incorrect.  Please enter the correct password. ", responseData.getMessage());
		assertEquals(-6, responseData.getSTATUS());
	}

	@Test
	public void testSignInInvalidEmail() throws JsonProcessingException {
		ResponseEntity<RESTResponseData> apiResponse = authenticateUser(".Thi$IsN0TaEM@il", "password", null);
		RESTResponseData responseData = apiResponse.getBody();
		assertEquals("Field invalid-email:Please enter a valid email address. ", responseData.getMessage());
		assertEquals(-3, responseData.getSTATUS());
	}

	@Test
	public void testSignInInvalidPassword() throws JsonProcessingException {
		ResponseEntity<RESTResponseData> apiResponse = authenticateUser("existed@email.com", "ThisPasswordIsTooLongAndItShouldNotBeAnValidPasswordSoTheTestShouldFail", null);
		RESTResponseData responseData = apiResponse.getBody();
		assertEquals("Field invalid-password:The password length must be 64 characters or less. ", responseData.getMessage());
		assertEquals(-2,  responseData.getSTATUS());
	}

	@Test
	public void testSignInUserNotExist() throws JsonProcessingException {
		ResponseEntity<RESTResponseData> apiResponse = authenticateUser("notexisted@email.com", "password", null);
		RESTResponseData responseData = apiResponse.getBody();
		assertEquals("Field invalid-email:The email address doesn't exist.  Please enter another email address. ", responseData.getMessage());
		assertEquals(-5, responseData.getSTATUS());
	}
	
	@Test
	public void testSignInAlreadySignedIn() throws JsonProcessingException{
		HttpEntity<String> requestEntity = signInAndCheckSession("existed@email.com", "password");
		ResponseEntity<RESTResponseData> apiResponse = authenticateUser("existed@email.com", "password", requestEntity.getHeaders());
		RESTResponseData responseData = apiResponse.getBody();
		assertEquals("Incorrect User State. Only non-registered users can access /rest/registrants/signin ", responseData.getMessage());
		assertEquals(-7, responseData.getSTATUS()); 
	}

}
