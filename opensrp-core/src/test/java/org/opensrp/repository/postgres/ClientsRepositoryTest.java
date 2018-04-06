package org.opensrp.repository.postgres;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.opensrp.common.AllConstants.Client.OPENMRS_UUID_IDENTIFIER_TYPE;
import static org.opensrp.common.AllConstants.BaseEntity.BASE_ENTITY_ID;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Test;
import org.opensrp.domain.Client;
import org.opensrp.repository.ClientsRepository;
import org.opensrp.search.AddressSearchBean;
import org.opensrp.search.ClientSearchBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class ClientsRepositoryTest extends BaseRepositoryTest {
	
	@Autowired
	@Qualifier("clientsRepositoryPostgres")
	private ClientsRepository clientsRepository;
	
	@Override
	protected Set<String> getDatabaseScripts() {
		Set<String> scripts = new HashSet<String>();
		scripts.add("client.sql");
		return scripts;
	}
	
	@Test
	public void testGet() {
		Client client = clientsRepository.get("05934ae338431f28bf6793b2416946b7");
		assertEquals("469597f0-eefe-4171-afef-f7234cbb2859", client.getBaseEntityId());
		assertEquals("eb4b258c-7558-436c-a1fe-e91d9e12f849", client.getIdentifier(OPENMRS_UUID_IDENTIFIER_TYPE));
		assertEquals("January", client.getFirstName().trim());
		assertEquals("Babysix", client.getLastName());
		//missing client
		assertNull(clientsRepository.get("05934ae338bf6793b2416946b7"));
	}
	
	@Test
	public void testAdd() {
		Client client = new Client("f67823b0-378e-4a35-93fc-bb00def74e2f").withBirthdate(new DateTime("2017-03-31"), true)
		        .withGender("Male").withFirstName("xobili").withLastName("mbangwa");
		client.withIdentifier("ZEIR_ID", "233864-8").withAttribute("Home_Facility", "Linda");
		clientsRepository.add(client);
		assertEquals(16, clientsRepository.getAll().size());
		
		Client savedClient = clientsRepository.findByBaseEntityId("f67823b0-378e-4a35-93fc-bb00def74e2f");
		assertNotNull(savedClient.getId());
		assertEquals(new DateTime("2017-03-31"), client.getBirthdate());
		assertEquals("xobili", client.getFirstName());
		assertEquals("mbangwa", client.getLastName());
		assertEquals("233864-8", client.getIdentifier("ZEIR_ID"));
		
	}
	
	@Test
	public void testUpdate() {
		Client client = clientsRepository.get("05934ae338431f28bf6793b2416946b7");
		client.setFirstName("Hummel");
		client.setLastName("Basialis");
		client.withIdentifier("ZEIR_ID", "09876-98");
		clientsRepository.update(client);
		
		Client updatedClient = clientsRepository.get(client.getId());
		assertEquals("Hummel", updatedClient.getFirstName());
		assertEquals("Basialis", updatedClient.getLastName());
		assertEquals("09876-98", client.getIdentifier("ZEIR_ID"));
	}
	
	@Test
	public void testGetAll() {
		assertEquals(15, clientsRepository.getAll().size());
		
		clientsRepository.safeRemove(clientsRepository.get("05934ae338431f28bf6793b24164cbd9"));
		
		List<Client> clients = clientsRepository.getAll();
		
		assertEquals(14, clients.size());
		for (Client client : clients)
			assertNotEquals("05934ae338431f28bf6793b24164cbd9", client.getId());
		
	}
	
	@Test
	public void testSafeRemove() {
		Client client = clientsRepository.get("05934ae338431f28bf6793b2416946b7");
		clientsRepository.safeRemove(client);
		List<Client> clients = clientsRepository.getAll();
		assertEquals(14, clients.size());
		
		for (Client cl : clients)
			assertNotEquals("05934ae338431f28bf6793b2416946b7", cl.getId());
		
		assertNull(clientsRepository.get("05934ae338431f28bf6793b2416946b7"));
	}
	
	@Test
	public void testFindByBaseEntityId() {
		Client client = clientsRepository.findByBaseEntityId("86c039a2-0b68-4166-849e-f49897e3a510");
		assertEquals("05934ae338431f28bf6793b24164cbd9", client.getId());
		assertEquals("ab91df5d-e433-40f3-b44f-427b73c9ae2a", client.getIdentifier(OPENMRS_UUID_IDENTIFIER_TYPE));
		assertEquals("Sally", client.getFirstName());
		assertEquals("Mtini", client.getLastName().trim());
		
		assertNull(clientsRepository.findByBaseEntityId("f67823b0-378e-4a35-93fc-bb00def74e2f"));
	}
	
	@Test
	public void testFindAllClients() {
		assertEquals(15, clientsRepository.findAllClients().size());
		
		clientsRepository.safeRemove(clientsRepository.get("05934ae338431f28bf6793b24164cbd9"));
		
		List<Client> clients = clientsRepository.findAllClients();
		
		assertEquals(14, clients.size());
		for (Client client : clients)
			assertNotEquals("05934ae338431f28bf6793b24164cbd9", client.getId());
	}
	
	@Test
	public void testFindAllByIdentifier() {
		List<Client> clients = clientsRepository.findAllByIdentifier(OPENMRS_UUID_IDENTIFIER_TYPE,
		    "ab91df5d-e433-40f3-b44f-427b73c9ae2a");
		
		assertEquals(1, clients.size());
		assertEquals("05934ae338431f28bf6793b24164cbd9", clients.get(0).getId());
		assertEquals("Sally", clients.get(0).getFirstName());
		assertEquals("Mtini", clients.get(0).getLastName().trim());
		
		assertTrue(clientsRepository.findAllByIdentifier(OPENMRS_UUID_IDENTIFIER_TYPE, "ab91df5d-e433-40f3-b44f-427b73ca")
		        .isEmpty());
		
		assertTrue(
		    clientsRepository.findAllByIdentifier("identifier_type", "ab91df5d-e433-40f3-b44f-427b73c9ae2a").isEmpty());
	}
	
	@Test
	public void testAllByAttribute() {
		List<Client> clients = clientsRepository.findAllByAttribute("Home_Facility", "Happy Kids Clinic");
		
		assertEquals(9, clients.size());
		
		clients = clientsRepository.findAllByAttribute("CHW_Phone_Number", "0964357951");
		assertEquals(1, clients.size());
		assertEquals("05934ae338431f28bf6793b24164cbd9", clients.get(0).getId());
		assertEquals("Sally", clients.get(0).getFirstName());
		assertEquals("Mtini", clients.get(0).getLastName().trim());
		
		assertTrue(clientsRepository.findAllByAttribute("CHW_Phone_Number", "+0964357951").isEmpty());
		
		assertTrue(clientsRepository.findAllByAttribute("Phone_Number", "0964357951").isEmpty());
	}
	
	@Test
	public void testFindAllByMatchingName() {
		assertEquals(5, clientsRepository.findAllByMatchingName("b").size());
		
		List<Client> clients = clientsRepository.findAllByMatchingName("babytwo");
		
		assertEquals(1, clients.size());
		
		assertEquals("05934ae338431f28bf6793b24167b6d1", clients.get(0).getId());
		assertEquals("fe7b6350-16d2-41d0-8574-c194088705df", clients.get(0).getBaseEntityId());
		assertEquals("218227-7", clients.get(0).getIdentifier("ZEIR_ID"));
		assertEquals("ba5d3927-414f-4796-ae1e-9b73b50a5573", clients.get(0).getIdentifier(OPENMRS_UUID_IDENTIFIER_TYPE));
		
		assertEquals(6, clientsRepository.findAllByMatchingName("a").size());
		
		assertEquals(5, clientsRepository.findAllByMatchingName("Ja").size());
		
		assertEquals(4, clientsRepository.findAllByMatchingName("Janu").size());
		
		assertEquals(2, clientsRepository.findAllByMatchingName("January").size());
		
		assertTrue(clientsRepository.findAllByMatchingName("Kimbley").isEmpty());
	}
	
	@Test
	public void testFindByRelationshipIdAndDateCreated() {
		assertEquals(2, clientsRepository.findByRelationshipIdAndDateCreated("0154839f-8766-4eda-b729-89067c7a8c5d",
		    new DateTime("2018-03-13").toString(), new DateTime().toString()).size());
		
		assertTrue(clientsRepository.findByRelationshipIdAndDateCreated("0154839f-8766-4eda-89067c7a8c5d",
		    new DateTime("2018-03-14").toString(), new DateTime().toString()).isEmpty());
		
		assertTrue(clientsRepository.findByRelationshipIdAndDateCreated("0154839f-8766-4eda-b729-89067c7a8c5d",
		    new DateTime("2018-03-14").toString(), new DateTime().toString()).isEmpty());
		
		Client client = clientsRepository.findByRelationshipIdAndDateCreated("3abdb25a-f151-4a95-9311-bd30bf935085",
		    new DateTime("2018-03-13").toString(), new DateTime().toString()).get(0);
		assertEquals("05934ae338431f28bf6793b2415a0374", client.getId());
		assertEquals("94f3e8fb-2f05-4fca-8119-2b593d1962eb", client.getBaseEntityId());
		assertEquals("2018-03-01", client.getBirthdate().toLocalDate().toString());
	}
	
	@Test
	public void testFindByRelationshipId() {
		assertEquals(2, clientsRepository.findByRelationshipId("mother", "0154839f-8766-4eda-b729-89067c7a8c5d").size());
		
		Client client = clientsRepository.findByRelationshipId("mother", "3abdb25a-f151-4a95-9311-bd30bf935085").get(0);
		assertEquals("05934ae338431f28bf6793b2415a0374", client.getId());
		assertEquals("94f3e8fb-2f05-4fca-8119-2b593d1962eb", client.getBaseEntityId());
		assertEquals("cf58894b-71c6-41e0-a977-7283f2411cd5", client.getIdentifier(OPENMRS_UUID_IDENTIFIER_TYPE));
		assertEquals("2018-03-01", client.getBirthdate().toLocalDate().toString());
		
		assertTrue(clientsRepository.findByRelationshipId("father", "0154839f-8766-4eda-b729-89067c7a8c5d").isEmpty());
		
		assertTrue(clientsRepository.findByRelationshipId("mother", "0154839f-4eda-b729-89067c7a8c5d").isEmpty());
	}
	
	@Test
	public void testFindByCriteria() {
		ClientSearchBean searchBean = new ClientSearchBean();
		AddressSearchBean addressSearchBean = new AddressSearchBean();
		assertEquals(15, clientsRepository.findByCriteria(searchBean, addressSearchBean).size());
		
		searchBean.setNameLike("Janu");
		assertEquals(4, clientsRepository.findByCriteria(searchBean, addressSearchBean).size());
		
		searchBean.setNameLike("Baby");
		searchBean.setGender("Male");
		assertEquals(2, clientsRepository.findByCriteria(searchBean, addressSearchBean).size());
		
		searchBean.setBirthdateFrom(new DateTime("2016-04-13"));
		searchBean.setBirthdateTo(new DateTime());
		assertEquals(2, clientsRepository.findByCriteria(searchBean, addressSearchBean).size());
		
		searchBean.setDeathdateFrom(new DateTime("2018-01-01"));
		searchBean.setDeathdateTo(new DateTime());
		assertEquals(1, clientsRepository.findByCriteria(searchBean, addressSearchBean).size());
		
		searchBean = new ClientSearchBean();
		searchBean.setAttributeType("Home_Facility");
		searchBean.setAttributeValue("Happy Kids Clinic");
		assertEquals(9, clientsRepository.findByCriteria(searchBean, addressSearchBean).size());
		
		searchBean.setAttributeType("CHW_Name");
		searchBean.setAttributeValue("Hellen");
		List<Client> clients = clientsRepository.findByCriteria(searchBean, addressSearchBean);
		assertEquals(1, clients.size());
		assertEquals("05934ae338431f28bf6793b24164cbd9", clients.get(0).getId());
		
		searchBean = new ClientSearchBean();
		searchBean.setLastEditFrom(new DateTime("2018-03-13T12:57:05.652"));
		searchBean.setLastEditTo(new DateTime());
		assertEquals(6, clientsRepository.findByCriteria(searchBean, addressSearchBean).size());
		
		addressSearchBean.setAddressType("usual_residence");
		assertEquals(6, clientsRepository.findByCriteria(searchBean, addressSearchBean).size());
		
		addressSearchBean.setAddressType("usual_residence");
		assertEquals(6, clientsRepository.findByCriteria(searchBean, addressSearchBean).size());
		
	}
	
	@Test
	public void testFindByCriteriaWithoutAddressBean() {
		ClientSearchBean searchBean = new ClientSearchBean();
		assertEquals(15, clientsRepository.findByCriteria(searchBean).size());
		
		searchBean.setNameLike("Janu");
		assertEquals(4, clientsRepository.findByCriteria(searchBean).size());
		
		searchBean.setNameLike("Baby");
		searchBean.setGender("Male");
		assertEquals(2, clientsRepository.findByCriteria(searchBean).size());
		
		searchBean.setBirthdateFrom(new DateTime("2016-04-13"));
		searchBean.setBirthdateTo(new DateTime());
		assertEquals(2, clientsRepository.findByCriteria(searchBean).size());
		
		searchBean.setDeathdateFrom(new DateTime("2018-01-01"));
		searchBean.setDeathdateTo(new DateTime());
		assertEquals(1, clientsRepository.findByCriteria(searchBean).size());
		
		searchBean = new ClientSearchBean();
		searchBean.setAttributeType("Home_Facility");
		searchBean.setAttributeValue("Happy Kids Clinic");
		assertEquals(9, clientsRepository.findByCriteria(searchBean).size());
		
		searchBean.setAttributeType("CHW_Name");
		searchBean.setAttributeValue("Hellen");
		List<Client> clients = clientsRepository.findByCriteria(searchBean);
		assertEquals(1, clients.size());
		assertEquals("05934ae338431f28bf6793b24164cbd9", clients.get(0).getId());
		
		searchBean = new ClientSearchBean();
		searchBean.setLastEditFrom(new DateTime("2018-03-13T12:57:05.652"));
		searchBean.setLastEditTo(new DateTime());
		assertEquals(6, clientsRepository.findByCriteria(searchBean).size());
		
	}
	
	@Test
	public void testFindByCriteriaWithEditDateParams() {
		
		DateTime from = new DateTime("2018-03-13T12:57:05.652");
		DateTime to = new DateTime();
		List<Client> clients = clientsRepository.findByCriteria(new AddressSearchBean(), from, to);
		assertEquals(6, clients.size());
		
		for (Client client : clients)
			assertTrue(client.getDateEdited().isEqual(from) || client.getDateEdited().isAfter(from));
		
		to = from;
		from = new DateTime("2018-01-01");
		clients = clientsRepository.findByCriteria(new AddressSearchBean(), from, to);
		assertEquals(10, clients.size());
		
		for (Client client : clients) {
			assertTrue(client.getDateEdited().isEqual(from) || client.getDateEdited().isAfter(from));
			assertTrue(client.getDateEdited().isEqual(to) || client.getDateEdited().isBefore(to));
		}
		
		AddressSearchBean addressSearchBean = new AddressSearchBean();
		addressSearchBean.setCityVillage("hui");
		assertTrue(
		    clientsRepository.findByCriteria(addressSearchBean, new DateTime("2018-01-01"), new DateTime()).isEmpty());
	}
	
	@Test
	public void testFindByRelationShip() {
		assertEquals(2, clientsRepository.findByRelationShip("0154839f-8766-4eda-b729-89067c7a8c5d").size());
		
		Client client = clientsRepository.findByRelationShip("3abdb25a-f151-4a95-9311-bd30bf935085").get(0);
		assertEquals("05934ae338431f28bf6793b2415a0374", client.getId());
		assertEquals("94f3e8fb-2f05-4fca-8119-2b593d1962eb", client.getBaseEntityId());
		assertEquals("Fith", client.getFirstName());
		assertEquals("2018-03-01", client.getBirthdate().toLocalDate().toString());
		
		assertTrue(clientsRepository.findByRelationShip("0154839f-4eda-b729-89067c7a8c5d").isEmpty());
	}
	
	@Test
	public void testFindByEmptyServerVersion() {
		assertTrue(clientsRepository.findByEmptyServerVersion().isEmpty());
		
		Client client = clientsRepository.get("05934ae338431f28bf6793b2415a0374");
		client.setServerVersion(null);
		clientsRepository.update(client);
		
		client = clientsRepository.findByEmptyServerVersion().get(0);
		assertEquals("94f3e8fb-2f05-4fca-8119-2b593d1962eb", client.getBaseEntityId());
		assertEquals("Fith", client.getFirstName());
		assertEquals("2018-03-01", client.getBirthdate().toLocalDate().toString());
		assertEquals("218224-4", client.getIdentifier("ZEIR_ID"));
		
	}
	
	@Test
	public void testFindByServerVersion() {
		assertEquals(5, clientsRepository.findByServerVersion(1520935878136l).size());
		
		List<Client> clients = clientsRepository.findByServerVersion(1521003136406l);
		List<String> expectedIds = Arrays.asList("05934ae338431f28bf6793b241839005", "05934ae338431f28bf6793b2418380ce");
		assertEquals(2, clients.size());
		for (Client client : clients) {
			assertTrue(client.getServerVersion() >= 1521003136406l);
			assertTrue(expectedIds.contains(client.getId()));
		}
	}
	
	@Test
	public void testFindByFieldValue() {
		assertEquals(3,
		    clientsRepository
		            .findByFieldValue(BASE_ENTITY_ID, Arrays.asList(new String[] { "86c039a2-0b68-4166-849e-f49897e3a510",
		                    "f33c71c7-a9a4-495d-8028-b6d59e4034b3", "fe7b6350-16d2-41d0-8574-c194088705df" }))
		            .size());
		
		assertTrue(clientsRepository.findByFieldValue("Firstname", Arrays.asList(new String[] { "Baby", "Jan" })).isEmpty());
		
		assertTrue(
		    clientsRepository.findByFieldValue(BASE_ENTITY_ID, Arrays.asList(new String[] { "Baby", "Jan" })).isEmpty());
		
		Client client = clientsRepository
		        .findByFieldValue(BASE_ENTITY_ID, Arrays.asList(new String[] { "f33c71c7-a9a4-495d-8028-b6d59e4034b3" }))
		        .get(0);
		
		assertEquals("05934ae338431f28bf6793b241679500", client.getId());
		assertEquals("Jan", client.getFirstName());
		assertEquals("2018-01-01", client.getBirthdate().toLocalDate().toString());
		assertEquals("218226-9", client.getIdentifier("ZEIR_ID"));
	}
	
	@Test
	public void testNotInOpenMRSByServerVersion() {
		assertTrue(clientsRepository.notInOpenMRSByServerVersion(0l, Calendar.getInstance()).isEmpty());
		
		Client client = clientsRepository.get("05934ae338431f28bf6793b2415a0374");
		client.removeIdentifier(OPENMRS_UUID_IDENTIFIER_TYPE);
		clientsRepository.update(client);
		
		client = clientsRepository.notInOpenMRSByServerVersion(0l, Calendar.getInstance()).get(0);
		assertEquals("94f3e8fb-2f05-4fca-8119-2b593d1962eb", client.getBaseEntityId());
		assertEquals("Fith", client.getFirstName());
		assertEquals("2018-03-01", client.getBirthdate().toLocalDate().toString());
		assertEquals("218224-4", client.getIdentifier("ZEIR_ID"));
	}
}
