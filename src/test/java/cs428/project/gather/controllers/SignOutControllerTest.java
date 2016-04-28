package cs428.project.gather.controllers;


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
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cs428.project.gather.GatherApplication;
import cs428.project.gather.data.model.*;
import cs428.project.gather.data.repo.*;
import cs428.project.gather.data.response.*;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		Registrant aUser = new Registrant("existed@email.com", "password", "existedName", 3, 10000);
		this.registrantRepo.save(aUser);
		assertEquals(this.registrantRepo.count(), 1);
	}

	@Test
	public void testSignInUserSuccess() throws JsonProcessingException {

		ResponseEntity<RESTResponseData> signInResponse = authenticateUser("existed@email.com", "password");
		List<String> cookies = signInResponse.getHeaders().get("Set-Cookie");

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Cookie",StringUtils.join(cookies,';'));
		HttpEntity<String> requestEntity = new HttpEntity<String>(requestHeaders);

		// Invoking the API

		ResponseEntity<RESTResponseData> response = signOutUser(requestEntity);
		assertTrue(response.getStatusCode().equals(HttpStatus.OK));

		RESTResponseData responseData = response.getBody();
		assertTrue(responseData.getMessage().equals("success"));

	}

	@Test
	public void testSignOutUserFail() throws IOException {
		HttpHeaders requestHeaders = new HttpHeaders();
		HttpEntity<String> requestEntity = new HttpEntity<String>(requestHeaders);
		ResponseEntity<RESTResponseData> response = signOutUser(requestEntity);
		assertTrue(response.getStatusCode().equals(HttpStatus.BAD_REQUEST));

		RESTResponseData responseData = response.getBody();
		assertTrue(responseData.getMessage().equals("User is not in authenticated state"));

	}

	private ResponseEntity<RESTResponseData> signOutUser(HttpEntity<String> requestEntity) throws JsonProcessingException {

		// Invoking the API

		ResponseEntity<RESTResponseData> response = restTemplate.exchange("http://localhost:8888/rest/registrants/signout", HttpMethod.POST, requestEntity, RESTResponseData.class);

		assertNotNull(response);

		// Asserting the response of the API.
		return response;

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

		return result;

	}

}
