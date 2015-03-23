package org.opensrp.web.controller;

import org.junit.Before;
import org.opensrp.connector.openmrs.service.LocationService;
import org.opensrp.connector.openmrs.service.UserService;

public class SecurityManagerTest {

	private UserService userservice;
	private LocationService locationservice;

	private UserController securityManager;
	private String openmrsOpenmrsUrl = "http://46.101.51.199:8080/openmrs/";
	private String openmrsUsername = "admin";
	private String openmrsPassword = "5rpAdmin";
	
	@Before
    public void setUp() throws Exception {
		this.userservice = new UserService();
		this.locationservice = new LocationService();

    //    this.securityManager = new SecurityManagerController(userservice, locationservice);
	}
	
	/*@Test
	public void testAuthentication() throws JSONException{
		ResponseEntity<Map<String, Object>> m = securityManager.authenticate();
		assertTrue(((User)m.getBody().get("user")).getUsername().equalsIgnoreCase("admin"));
		assertTrue(((Location)m.getBody().get("location")).getName().equalsIgnoreCase("testloc"));
	}*/
}
