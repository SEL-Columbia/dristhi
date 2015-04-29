import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.connector.openmrs.service.UserService;


public class UserTest extends TestResourceLoader{

	public UserTest() throws IOException {
		super();
	}

	UserService ls;

	@Before
	public void setup(){
		ls = new UserService(openmrsOpenmrsUrl, openmrsUsername, openmrsPassword);
	}
	
	@Test
	public void testAuthentication() throws JSONException {
		assertTrue(ls.authenticate(openmrsUsername, openmrsPassword));
	}
	
	@Test
	public void testUser() throws JSONException {
		assertTrue(ls.getUser("admin").getUsername().equalsIgnoreCase("admin"));
	}
}
