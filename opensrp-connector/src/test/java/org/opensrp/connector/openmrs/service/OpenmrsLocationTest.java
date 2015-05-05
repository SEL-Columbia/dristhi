package org.opensrp.connector.openmrs.service;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.api.domain.Location;
import org.opensrp.api.util.LocationTree;

import com.google.gson.Gson;

public class OpenmrsLocationTest extends TestResourceLoader{
	public OpenmrsLocationTest() throws IOException {
		super();
	}

	OpenmrsLocationService ls;

	@Before
	public void setup() throws IOException{
		ls = new OpenmrsLocationService(openmrsOpenmrsUrl, openmrsUsername, openmrsPassword);
	}
	
	@Test
	public void testLocation() throws JSONException {
		Location l = ls.getLocation("unknown location");
		assertNotNull(l);
	}
	
	@Test
	public void testLocationTree() throws JSONException {
		LocationTree ltree = ls.getLocationTree();
		String ltreestr = new Gson().toJson(ltree, LocationTree.class);
		System.out.println(ltreestr);
		assertNotNull(ltreestr);
		LocationTree lconverted = new Gson().fromJson(ltreestr, LocationTree.class);
		assertNotNull(lconverted);
		String lconvertedstr = new Gson().toJson(lconverted, LocationTree.class);
		System.out.println(lconvertedstr);
		assertEquals(ltreestr, lconvertedstr);
	}
	
	@Test
	public void testLocationTreeOf() throws JSONException {
		LocationTree l = ls.getLocationTreeOf("unknown location");
		assertNotNull(l);
	}
}
