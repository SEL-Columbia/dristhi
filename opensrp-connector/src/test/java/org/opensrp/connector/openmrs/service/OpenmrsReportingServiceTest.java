package org.opensrp.connector.openmrs.service;

import java.io.IOException;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

public class OpenmrsReportingServiceTest extends TestResourceLoader{
	public OpenmrsReportingServiceTest() throws IOException {
		super();
	}

	OpenmrsReportingService rs;

	@Before
	public void setup() throws IOException{
		rs = new OpenmrsReportingService(openmrsOpenmrsUrl, openmrsUsername, openmrsPassword);
	}
	
	@Test
	public void testReportDefinition() throws JSONException {
		System.out.println(rs.getReportDefinitions());
	}
	
	@Test
	public void testReportData() throws JSONException {
		System.out.println(rs.getReportData("72cc5cee-4f0d-47e0-a565-038b43a7e37e"));
	}
}
