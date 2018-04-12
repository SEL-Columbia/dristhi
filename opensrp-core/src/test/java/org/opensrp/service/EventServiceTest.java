package org.opensrp.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.opensrp.common.AllConstants.Event.OPENMRS_UUID_IDENTIFIER_TYPE;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.common.AllConstants.Client;
import org.opensrp.domain.Event;
import org.opensrp.domain.Obs;
import org.opensrp.repository.ClientsRepository;
import org.opensrp.repository.EventsRepository;
import org.opensrp.repository.postgres.BaseRepositoryTest;
import org.opensrp.repository.postgres.EventsRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DuplicateKeyException;

public class EventServiceTest extends BaseRepositoryTest {
	
	private EventService eventService;
	
	@Autowired
	@Qualifier("eventsRepositoryPostgres")
	private EventsRepository eventsRepository;
	
	@Autowired
	@Qualifier("clientsRepositoryPostgres")
	private ClientsRepository clientsRepository;
	
	private Set<String> scripts = new HashSet<String>();;
	
	@Before
	public void setUpPostgresRepository() {
		eventService = new EventService(eventsRepository, new ClientService(clientsRepository));
	}
	
	@Override
	protected Set<String> getDatabaseScripts() {
		scripts.add("event.sql");
		return scripts;
	}
	
	@Test
	public void testFindByUniqueId() {
		assertEquals("05934ae338431f28bf6793b241be69a5", eventService.find("4aecc0c1-e008-4227-938d-66db17236a3d").getId());
		
		assertEquals("05934ae338431f28bf6793b241bdb88c", eventService.find("06c8644b-b560-45fd-9af5-b6b1484e3504").getId());
		
		assertNull(eventService.find("50102f0a-a9c9-1d-a6c476433b3c"));
		
		//deleted event	
		eventsRepository.safeRemove(eventService.find("06c8644b-b560-45fd-9af5-b6b1484e3504"));
		assertNull(eventService.find("06c8644b-b560-45fd-9af5-b6b1484e3504"));
	}
	
	@Test
	public void testFindByEvent() {
		Event event = new Event();
		assertNull(eventService.find(event));
		
		event.withIdentifier(OPENMRS_UUID_IDENTIFIER_TYPE, "94ec8561-14ab-48d1-a6d4-4ae05191f6e6");
		
		assertEquals("05934ae338431f28bf6793b241bdbc55", eventService.find(event).getId());
		
		//deleted event	
		eventsRepository.safeRemove(eventService.find(event));
		assertNull(eventService.find(event));
	}
	
	@Test
	public void testFindByEventId() {
		Event event = eventService.findById("05934ae338431f28bf6793b2419c319a");
		assertEquals("ea1f9439-a663-4073-93b9-6ef2b8bca3c1", event.getBaseEntityId());
		assertEquals("d960046a-e2a0-4bbf-b687-d41c2a52d8c8", event.getFormSubmissionId());
		assertEquals("Vaccination", event.getEventType());
		
		assertNull(eventService.findById("50102f0a-a9c9-1d-a6c476433b3c"));
		
		//deleted event	
		eventsRepository.safeRemove(event);
		assertNull(eventService.findById(event.getId()));
	}
	
	@Test
	public void testAddEvent() {
		Obs obs = new Obs("concept", "decimal", "1730AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, "3.5", null, "weight");
		Event event = new Event().withBaseEntityId("435534534543").withEventType("Growth Monitoring")
		        .withFormSubmissionId("gjhg34534 nvbnv3345345__4").withEventDate(new DateTime()).withObs(obs);
		
		eventService.addEvent(event);
		
		event = eventService.findByFormSubmissionId("gjhg34534 nvbnv3345345__4");
		assertEquals("435534534543", event.getBaseEntityId());
		assertEquals("Growth Monitoring", event.getEventType());
		assertEquals(1, event.getObs().size());
		assertEquals("3.5", event.getObs(null, "1730AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA").getValue());
		
		//test if an event with voided date add event as deleted
		event = new Event().withBaseEntityId("2423nj-sdfsd-sf2dfsd-2399d").withEventType("Vaccination")
		        .withFormSubmissionId("hshj2342_jsjs-jhjsdfds-23").withEventDate(new DateTime()).withObs(obs);
		event.setDateVoided(new DateTime());
		eventService.addEvent(event);
		assertNull(eventService.findByFormSubmissionId(event.getFormSubmissionId()));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testAddEvent_For_ExistingEvent() {
		Event event = eventService.findById("05934ae338431f28bf6793b241bdc44a");
		eventService.addEvent(event);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testAddEvent_Duplicate_Identifiers() {
		Event event = new Event().withIdentifier(OPENMRS_UUID_IDENTIFIER_TYPE, "4aecc0c1-e008-4227-938d-66db17236a3d");
		eventService.addEvent(event);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testAddEvent_Duplicate_BaseEntity_FormSubmission() {
		Event event = new Event().withBaseEntityId("58b33379-dab2-4f5c-8f09-6d2bd63023d8")
		        .withFormSubmissionId("5f1b201d-2132-4eb9-8fa1-3169a61cc50a");
		eventService.addEvent(event);
	}
	
	@Test(expected = DuplicateKeyException.class)
	public void testAddEvent_Duplicate_FormSubmission() {
		Event event = new Event().withBaseEntityId("58b33379-dab2-4f")
		        .withFormSubmissionId("5f1b201d-2132-4eb9-8fa1-3169a61cc50a");
		eventService.addEvent(event);
		
	}
	
	@Test
	public void testProcessOutOfArea() throws SQLException {
		scripts.add("client.sql");
		populateDatabase();
		Event event = new Event().withEventType("Temperature").withProviderId("tester111")
		        .withLocationId("2242342-23dsfsdfds").withIdentifier(Client.ZEIR_ID, "218229-3");
		Event outOfAreaEvent = eventService.processOutOfArea(event);
		assertEquals(1, outOfAreaEvent.getDetails().size());
		assertEquals("biddemo", outOfAreaEvent.getDetails().get("out_of_catchment_provider_id"));
		assertEquals("42abc582-6658-488b-922e-7be500c070f3", outOfAreaEvent.getLocationId());
		assertEquals("biddemo", outOfAreaEvent.getProviderId());
		
		event = new Event().withEventType("Out of Area Service").withProviderId("tester111").withLocationId("2242342-23dsfsdfds")
		        .withIdentifier(Client.ZEIR_ID, "218229-3");
		
		outOfAreaEvent = eventService.processOutOfArea(event);
		assertEquals(event, outOfAreaEvent);
		assertEquals(15, eventService.getAll().size());
		
		Obs obs = new Obs("concept", "decimal", "1730AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, "3.5", null, "weight");
		event = new Event().withEventType("Growth Monitoring").withFormSubmissionId("gjhg34534 nvbnv3345345__4")
		        .withEventDate(new DateTime()).withObs(obs).withIdentifier(Client.ZEIR_ID, "218229-3");
		
		outOfAreaEvent = eventService.processOutOfArea(event);
		assertEquals(event, outOfAreaEvent);
		
		//assertEquals(16, eventService.getAll().size());
		
	}
	
	@Test
	public void testAddorUpdateEvent() {
		Obs obs = new Obs("concept", "decimal", "1730AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, "3.5", null, "weight");
		Event event = new Event().withBaseEntityId("435534534543").withEventType("Growth Monitoring")
		        .withFormSubmissionId("gjhg34534 nvbnv3345345__4").withEventDate(new DateTime()).withObs(obs);
		
		eventService.addorUpdateEvent(event);
		
		Event updatedEvent = eventService.findByFormSubmissionId("gjhg34534 nvbnv3345345__4");
		assertEquals("435534534543", updatedEvent.getBaseEntityId());
		assertEquals("Growth Monitoring", updatedEvent.getEventType());
		assertEquals(1, updatedEvent.getObs().size());
		assertEquals("3.5", updatedEvent.getObs(null, "1730AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA").getValue());
		assertNull(updatedEvent.getDateEdited());
		
		event.setTeam("ATeam");
		event.setProviderId("tester11");
		event.setLocationId("321312-fsff-2328");
		eventService.addorUpdateEvent(event);
		
		updatedEvent = eventService.findByFormSubmissionId("gjhg34534 nvbnv3345345__4");
		assertEquals("ATeam", updatedEvent.getTeam());
		assertEquals("tester11", updatedEvent.getProviderId());
		assertEquals("321312-fsff-2328", updatedEvent.getLocationId());
		assertEquals(EventsRepositoryImpl.REVISION_PREFIX + 2, updatedEvent.getRevision());
		assertNotNull(updatedEvent.getDateEdited());
		
		//test if an event with voided date add event as deleted
		event = new Event().withBaseEntityId("2423nj-sdfsd-sf2dfsd-2399d").withEventType("Vaccination")
		        .withFormSubmissionId("hshj2342_jsjs-jhjsdfds-23").withEventDate(new DateTime()).withObs(obs);
		event.setDateVoided(new DateTime());
		eventService.addorUpdateEvent(event);
		assertNull(eventService.findByFormSubmissionId(event.getFormSubmissionId()));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testUpdateEvent_NonExistingEvent() {
		Obs obs = new Obs("concept", "decimal", "1730AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, "3.5", null, "weight");
		Event event = new Event().withBaseEntityId("435534534543").withEventType("Growth Monitoring")
		        .withFormSubmissionId("gjhg34534 nvbnv3345345__4").withEventDate(new DateTime()).withObs(obs);
		
		eventService.updateEvent(event);
	}
	
	@Test
	public void testUpdateEvent() {
		DateTime timebeforeUpdate = DateTime.now();
		Event event = eventService.findById("05934ae338431f28bf6793b24177a1dc");
		Obs obs = new Obs("concept", "decimal", "1730AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, "3.5", null, "weight");
		event.withIdentifier(OPENMRS_UUID_IDENTIFIER_TYPE, "62242n-223423-2332").addObs(obs);
		eventService.updateEvent(event);
		
		Event updatedEvent = eventService.findById(event.getId());
		assertEquals(0, Minutes.minutesBetween(timebeforeUpdate, updatedEvent.getDateEdited()).getMinutes());
		assertEquals("3.5", updatedEvent.getObs(null, "1730AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA").getValue());
		assertEquals("62242n-223423-2332", updatedEvent.getIdentifier(OPENMRS_UUID_IDENTIFIER_TYPE));
		
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testMergeEvent_Missing_Identifiers() {
		Event event = new Event().withBaseEntityId("435534534543").withEventType("Growth Monitoring");
		eventService.mergeEvent(event);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testMergeEvent_NonExisting_Identifiers() {
		Event event = new Event().withBaseEntityId("435534534543").withEventType("Growth Monitoring")
		        .withIdentifier(OPENMRS_UUID_IDENTIFIER_TYPE, "242332-hgfhfh-dfg8d");
		eventService.mergeEvent(event);
	}
	
	@Test
	public void testMergeEvent() {
		Obs obs = new Obs("concept", "decimal", "1730AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, "3.5", null, "weight");
		Event event = new Event().withEventType("Growth Monitoring")
		        .withIdentifier(OPENMRS_UUID_IDENTIFIER_TYPE, "4aecc0c1-e008-4227-938d-66db17236a3d")
		        .withEventDate(new DateTime()).withObs(obs);
		
		eventService.mergeEvent(event);
		
		Event updatedEvent = eventService.find("4aecc0c1-e008-4227-938d-66db17236a3d");
		
		assertEquals("05934ae338431f28bf6793b241be69a5", updatedEvent.getId());
		assertEquals("Growth Monitoring", updatedEvent.getEventType());
		assertEquals(1, updatedEvent.getObs().size());
		assertEquals("3.5", updatedEvent.getObs(null, "1730AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA").getValue());
		assertEquals(0, Minutes.minutesBetween(DateTime.now(), updatedEvent.getDateEdited()).getMinutes());
	}
	
}
