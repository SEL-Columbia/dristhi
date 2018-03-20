package org.opensrp.repository.postgres;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.opensrp.domain.Event;
import org.opensrp.repository.EventsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-opensrp.xml")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EventsRepositoryTest {
	
	@Autowired
	@Qualifier("eventsRepositoryPostgres")
	private EventsRepository eventsRepository;
	
	@Test
	public void test1Get() {
		Event event = eventsRepository.get("05934ae338431f28bf6793b2419c319a");
		assertEquals("ea1f9439-a663-4073-93b9-6ef2b8bca3c1", event.getBaseEntityId());
		assertEquals("d960046a-e2a0-4bbf-b687-d41c2a52d8c8", event.getFormSubmissionId());
		assertEquals("Vaccination", event.getEventType());
		//find non existent event
		assertNull(eventsRepository.get("05934ae338431f28bf4234gvnbvvh"));
		assertNull(eventsRepository.get(null));
	}
	
	@Test
	public void test2GetAll() {
		List<Event> events = eventsRepository.getAll();
		assertEquals(15, events.size());
	}
	
	@Test
	public void test3FindAllByIdentifier() {
		assertTrue(eventsRepository.findAllByIdentifier(null).isEmpty());
		List<Event> events = eventsRepository.findAllByIdentifier("06c8644b-b560-45fd-9af5-b6b1484e3504");
		assertEquals("d59504cc-09ef-4d09-9dc3-8f7eb65882fd", events.get(0).getFormSubmissionId());
		assertEquals("05934ae338431f28bf6793b241bdb88c", events.get(0).getId());
	}
	
	@Test
	public void test4FindAllByIdentifierAndType() {
		assertTrue(eventsRepository.findAllByIdentifier(null, null).isEmpty());
		List<Event> events = eventsRepository.findAllByIdentifier("OPENMRS_UUID", "06c8644b-b560-45fd-9af5-b6b1484e3504");
		assertEquals("d59504cc-09ef-4d09-9dc3-8f7eb65882fd", events.get(0).getFormSubmissionId());
		assertEquals("05934ae338431f28bf6793b241bdb88c", events.get(0).getId());
		
		assertTrue(eventsRepository.findAllByIdentifier("OPENMRS", "06c8644b-b560-45fd-9af5-b6b1484e3504").isEmpty());
	}
	
	@Test
	public void test5FindById() {
		Event event = eventsRepository.findById("05934ae338431f28bf6793b2419c319a");
		assertEquals("ea1f9439-a663-4073-93b9-6ef2b8bca3c1", event.getBaseEntityId());
		assertEquals("d960046a-e2a0-4bbf-b687-d41c2a52d8c8", event.getFormSubmissionId());
		assertEquals("Vaccination", event.getEventType());
		//find non existent event
		assertNull(eventsRepository.findById("05934ae338431f28bf4234gvnbvvh"));
		assertNull(eventsRepository.findById(null));
	}
	
	@Test
	public void test6FindByFormSubmissionId() {
		Event event = eventsRepository.findByFormSubmissionId("31c4a45a-09f4-4b01-abe8-a87526827df6");
		assertEquals("ea1f9439-a663-4073-93b9-6ef2b8bca3c1", event.getBaseEntityId());
		assertEquals("05934ae338431f28bf6793b241781149", event.getId());
		assertEquals("Growth Monitoring", event.getEventType());
		//find non existent event
		assertNull(eventsRepository.findByFormSubmissionId("05934ae338431f28bf4234gvnbvvh"));
		assertNull(eventsRepository.findByFormSubmissionId(null));
	}
	
	@Test
	public void test7FindByBaseEntityId() {
		List<Event> events = eventsRepository.findByBaseEntityId("58b33379-dab2-4f5c-8f09-6d2bd63023d8");
		assertEquals(7, events.size());
		
		events = eventsRepository.findByBaseEntityId("43930c23-c787-4ddb-ab76-770f77e7b17d");
		assertEquals(1, events.size());
		assertEquals("6b3243e9-3d45-495c-af69-f012061def01", events.get(0).getFormSubmissionId());
		assertEquals("05934ae338431f28bf6793b24199e690", events.get(0).getId());
		assertEquals("New Woman Registration", events.get(0).getEventType());
		//non-existent records
		assertTrue(eventsRepository.findByBaseEntityId("05934ae338431f28bf4234gvnbvvh").isEmpty());
	}
	
	@Test
	public void test8FindByBaseEntityAndFormSubmissionId() {
		Event event = eventsRepository.findByBaseEntityAndFormSubmissionId("58b33379-dab2-4f5c-8f09-6d2bd63023d8",
		    "baf8e663-71a1-4a30-8d40-2f3cab45a6d7");
		assertEquals("05934ae338431f28bf6793b241bdbb60", event.getId());
		
		event = eventsRepository.findByBaseEntityAndFormSubmissionId("43930c23-c787-4ddb-ab76-770f77e7b17d",
		    "6b3243e9-3d45-495c-af69-f012061def01");
		assertEquals(3, event.getObs().size());
		assertEquals("05934ae338431f28bf6793b24199e690", event.getId());
		assertEquals("New Woman Registration", event.getEventType());
		//non-existent records
		assertNull(eventsRepository.findByBaseEntityAndFormSubmissionId("58b33379-dab2-4f5c-8f09-6d2bd63023d8", "34354"));
	}
	
	@Test
	public void test9FindByBaseEntityAndType() {
		List<Event> events = eventsRepository.findByBaseEntityAndType("58b33379-dab2-4f5c-8f09-6d2bd63023d8", "Vaccination");
		assertEquals(6, events.size());
		
		events = eventsRepository.findByBaseEntityAndType("58b33379-dab2-4f5c-8f09-6d2bd63023d8", "Birth Registration");
		assertEquals(1, events.size());
		assertEquals("d59504cc-09ef-4d09-9dc3-8f7eb65882fd", events.get(0).getFormSubmissionId());
		assertEquals("05934ae338431f28bf6793b241bdb88c", events.get(0).getId());
		assertEquals(1521183592609l, events.get(0).getServerVersion().longValue());
		//non-existent records
		assertTrue(
		    eventsRepository.findByBaseEntityAndType("58b33379-dab2-4f5c-8f09-6d2bd63023d8", "Growth Monitoring").isEmpty());
		assertTrue(eventsRepository.findByBaseEntityAndType("58b33379", "Vaccination").isEmpty());
	}
	
	@Test
	public void test10FindEvents() {
		List<Event> events = eventsRepository.findEvents("58b33379-dab2-4f5c-8f09-6d2bd63023d8", null, null, null, null,
		    null, null, null, null, null, null);
		assertEquals(7, events.size());
		
		events = eventsRepository.findEvents("58b33379-dab2-4f5c-8f09-6d2bd63023d8", new DateTime("2018-01-10"),
		    new DateTime("2018-02-21"), null, null, null, null, null, null, null, null);
		assertEquals(6, events.size());
		
		events = eventsRepository.findEvents("58b33379-dab2-4f5c-8f09-6d2bd63023d8", new DateTime("2018-01-10"),
		    new DateTime(), "Vaccination", null, null, null, null, null, null, null);
		assertEquals(6, events.size());
		
		events = eventsRepository.findEvents("58b33379-dab2-4f5c-8f09-6d2bd63023d8", new DateTime("2018-01-10"),
		    new DateTime(), "Vaccination", "vaccination", null, null, null, null, null, null);
		assertEquals(6, events.size());
		
		events = eventsRepository.findEvents("58b33379-dab2-4f5c-8f09-6d2bd63023d8", new DateTime("2018-01-10"),
		    new DateTime(), "Vaccination", "vaccination", "biddemo", "42abc582-6658-488b-922e-7be500c070f3", null, null,
		    null, null);
		assertEquals(6, events.size());
		
		events = eventsRepository.findEvents("58b33379-dab2-4f5c-8f09-6d2bd63023d8", new DateTime("2018-01-10"),
		    new DateTime(), "Vaccination", "vaccination", "biddemo", "42abc582-6658-488b-922e-7be500c070f3", null, null,
		    "ATeam", "3453hgb454-4j345n-llk345");
		assertEquals(2, events.size());
		
		events = eventsRepository.findEvents("58b33379-dab2-4f5c-8f09-6d2bd63023d8", new DateTime("2018-01-10"),
		    new DateTime(), "Vaccination", "vaccination", "biddemo", "42abc582-6658-488b-922e-7be500c070f3", new DateTime(),
		    new DateTime(), "ATeam", "3453hgb454-4j345n-llk345");
		assertTrue(events.isEmpty());
	}
	
	@Test(expected = RuntimeException.class)
	public void test11FindEventsByDynamicQuery() {
		eventsRepository.findEventsByDynamicQuery("baseEntityId:4234324");
	}
	
	@Test
	public void test12FindByServerVersion() {
		assertEquals(15, eventsRepository.findByServerVersion(0).size());
		assertEquals(10, eventsRepository.findByServerVersion(1521183592609l).size());
		assertTrue(eventsRepository.findByServerVersion(1521469045597l).isEmpty());
	}
	
	@Test
	public void test13NotInOpenMRSByServerVersion() {
		Calendar cal = Calendar.getInstance();
		assertEquals(7, eventsRepository.notInOpenMRSByServerVersion(0, cal).size());
		assertEquals(3, eventsRepository.notInOpenMRSByServerVersion(1521469045588l, cal).size());
		assertTrue(eventsRepository.notInOpenMRSByServerVersion(1521469045597l, cal).isEmpty());
	}
	
	@Test
	public void test14NotInOpenMRSByServerVersionAndType() {
		Calendar cal = Calendar.getInstance();
		assertEquals(4, eventsRepository.notInOpenMRSByServerVersionAndType("Growth Monitoring", 0, cal).size());
		assertEquals(3,
		    eventsRepository.notInOpenMRSByServerVersionAndType("Growth Monitoring", 1521469045588l, cal).size());
		assertTrue(eventsRepository.notInOpenMRSByServerVersion(1521469045597l, cal).isEmpty());
	}
	
	@Test
	public void test15FindByClientAndConceptAndDate() {
		List<Event> events = eventsRepository.findByClientAndConceptAndDate("58b33379-dab2-4f5c-8f09-6d2bd63023d8",
		    "1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "1", "2018-01-10", new DateTime().toString("yyyy-MM-dd"));
		assertEquals(5, events.size());
		
		events = eventsRepository.findByClientAndConceptAndDate("58b33379-dab2-4f5c-8f09-6d2bd63023d8",
		    "1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "1", "2018-03-20", new DateTime().toString("yyyy-MM-dd"));
		
		assertTrue(events.isEmpty());
		
		events = eventsRepository.findByClientAndConceptAndDate("58b33379-dab2-4f5c-8f09-6d2bd63023d8",
		    "1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "2018-02-21", "2018-03-19", new DateTime().toString("yyyy-MM-dd"));
		
		assertEquals(4, events.size());
		
		events = eventsRepository.findByClientAndConceptAndDate("58b33379-dab2-4f5c-8f09-6d2bd63023d8",
		    "163531AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "Happy Kids Clinic", "2018-03-19",
		    new DateTime().toString("yyyy-MM-dd"));
		
		assertEquals(1, events.size());
		
	}
	
	@Test
	public void test16FindByBaseEntityIdAndConceptParentCode() {
		List<Event> events = eventsRepository.findByBaseEntityIdAndConceptParentCode("58b33379-dab2-4f5c-8f09-6d2bd63023d8",
		    "1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "783AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		assertEquals(2, events.size());
		
		events = eventsRepository.findByBaseEntityIdAndConceptParentCode("58b33379-dab2-4f5c-8f09-6d2bd63023d8",
		    "1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "886AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		
		assertEquals(1, events.size());
		
		assertEquals("05934ae338431f28bf6793b241bdbc55", events.get(0).getId());
		
		events = eventsRepository.findByBaseEntityIdAndConceptParentCode("58b33379-dab2-4f5c-8f09-6d2bd63023d8",
		    "1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "886AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		
		assertTrue(events.isEmpty());
	}
	
	@Test
	public void test15FindByConceptAndValue() {
		List<Event> events = eventsRepository.findByConceptAndValue("1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "1");
		assertEquals(8, events.size());
		
		events = eventsRepository.findByConceptAndValue("1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "5");
		
		assertTrue(events.isEmpty());
		
		events = eventsRepository.findByConceptAndValue("1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "2018-02-21");
		
		assertEquals(4, events.size());
		
		events = eventsRepository.findByConceptAndValue("163531AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "Happy Kids Clinic");
		
		assertEquals(1, events.size());
		
	}
	
	@Test
	public void test16findByEmptyServerVersion() {
		assertEquals(0, eventsRepository.findByEmptyServerVersion().size());
	}
	
	@Test
	public void test17findEvents() {
		
	}
	
	/*@Test
	public void test18findEventByEventTypeBetweenTwoDates() {
		List<Event> events = eventsRepository.getAll();
		for (Event event : events) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DATE, 1);
			event.setServerVersion(cal.getTimeInMillis());
			eventsRepository.update(event);
		}
		assertEquals(10, eventsRepository.findEventByEventTypeBetweenTwoDates("Vaccination").size());
		assertEquals(4, eventsRepository.findEventByEventTypeBetweenTwoDates("Growth Monitoring").size());
		assertEquals(1, eventsRepository.findEventByEventTypeBetweenTwoDates("Birth Registration").size());
	}*/
	
	@Test
	public void test30Add() {
		List<Event> events = eventsRepository.getAll();
		assertEquals(15, events.size());
	}
	
	@Test
	public void test31Update() {
		List<Event> events = eventsRepository.getAll();
		assertEquals(15, events.size());
	}
	
	@Test
	public void test32SafeRemove() {
		List<Event> events = eventsRepository.getAll();
		assertEquals(15, events.size());
	}
}
