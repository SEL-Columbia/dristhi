package org.opensrp.repository.it;

import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensrp.domain.Address;
import org.opensrp.repository.AllLocations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-opensrp.xml")
public class AllLocationsIntegrationTest {


	@Autowired
	private AllLocations allLocations;

	@Before
	public void setUp() throws Exception {
		initMocks(this);
	}
	
	@Test
	public void shouldAddLocation() throws Exception
	{
		Map<String, String> addressFields = new HashMap<String, String>();
		addressFields.put("PS","Dimla");
		addressFields.put("Union","Gaya Bari");
		addressFields.put("Ward","Middle Gaya Bari");
		
		Address address = new Address("Permanent", DateTime.now(), DateTime.now(),
				addressFields, "70.5", "40.5", "6300", "", "Bangladesh");
		
		Map<String, String> identifiers = new HashMap<String, String>();
		identifiers.put("identifier-1",
				"FWA0001");
		
		Set<String> tags = new HashSet<String>();
		tags.add("tag1");
		tags.add("tag2");
		
		Map<String, Object> attributes = new HashMap<String, Object>();
		
		attributes.put("attr1", "value1");
		attributes.put("attr2", "value2");


		
		org.opensrp.domain.Location domainLocation = new org.opensrp.domain.Location()
														.withLocationId("10203040")
														.withName("Dimla")
														.withAddress(address)
														.withIdentifiers(identifiers)
														.withAttributes(attributes)
														.withTags(tags);
		
		allLocations.add(domainLocation);
	}
}
