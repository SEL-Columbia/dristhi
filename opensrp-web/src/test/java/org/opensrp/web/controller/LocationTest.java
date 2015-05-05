package org.opensrp.web.controller;

import java.io.IOException;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.connector.openmrs.service.OpenmrsLocationService;
import org.springframework.http.ResponseEntity;

import com.google.gson.Gson;

public class LocationTest extends TestResourceLoader{

	public LocationTest() throws IOException {
		super();
	}

	private OpenmrsLocationService locationservice;
	private LocationController controller;

	@Before
    public void setUp() throws Exception {
		this.locationservice = new OpenmrsLocationService(openmrsOpenmrsUrl, openmrsUsername, openmrsPassword);
        this.controller = new LocationController(locationservice);
	}
	
	@Test
	public void testLocationTree() throws JSONException{
		ResponseEntity<String> l = controller.getLocationTree();
		System.out.println(new Gson().toJson(l));
		//assertNotNull(l.getBody().findLocation("60c21502-fec1-40f5-b77d-6df3f92771ce"));		
		//assertTrue(l.getBody().findLocation("60c21502-fec1-40f5-b77d-6df3f92771ce").getName().equalsIgnoreCase("testloc4"));
	}
	
	@Test
	public void testLocationTreeWithId() throws JSONException{
		ResponseEntity<String> l = controller.getLocationTree("60c21502-fec1-40f5-b77d-6df3f92771ce");
		//assertNotNull(l.getBody().findLocation("60c21502-fec1-40f5-b77d-6df3f92771ce"));		
		//assertTrue(l.getBody().findLocation("60c21502-fec1-40f5-b77d-6df3f92771ce").getName().equalsIgnoreCase("testloc4"));
	}
	
}
