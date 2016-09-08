package org.opensrp.repository.it;

import static org.mockito.MockitoAnnotations.initMocks;
import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.ektorp.CouchDbConnector;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensrp.common.AllConstants;
import org.opensrp.common.Gender;
import org.opensrp.domain.Address;
import org.opensrp.domain.BaseEntity;
import org.opensrp.domain.Client;
import org.opensrp.repository.AllBaseEntities;
import org.opensrp.repository.AllClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-opensrp.xml")
public class AllBaseEntitiesIntegrationTest {

	@Autowired
	@Qualifier(AllConstants.OPENSRP_DATABASE_CONNECTOR)
	CouchDbConnector db;

	@Autowired
	private AllBaseEntities allBaseEntities;
	@Autowired
	private AllClients allClients;

	@Before
	public void setUp() throws Exception {
		allClients.removeAll();
		initMocks(this);
	}

	@Test
	public void shouldFetchBaseEntityForClientData() throws Exception {
		String baseEntityId = "testclient1";
		Client c = new Client(baseEntityId)
			.withBirthdate(new DateTime(), false)
			.withFirstName("C first n")
			.withLastName("C last n")
			.withMiddleName("C middle n")
			.withGender(Gender.FEMALE);
		c.withAddress(new Address("birthplace", new DateTime(System.currentTimeMillis()-1000*60*60*24*2), DateTime.now(), null, "lat", "lon", "75210", "Sindh", "Pakistan"));
		c.withAttribute("ETHNICITY", "Mughal");
		c.withIdentifier("Program ID", "01001221");
		
		allClients.add(c);
		
		BaseEntity be = allBaseEntities.findByBaseEntityId("testclient1");
		assertEquals("testclient1", be.getBaseEntityId());
		assertTrue(Client.class.getSimpleName().equals(be.type()));
		assertEquals("birthplace", be.getAddresses().get(0).getAddressType());
		assertEquals("Mughal", be.getAttribute("ethnicity"));
		assertEquals("01001221", be.getIdentifier("program id"));
		
		List<BaseEntity> be2 = allBaseEntities.findAllByIdentifier("01001221");
		assertTrue(be2.size() == 1);
		assertEquals("testclient1", be2.get(0).getBaseEntityId());
		
		List<BaseEntity> be3 = allBaseEntities.findAllByIdentifier("Program ID", "01001221");
		assertTrue(be3.size() == 1);
		assertEquals("testclient1", be3.get(0).getBaseEntityId());
	}
	
	@Test
	public void shouldFetchBaseEntityByIdentifier() throws Exception {
		String baseEntityId = "testclient2";
		Client c = new Client(baseEntityId)
			.withBirthdate(new DateTime(), false)
			.withFirstName("C first n")
			.withLastName("C last n")
			.withMiddleName("C middle n")
			.withGender(Gender.MALE);
		c.withAddress(new Address("birthplace", new DateTime(System.currentTimeMillis()-1000*60*60*24*2), DateTime.now(), null, "lat", "lon", "75210", "Sindh", "Pakistan"));
		c.withAttribute("ETHNICITY", "Mughal");
		c.withIdentifier("Program ID", "01001222");
		
		allClients.add(c);
		
		BaseEntity be = allBaseEntities.findByBaseEntityId("testclient2");
		assertEquals("testclient2", be.getBaseEntityId());
		assertTrue(Client.class.getSimpleName().equals(be.type()));
		assertEquals("01001222", be.getIdentifier("program id"));
		
		List<BaseEntity> be2 = allBaseEntities.findAllByIdentifier("01001222");
		assertTrue(be2.size() == 1);
		assertEquals("testclient2", be2.get(0).getBaseEntityId());
		
		List<BaseEntity> be3 = allBaseEntities.findAllByIdentifier("Program ID", "01001222");
		assertTrue(be3.size() == 1);
		assertEquals("testclient2", be3.get(0).getBaseEntityId());
	}

}
