package cs428.project.gather.controllers;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(HomeController.class)
@WebAppConfiguration
@ActiveProfiles("scratch")

public class HomeControllerTest {

	private MockMvc mvc;
	private MockMvc mvcSignIn;
	private WebApplicationContext webApplicationContext;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		// InternalResourceViewResolver viewResolver = new
		// InternalResourceViewResolver();
		// viewResolver.setPrefix("/resources/templates/");
		// viewResolver.setSuffix(".html");
		mvc = MockMvcBuilders.standaloneSetup(new HomeController()).build();
//		mvcSignIn = MockMvcBuilders.standaloneSetup(new SignInController()).build();
		// mvcSignIn =
		// MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	public void testHome() throws Exception {

		mvc.perform(get("/")).andExpect(status().isOk()).andExpect(view().name("index"));
	}

	// @Test
	// public void testSignin() throws Exception {
	//
	// mvcSignIn.perform(get("/sign-in")).andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
	// .andExpect(view().name("signInController"));
	// //.andExpect(jsonPath("$.username").value("username"));
	// }
	//
	// @Test
	// public void testRegister() throws Exception {
	//
	// mvcSignIn.perform(post("/register")).andExpect(status().isOk()).andExpect(view().name("register"));
	// }
	//
	// @Test
	// public void testRegistrants() throws Exception {
	//
	// mvcSignIn.perform(post("/registrants")).andExpect(status().isOk()).andExpect(view().name("registrants"));
	// }
	//
	// @Test
	// public void testZipcode() throws Exception {
	//
	// mvcSignIn.perform(post("/zipcode")).andExpect(status().isOk()).andExpect(view().name("zipcode"));
	// }
}
