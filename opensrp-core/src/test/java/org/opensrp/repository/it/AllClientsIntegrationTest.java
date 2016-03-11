package org.opensrp.repository.it;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensrp.common.Gender;
import org.opensrp.domain.Address;
import org.opensrp.domain.Client;
import org.opensrp.repository.AllClients;
import org.opensrp.service.ClientService;
import org.opensrp.util.DateTimeTypeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-opensrp.xml")
public class AllClientsIntegrationTest {
//TODO detailed testign
	
	@Autowired
	private ClientService clientService;
	@Autowired
	private AllClients ac;

	@Before
	public void setUp() throws Exception {
		System.out.println("Removing all data");
		ac.removeAll();
		System.out.println("Removed");
		initMocks(this);
		
		FileAppender fa = new FileAppender();
		fa.setName("FileLogger");
		fa.setFile("d:\\opensrp-logger.log");
		fa.setLayout(new PatternLayout("%d %-5p [%c{1}] %m%n"));
		fa.setThreshold(Level.INFO);
		fa.setAppend(true);
		fa.activateOptions();

		// add appender to any Logger (here is root)
		Logger.getRootLogger().addAppender(fa);
	}
	
	private void addClients() {
		for (int i = 0; i < 10; i++) {
			Client c = new Client("eid"+i)
				.withName("fn"+i, "mn"+i, "ln"+i)
				.withGender("MALE")
				.withBirthdate(new DateTime(), false);
			c.withAddress(new Address().withAddressType("usual_residence").withCityVillage("city"+i).withTown("town"+i));
			c.withAttribute("at1", "atval"+i);
			
			clientService.addClient(c);
		}
	}
	
	@Test
	public void shouldUpdateSuccessfullyIfClientFound() throws JSONException {
		Client c = new Client("eid")
			.withName("fn", "mn", "ln")
			.withGender("MALE")
			.withBirthdate(new DateTime(), false);
		c.withAddress(new Address().withAddressType("usual_residence").withCityVillage("city").withTown("town"));
		c.withAttribute("at1", "atval1");
		
		clientService.addClient(c);
		
		Client cu = new Client("eid0")
			.withGender("FEMALE")
			.withBirthdate(new DateTime(), false);
		cu.withAddress(new Address().withAddressType("deathplace").withCityVillage("city").withTown("town"));
		cu.withAttribute("at2", "atval2");

		clientService.updateClient(cu);
	}
	
	@Test
	public void shouldAdd10KFullDataClientsDirectly() {
		Logger.getLogger("FileLogger").info("Starting at "+new DateTime());
		
		final long start = System.currentTimeMillis();
		long last1000 = System.currentTimeMillis();
		
		for (int i = 0; i < 10000; i++) {
			addClient(i, true);
			
			if(i%1000 == 0){
				Logger.getLogger("FileLogger").info("Next thousand entries at "+new DateTime()+" in "+((System.currentTimeMillis()-last1000)/1000)+" sec");
				last1000 = System.currentTimeMillis();
			}
		}
		Logger.getLogger("FileLogger").info("10K entries complete at "+new DateTime()+" in "+((System.currentTimeMillis()-start)/1000)+" sec");
	}
	
	@Test
	public void shouldSearchFullDataClientsIn10Sec() {
		Logger.getLogger("FileLogger").info("Starting at "+new DateTime());
		
		final long start = System.currentTimeMillis();
		
		for (int i = 0; i < 10000; i++) {
			addClient(i, false);
		}
		Logger.getLogger("FileLogger").info("10K entries complete at "+new DateTime()+" in "+((System.currentTimeMillis()-start)/1000)+" sec");
		
		Logger.getLogger("FileLogger").info("Going for First search by Couch");
		clientService.findAllByIdentifier("1234556"+"786");
		Logger.getLogger("FileLogger").info("Completed First search by Couch");
		
		Logger.getLogger("FileLogger").info("Going for 2nd search by Couch");
		clientService.findAllByIdentifier("1234556"+"786");
		Logger.getLogger("FileLogger").info("Completed 2nd search by Couch");

		
		Logger.getLogger("FileLogger").info("Going for First search by Lucene");
		List<Client> l = clientService.findByCriteria("first", "MALE", null, null, "ethnicity", "eth3");
		Logger.getLogger("FileLogger").info("Completed First search of size "+l.size()+" by Lucene");
		
		Logger.getLogger("FileLogger").info("Going for 2nd search by Lucene");
		l = clientService.findByCriteria("first", "MALE", null, null, "ethnicity", "eth3");
		Logger.getLogger("FileLogger").info("Completed 2nd search of size "+l.size()+" by Lucene");
	}
	
	@Test
	public void shouldAdd10KFullDataClientsAfterValidation() {
		Logger.getLogger("FileLogger").info("Starting at "+new DateTime());
		
		final long start = System.currentTimeMillis();
		long last1000 = System.currentTimeMillis();
		
		for (int i = 0; i < 10000; i++) {
			addClient(i, false);
			
			if(i%1000 == 0){
				Logger.getLogger("FileLogger").info("Next thousand entries at "+new DateTime()+" in "+((System.currentTimeMillis()-last1000)/1000)+" sec");
				last1000 = System.currentTimeMillis();
			}
		}
		Logger.getLogger("FileLogger").info("10K entries complete at "+new DateTime()+" in "+((System.currentTimeMillis()-start)/1000)+" sec");
	}
	
	void addClient(int i, boolean direct){
		int ageInWeeks = new Random().nextInt(2860);// assuming average age of people is 55 years
		DateTime birthdate = new DateTime().minusWeeks(ageInWeeks);
		DateTime deathdate = i%7==0?new DateTime():null;// every 7th person died today
		Client c = new Client("entityId"+i, "firstName"+i, "middleName"+i, "lastName"+i, birthdate, deathdate, false, false, i%2==0?"FEMALE":"MALE");
		
		Map<String, String> am = new HashMap<>();
		Address ab = new Address("birthplace", null, null, am , null, null, null, "Sindh", "Pakistan");
		ab.setCityVillage("Karachi");
		ab.setTown("Korangi");
		ab.setSubTown("UC"+i%11);
		c.addAddress(ab);
		
		Address ur = new Address("usual_residence", null, null, am , null, null, "752"+new Random().nextInt(5), "Sindh", "Pakistan");
		ur.setCityVillage("Karachi");
		ur.setTown(i%3==0?"Korangi":"Baldia");
		ur.setSubTown("UC"+i%11);
		c.addAddress(ur);
		
		c.addAttribute("ethnicity", "eth"+i%7);
		c.addAttribute("health area", "healtha"+i%7);
		
		c.addIdentifier("CNIC", "1234556"+i);
		c.addIdentifier("NTN", "564300"+i);
		
		if(direct){
			ac.add(c);
		}
		else {
			clientService.addClient(c);
		}
	}
	
	@Test
	public void shouldGetByDynamicView() {
		addClients();
		List<Client> l2 = clientService.findByCriteria(null, "MALE", null, null, null, null, null, null, null, null, null, null, null, null);
		assertTrue(l2.size() == 10);
		
		l2 = clientService.findByCriteria(null, "FEMALE", null, null, null, null, null, null, null, null, null, null, null, null);
		assertTrue(l2.size() == 0);

		l2 = clientService.findByCriteria("fn", "MALE", null, null, null, null, null, null, null, null, null, null, null, null);
		assertTrue(l2.size() == 10);
		
		l2 = clientService.findByCriteria("fn1", "MALE"   , null, null, null, null, null, null, null, null, null, null, null, null);
		assertTrue(l2.size() == 1);
	}

	@Test
	public void shouldFetchClientByIdentifier()
	{
		String baseEntityId = "testclient2";
		Client c = new Client(baseEntityId)
			.withBirthdate(new DateTime(), false)
			.withFirstName("C first n")
			.withLastName("C last n")
			.withMiddleName("C middle n")
			.withGender(Gender.MALE);
		c.withAddress(new Address("birthplace", new Date(System.currentTimeMillis()-1000*60*60*24*2), new Date(), null, "lat", "lon", "75210", "Sindh", "Pakistan"));
		c.withAttribute("ETHNICITY", "Mughal");
		c.withIdentifier("Program ID", "01001222");
		
		clientService.addClient(c);
		
		Client ce = clientService.getByBaseEntityId("testclient2");
		assertEquals("testclient2", ce.getBaseEntityId());
		assertTrue(Client.class.getSimpleName().equals(ce.type()));
		assertEquals("birthplace", ce.getAddresses().get(0).getAddressType());
		assertEquals("Mughal", ce.getAttribute("ethnicity"));
		assertEquals("01001222", ce.getIdentifier("program id"));
		
		List<Client> ce2 = clientService.findAllByIdentifier("01001222");
		assertTrue(ce2.size() == 1);
		assertEquals("testclient2", ce2.get(0).getBaseEntityId());
		
		List<Client> ce3 = clientService.findAllByIdentifier("Program ID", "01001222");
		assertTrue(ce3.size() == 1);
		assertEquals("testclient2", ce3.get(0).getBaseEntityId());
	}
	
	@Test
	public void shouldFetchClientByAttribute()
	{
		String baseEntityId = "testclient2";
		Client c = new Client(baseEntityId)
			.withBirthdate(new DateTime(), false)
			.withFirstName("C first n")
			.withLastName("C last n")
			.withMiddleName("C middle n")
			.withGender(Gender.MALE);
		c.withAddress(new Address("birthplace", new Date(System.currentTimeMillis()-1000*60*60*24*2), new Date(), null, "lat", "lon", "75210", "Sindh", "Pakistan"));
		c.withAttribute("ETHNICITY", "Mughal");
		c.withIdentifier("Program ID", "01001222");
		
		clientService.addClient(c);
		
		c = new Client("testclient3")
		.withBirthdate(new DateTime(), false)
		.withFirstName("C first n")
		.withLastName("C last n")
		.withMiddleName("C middle n")
		.withGender(Gender.MALE);
		c.withAttribute("ETHNICITY", "Mughal");
		c.addIdentifier("Program ID", "01001223");
		
		clientService.addClient(c);
		
		List<Client> ce = clientService.findAllByAttribute("ETHNICITY", "Mughal");
		assertTrue(ce.size() == 2);
		assertThat(ce, Matchers.<Client>hasItem(Matchers.<Client>hasProperty("baseEntityId",equalTo("testclient2"))));
		assertThat(ce, Matchers.<Client>hasItem(Matchers.<Client>hasProperty("baseEntityId",equalTo("testclient3"))));
	}
	
}
