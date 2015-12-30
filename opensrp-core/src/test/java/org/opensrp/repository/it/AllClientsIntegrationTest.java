package org.opensrp.repository.it;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Date;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensrp.common.Gender;
import org.opensrp.domain.Address;
import org.opensrp.domain.BaseEntity;
import org.opensrp.domain.Client;
import org.opensrp.repository.AllClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-opensrp.xml")
public class AllClientsIntegrationTest {
//TODO detailed testign
	
	@Autowired
	private AllClients allClients;

	@Before
	public void setUp() throws Exception {
		allClients.removeAll();
		initMocks(this);
	}

	@Test
	public void shouldFetchClientByIdentifier()
	{
		String baseEntityId = "testclient2";
		Client c = new Client(baseEntityId)
			.withBirthdate(new Date(), false)
			.withFirstName("C first n")
			.withLastName("C last n")
			.withMiddleName("C middle n")
			.withGender(Gender.MALE);
		c.withAddress(new Address("birthplace", new Date(System.currentTimeMillis()-1000*60*60*24*2), new Date(), null, "lat", "lon", "75210", "Sindh", "Pakistan"));
		c.withAttribute("ETHNICITY", "Mughal");
		c.withIdentifier("Program ID", "01001222");
		
		allClients.add(c);
		
		Client ce = allClients.findByBaseEntityId("testclient2");
		assertEquals("testclient2", ce.getBaseEntityId());
		assertTrue(Client.class.getSimpleName().equals(ce.type()));
		assertEquals("birthplace", ce.getAddresses().get(0).getAddressType());
		assertEquals("Mughal", ce.getAttribute("ethnicity"));
		assertEquals("01001222", ce.getIdentifier("program id"));
		
		List<Client> ce2 = allClients.findAllByIdentifier("01001222");
		assertTrue(ce2.size() == 1);
		assertEquals("testclient2", ce2.get(0).getBaseEntityId());
		
		List<Client> ce3 = allClients.findAllByIdentifier("Program ID", "01001222");
		assertTrue(ce3.size() == 1);
		assertEquals("testclient2", ce3.get(0).getBaseEntityId());
	}
	
	@Test
	public void shouldFetchClientByAttribute()
	{
		String baseEntityId = "testclient2";
		Client c = new Client(baseEntityId)
			.withBirthdate(new Date(), false)
			.withFirstName("C first n")
			.withLastName("C last n")
			.withMiddleName("C middle n")
			.withGender(Gender.MALE);
		c.withAddress(new Address("birthplace", new Date(System.currentTimeMillis()-1000*60*60*24*2), new Date(), null, "lat", "lon", "75210", "Sindh", "Pakistan"));
		c.withAttribute("ETHNICITY", "Mughal");
		c.withIdentifier("Program ID", "01001222");
		
		allClients.add(c);
		
		c = new Client("testclient3")
		.withBirthdate(new Date(), false)
		.withFirstName("C first n")
		.withLastName("C last n")
		.withMiddleName("C middle n")
		.withGender(Gender.MALE);
		c.withAttribute("ETHNICITY", "Mughal");
		c.addIdentifier("Program ID", "01001223");
		
		allClients.add(c);
		
		List<Client> ce = allClients.findAllByAttribute("ETHNICITY", "Mughal");
		assertTrue(ce.size() == 2);
		assertThat(ce, Matchers.<Client>hasItem(Matchers.<Client>hasProperty("baseEntityId",equalTo("testclient2"))));
		assertThat(ce, Matchers.<Client>hasItem(Matchers.<Client>hasProperty("baseEntityId",equalTo("testclient3"))));
	}
	
}
