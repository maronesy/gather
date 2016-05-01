package cs428.project.gather.data.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cs428.project.gather.GatherApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(GatherApplication.class)
public class ActorTypeUnitTest {
	
	@Test
	public void testActorTypeInteraction(){
		ActorType type = ActorType.ANONYMOUS_USER;
		assertEquals(type.getValue(), "anonymousUser");
		type = ActorType.fromValue("registeredUser");
		assertEquals(type.getValue(), ActorType.REGISTERED_USER.getValue());
		assertEquals(type.toString(), "registeredUser");
	}

}
