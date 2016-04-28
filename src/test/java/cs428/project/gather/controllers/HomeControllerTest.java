package cs428.project.gather.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(HomeController.class)
@WebAppConfiguration
@ActiveProfiles("scratch")

public class HomeControllerTest {

	private MockMvc mvc;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders.standaloneSetup(new HomeController()).build();
	}

	@Test
	public void testHome() throws Exception {
		mvc.perform(get("/")).andExpect(status().isOk()).andExpect(view().name("index"));
	}

	@Test
	public void testRegister() throws Exception {
		mvc.perform(get("/registerform.do")).andExpect(status().isOk()).andExpect(view().name("registerform"));
	}
	
	@Test
	public void testRegistrants() throws Exception {
		mvc.perform(get("/registrants.do")).andExpect(status().isOk()).andExpect(view().name("registrants"));
	}
	
	@Test
	public void testZipcode() throws Exception {
		mvc.perform(get("/zipcode.do")).andExpect(status().isOk()).andExpect(view().name("zipcode"));
	}

}
