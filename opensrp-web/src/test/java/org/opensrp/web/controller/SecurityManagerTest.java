package org.opensrp.web.controller;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.api.domain.Location;
import org.opensrp.api.domain.User;
import org.opensrp.connector.openmrs.LocationService;
import org.opensrp.connector.openmrs.UserService;
import org.springframework.http.ResponseEntity;

public class SecurityManagerTest {

	private UserService userservice;
	private LocationService locationservice;

	//private SecurityManagerController securityManager;
	private String openmrsOpenmrsUrl = "http://46.101.51.199:8080/openmrs/";
	private String openmrsUsername = "admin";
	private String openmrsPassword = "5rpAdmin";
	
	@Before
    public void setUp() throws Exception {
		this.userservice = new UserService(openmrsOpenmrsUrl, openmrsUsername, openmrsPassword);
		this.locationservice = new LocationService(openmrsOpenmrsUrl, openmrsUsername, openmrsPassword);

    //    this.securityManager = new SecurityManagerController(userservice, locationservice);
	}
	
//	@Test
//	public void testAuthentication() throws JSONException{
//		ResponseEntity<Map<String, Object>> m = securityManager.authenticate();
//		assertTrue(((User)m.getBody().get("user")).getUsername().equalsIgnoreCase("admin"));
//		assertTrue(((Location)m.getBody().get("location")).getName().equalsIgnoreCase("testloc"));
//	}
}
