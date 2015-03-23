import static org.junit.Assert.assertTrue;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.connector.openmrs.service.UserService;


public class UserTest {

	String openmrsOpenmrsUrl = "http://46.101.51.199:8080/openmrs/";
	String openmrsUsername = "admin";
	String openmrsPassword = "5rpAdmin";
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
		assertTrue(ls.getUser("test2").getUsername().equalsIgnoreCase("test2"));
	}
}
