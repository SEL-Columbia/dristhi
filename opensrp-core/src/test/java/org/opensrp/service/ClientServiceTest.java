package org.opensrp.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.opensrp.common.AllConstants.Client.OPENMRS_UUID_IDENTIFIER_TYPE;

import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.domain.Address;
import org.opensrp.domain.Client;
import org.opensrp.repository.ClientsRepository;
import org.opensrp.repository.postgres.BaseRepositoryTest;
import org.opensrp.repository.postgres.ClientsRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class ClientServiceTest extends BaseRepositoryTest {
	
	private ClientService clientService;
	
	@Autowired
	@Qualifier("clientsRepositoryPostgres")
	private ClientsRepository clientsRepository;
	
	@Before
	public void setUpPostgresRepository() {
		clientService = new ClientService(clientsRepository);
	}
	
	@Override
	protected Set<String> getDatabaseScripts() {
		Set<String> scripts = new HashSet<String>();
		scripts.add("client.sql");
		return scripts;
	}
	
	@Test
	public void testAddClient() {
		Client client = new Client("f67823b0-378e-4a35-93fc-bb00def74e2f").withBirthdate(new DateTime("2017-03-31"), true)
		        .withGender("Male").withFirstName("xobili").withLastName("mbangwa");
		client.withIdentifier("ZEIR_ID", "233864-8").withAttribute("Home_Facility", "Linda");
		clientService.addClient(client);
		assertEquals(16, clientService.findAllClients().size());
		
		Client savedClient = clientService.find("f67823b0-378e-4a35-93fc-bb00def74e2f");
		assertNotNull(savedClient.getId());
		assertEquals(new DateTime("2017-03-31"), client.getBirthdate());
		assertEquals("xobili", client.getFirstName());
		assertEquals("mbangwa", client.getLastName());
		assertEquals("233864-8", client.getIdentifier("ZEIR_ID"));
		
		//test adding existing client is updated
		DateTime timeBeforeUpdate = new DateTime();
		savedClient.withMiddleName("Rustus");
		clientService.addClient(savedClient);
		
		Client updatedClient = clientService.find(savedClient.getBaseEntityId());
		assertEquals("Rustus", updatedClient.getMiddleName());
		assertEquals(ClientsRepositoryImpl.REVISION_PREFIX + 2, updatedClient.getRevision());
		assertTrue(timeBeforeUpdate.isBefore(updatedClient.getDateEdited()));
		
	}
	
	@Test
	public void testFindClient() {
		Client client = new Client("33d9a17f-d729-4276-9891-b43e8b60fd12");
		assertEquals("05934ae338431f28bf6793b24159c647", clientService.findClient(client).getId());
		
		client.withIdentifier(OPENMRS_UUID_IDENTIFIER_TYPE, "9c4260f2-d296-4af1-b771-debda1b6cfd1").withIdentifier("ZEIR_ID",
		    "218220-2");
		assertEquals("05934ae338431f28bf6793b24159c647", clientService.findClient(client).getId());
		
		client = new Client("");
		client.withIdentifier("ZEIR_ID", "218220-211");
		assertNull(clientService.findClient(client));
		
		client.withBaseEntityId("3423fdsf-23423-hjh23423");
		
		assertNull(clientService.findClient(client));
		
		client = new Client("");
		assertNull(clientService.findClient(client));
		
		client.withIdentifier("ZEIR_ID", "218225-1");
		assertEquals("05934ae338431f28bf6793b24167817d", clientService.findClient(client).getId());
	}
	
	@Test
	public void testFind() {
		assertEquals("05934ae338431f28bf6793b24159c647", clientService.find("33d9a17f-d729-4276-9891-b43e8b60fd12").getId());
		
		assertNull(clientService.find("3423fdsf-23423-hjh23423"));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testUpdateClient_NewClient() throws JSONException {
		Client client = new Client("33d9a17f-d729-4276-9891-b43e8b60fd12");
		clientService.updateClient(client);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testUpdateClient_Client_Not_Exists() throws JSONException {
		Client client = clientService.find("67007c17-97bb-4732-a1b8-3a0c292b5432");
		clientsRepository.safeRemove(client);
		client.setFirstName("Conses");
		clientService.updateClient(client);
	}
	
	@Test()
	public void testUpdateClient() throws JSONException {
		Client client = clientService.find("67007c17-97bb-4732-a1b8-3a0c292b5432");
		client.withFirstName("Conses").withMiddleName("Divens").withIdentifier(OPENMRS_UUID_IDENTIFIER_TYPE,
		    "2321312-dsfsd");
		clientService.updateClient(client);
		
		Client updatedClient = clientService.find("67007c17-97bb-4732-a1b8-3a0c292b5432");
		assertEquals("Conses", updatedClient.getFirstName());
		assertEquals("Divens", updatedClient.getMiddleName());
		assertEquals("2321312-dsfsd", updatedClient.getIdentifier(OPENMRS_UUID_IDENTIFIER_TYPE));
	}
	
	@Test
	public void testMergeClient() {
		Client client = clientService.find("cc127350-c1cd-4c3a-99d4-4d632882f522");
		Address address = new Address().withCountry("KE").withStateProvince("Punja").withAddressType("home");
		
		client.withFirstName("Conses").withMiddleName("Divens").withIdentifier(OPENMRS_UUID_IDENTIFIER_TYPE, "2321312-dsfsd")
		        .withAttribute("Zone", "Zone3").withAddress(address);
		
		Client mergedClient = clientService.mergeClient(client);
		assertEquals("Conses", mergedClient.getFirstName());
		assertEquals("Divens", mergedClient.getMiddleName());
		assertEquals("2321312-dsfsd", mergedClient.getIdentifier(OPENMRS_UUID_IDENTIFIER_TYPE));
		assertEquals("Zone3", mergedClient.getAttribute("Zone"));
		assertEquals(address, mergedClient.getAddress(address.getAddressType()));
		
	}
	
	@Test
	public void testAddorUpdate() {
		Client client = new Client("f67823b0-378e-4a35-93fc-bb00def74e2f").withBirthdate(new DateTime("2017-03-31"), true)
		        .withGender("Male").withFirstName("xobili").withLastName("mbangwa");
		client.withIdentifier("ZEIR_ID", "233864-8").withAttribute("Home_Facility", "Linda");
		clientService.addorUpdate(client);
		assertEquals(16, clientService.findAllClients().size());
		
		Client savedClient = clientService.find("f67823b0-378e-4a35-93fc-bb00def74e2f");
		assertNotNull(savedClient.getId());
		assertEquals(new DateTime("2017-03-31"), client.getBirthdate());
		assertEquals("xobili", client.getFirstName());
		assertEquals("mbangwa", client.getLastName());
		assertEquals("233864-8", client.getIdentifier("ZEIR_ID"));
		
		//test adding existing client is updated
		DateTime timeBeforeUpdate = new DateTime();
		savedClient.withMiddleName("Rustus");
		clientService.addorUpdate(savedClient);
		
		Client updatedClient = clientService.find(savedClient.getBaseEntityId());
		assertEquals("Rustus", updatedClient.getMiddleName());
		assertEquals(ClientsRepositoryImpl.REVISION_PREFIX + 2, updatedClient.getRevision());
		assertTrue(timeBeforeUpdate.isBefore(updatedClient.getDateEdited()));
	}
	
	@Test
	public void testAddorUpdateWithServerVersionFlag() {
		Client client = new Client("f67823b0-378e-4a35-93fc-bb00def74e2f").withBirthdate(new DateTime("2017-03-31"), true)
		        .withGender("Male").withFirstName("xobili").withLastName("mbangwa");
		client.withIdentifier("ZEIR_ID", "233864-8").withAttribute("Home_Facility", "Linda");
		clientService.addorUpdate(client, false);
		assertEquals(16, clientService.findAllClients().size());
		
		Client savedClient = clientService.find("f67823b0-378e-4a35-93fc-bb00def74e2f");
		assertNotNull(savedClient.getId());
		assertEquals(new DateTime("2017-03-31"), client.getBirthdate());
		assertEquals("xobili", client.getFirstName());
		assertEquals("mbangwa", client.getLastName());
		assertEquals("233864-8", client.getIdentifier("ZEIR_ID"));
		
		long existingServerVesion = savedClient.getServerVersion();
		//test adding existing client is updated
		DateTime timeBeforeUpdate = new DateTime();
		savedClient.withMiddleName("Rustus");
		clientService.addorUpdate(savedClient, false);
		
		Client updatedClient = clientService.find(savedClient.getBaseEntityId());
		assertEquals("Rustus", updatedClient.getMiddleName());
		assertEquals(ClientsRepositoryImpl.REVISION_PREFIX + 2, updatedClient.getRevision());
		assertTrue(timeBeforeUpdate.isBefore(updatedClient.getDateEdited()));
		assertEquals(existingServerVesion, updatedClient.getServerVersion().longValue());
		
		clientService.addorUpdate(savedClient, true);
		assertTrue(clientService.find(savedClient.getBaseEntityId()).getServerVersion() > existingServerVesion);
	}
	
}
