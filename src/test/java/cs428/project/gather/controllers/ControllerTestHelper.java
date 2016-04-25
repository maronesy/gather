package cs428.project.gather.controllers;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cs428.project.gather.data.response.RESTResponseData;

public class ControllerTestHelper {
	
	public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	public static RestTemplate restTemplate = new TestRestTemplate();

	public static ResponseEntity<RESTResponseData> authenticateUser(String email, String password) throws JsonProcessingException {
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
	
	public static ResponseEntity<RESTResponseData> checkSession(HttpEntity<String> requestEntity) throws JsonProcessingException {
		// Invoking the API
		ResponseEntity<RESTResponseData> response = restTemplate.exchange("http://localhost:8888/rest/session",
				HttpMethod.GET, requestEntity, RESTResponseData.class);

		assertNotNull(response);
		return response;

	}
	
	public static HttpEntity<String> signInAndCheckSession(String email, String password) throws JsonProcessingException {
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
}
