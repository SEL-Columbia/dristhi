package org.opensrp.connector.openmrs.service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.domain.Address;
import org.opensrp.domain.Client;


public class PatientTest extends TestResourceLoader{
	PatientService s;

	public PatientTest() throws IOException {
		super();
	}
	
	@Before
	public void setup(){
		openmrsOpenmrsUrl="http://localhost:8181/openmrs/";
		openmrsUsername="admin";
		openmrsPassword="Admin123";
		pushToOpenmrsForTest = true;
		s = new PatientService(openmrsOpenmrsUrl, openmrsUsername, openmrsPassword);
	}
	
	@Test
	public void shouldCreatePerson() throws JSONException {
		List<Address> addresses = new ArrayList<>();
		addresses.add(new Address("BIRTH", new Date(), new Date(), null, "LAT", "LON", "PCODE", "SINDH", "PK"));
		addresses.add(new Address("DEATH", new Date(), new Date(), null, "LATd", "LONd", "dPCODE", "KPK", "PK"));
		Map<String, Object> attribs = new HashMap<>();
		//attribs.put("Household ID", "HH112");
		Client c = new Client(UUID.randomUUID().toString())
			.withFirstName("FN")
			.withMiddleName("MN")
			.withLastName("LN")
			.withBirthdate(new DateTime(), true)
			.withDeathdate(new DateTime(), false)
			.withGender("MALE");
		
		c.withAddresses(addresses)
			.withAttributes(attribs )
			//.withIdentifier("Birth Reg Num", "b-8912819"+new Random().nextInt(99))
			//.withIdentifier("Death Reg Num", "d-ewj-js3u2"+new Random().nextInt(99))
			;
		if(pushToOpenmrsForTest){
			if(s.getPatientByIdentifier(c.getBaseEntityId()) == null)
			System.out.println(s.createPatient(c));
		}
	}
}
