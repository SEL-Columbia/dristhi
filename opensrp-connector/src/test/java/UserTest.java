import org.json.JSONException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.opensrp.connector.openmrs.UserService;


public class UserTest {

	@Test
	public void testAuthentication() throws JSONException {
		String openmrsOpenmrsUrl = "http://46.101.51.199:8080/openmrs/";
		String openmrsUsername = "admin";
		String openmrsPassword = "5rpAdmin";
		UserService ls = new UserService(openmrsOpenmrsUrl, openmrsUsername, openmrsPassword);
		assertTrue(ls.authenticate(openmrsUsername, openmrsPassword));
	}
	
	@Test
	public void testUser() throws JSONException {
		String openmrsOpenmrsUrl = "http://46.101.51.199:8080/openmrs/";
		String openmrsUsername = "admin";
		String openmrsPassword = "5rpAdmin";
		UserService ls = new UserService(openmrsOpenmrsUrl, openmrsUsername, openmrsPassword);
		assertTrue(ls.getUser("test2").getUsername().equalsIgnoreCase("test2"));
	}
}
