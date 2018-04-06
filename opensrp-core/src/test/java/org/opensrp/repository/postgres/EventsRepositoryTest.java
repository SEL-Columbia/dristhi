package org.opensrp.repository.postgres;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Test;
import org.opensrp.common.AllConstants.BaseEntity;
import org.opensrp.domain.Event;
import org.opensrp.domain.Obs;
import org.opensrp.repository.EventsRepository;
import org.opensrp.search.EventSearchBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class EventsRepositoryTest extends BaseRepositoryTest {
	
	@Autowired
	@Qualifier("eventsRepositoryPostgres")
	private EventsRepository eventsRepository;
	
	@Override
	protected Set<String> getDatabaseScripts() {
		Set<String> scripts = new HashSet<String>();
		scripts.add("event.sql");
		return scripts;
	}
	
	@Test
	public void testGet() {
		Event event = eventsRepository.get("05934ae338431f28bf6793b2419c319a");
		assertEquals("ea1f9439-a663-4073-93b9-6ef2b8bca3c1", event.getBaseEntityId());
		assertEquals("d960046a-e2a0-4bbf-b687-d41c2a52d8c8", event.getFormSubmissionId());
		assertEquals("Vaccination", event.getEventType());
		//find non existent event
		assertNull(eventsRepository.get("05934ae338431f28bf4234gvnbvvh"));
		assertNull(eventsRepository.get(null));
	}
	
	@Test
	public void testGetAll() {
		List<Event> events = eventsRepository.getAll();
		assertEquals(15, events.size());
		
		eventsRepository.safeRemove(eventsRepository.findById("05934ae338431f28bf6793b2419c319a"));
		events = eventsRepository.getAll();
		assertEquals(14, events.size());
		for (Event event : events)
			assertNotEquals("05934ae338431f28bf6793b2419c319a", event.getId());
	}
	
	@Test
	public void testFindAllByIdentifier() {
		assertTrue(eventsRepository.findAllByIdentifier(null).isEmpty());
		List<Event> events = eventsRepository.findAllByIdentifier("06c8644b-b560-45fd-9af5-b6b1484e3504");
		assertEquals(1, events.size());
		assertEquals("d59504cc-09ef-4d09-9dc3-8f7eb65882fd", events.get(0).getFormSubmissionId());
		assertEquals("05934ae338431f28bf6793b241bdb88c", events.get(0).getId());
	}
	
	@Test
	public void testFindAllByIdentifierAndType() {
		assertTrue(eventsRepository.findAllByIdentifier(null, null).isEmpty());
		List<Event> events = eventsRepository.findAllByIdentifier("OPENMRS_UUID", "06c8644b-b560-45fd-9af5-b6b1484e3504");
		assertEquals(1, events.size());
		assertEquals("d59504cc-09ef-4d09-9dc3-8f7eb65882fd", events.get(0).getFormSubmissionId());
		assertEquals("05934ae338431f28bf6793b241bdb88c", events.get(0).getId());
		
		assertTrue(eventsRepository.findAllByIdentifier("OPENMRS", "06c8644b-b560-45fd-9af5-b6b1484e3504").isEmpty());
	}
	
	@Test
	public void testFindById() {
		Event event = eventsRepository.findById("05934ae338431f28bf6793b2419c319a");
		assertEquals("ea1f9439-a663-4073-93b9-6ef2b8bca3c1", event.getBaseEntityId());
		assertEquals("d960046a-e2a0-4bbf-b687-d41c2a52d8c8", event.getFormSubmissionId());
		assertEquals("Vaccination", event.getEventType());
		//find non existent event
		assertNull(eventsRepository.findById("05934ae338431f28bf4234gvnbvvh"));
		assertNull(eventsRepository.findById(null));
	}
	
	@Test
	public void testFindByFormSubmissionId() {
		Event event = eventsRepository.findByFormSubmissionId("31c4a45a-09f4-4b01-abe8-a87526827df6");
		assertEquals("ea1f9439-a663-4073-93b9-6ef2b8bca3c1", event.getBaseEntityId());
		assertEquals("05934ae338431f28bf6793b241781149", event.getId());
		assertEquals("Growth Monitoring", event.getEventType());
		//find non existent event
		assertNull(eventsRepository.findByFormSubmissionId("05934ae338431f28bf4234gvnbvvh"));
		assertNull(eventsRepository.findByFormSubmissionId(null));
	}
	
	@Test
	public void testFindByBaseEntityId() {
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
	public void testFindByBaseEntityAndFormSubmissionId() {
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
	public void testFindByBaseEntityAndType() {
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
	public void testFindEvents() {
		
		EventSearchBean eventSearchBean = new EventSearchBean();
		eventSearchBean.setBaseEntityId("58b33379-dab2-4f5c-8f09-6d2bd63023d8");
		List<Event> events = eventsRepository.findEvents(eventSearchBean);
		assertEquals(7, events.size());
		
		DateTime from = new DateTime("2018-01-10T11:59:37.380");
		DateTime to = new DateTime("2018-02-21T12:00:08.788");
		eventSearchBean.setEventDateFrom(from);
		eventSearchBean.setEventDateTo(to);
		events = eventsRepository.findEvents(eventSearchBean);
		assertEquals(6, events.size());
		
		eventSearchBean.setEventType("Vaccination");
		events = eventsRepository.findEvents(eventSearchBean);
		assertEquals(6, events.size());
		
		eventSearchBean.setEntityType("vaccination");
		events = eventsRepository.findEvents(eventSearchBean);
		assertEquals(6, events.size());
		
		eventSearchBean.setProviderId("biddemo");
		eventSearchBean.setLocationId("42abc582-6658-488b-922e-7be500c070f3");
		events = eventsRepository.findEvents(eventSearchBean);
		assertEquals(6, events.size());
		
		eventSearchBean.setTeam("ATeam");
		eventSearchBean.setTeamId("3453hgb454-4j345n-llk345");
		events = eventsRepository.findEvents(eventSearchBean);
		assertEquals(2, events.size());
		
		for (Event event : events) {
			assertEquals("3453hgb454-4j345n-llk345", event.getTeamId());
			assertEquals("ATeam", event.getTeam());
			assertEquals("42abc582-6658-488b-922e-7be500c070f3", event.getLocationId());
			assertEquals("biddemo", event.getProviderId());
			assertEquals("vaccination", event.getEntityType());
			assertEquals("Vaccination", event.getEventType());
			assertEquals("58b33379-dab2-4f5c-8f09-6d2bd63023d8", event.getBaseEntityId());
			assertTrue(event.getEventDate().isEqual(from) || event.getEventDate().isAfter(from));
			assertTrue(event.getEventDate().isEqual(to) || event.getEventDate().isBefore(to));
		}
		
		eventSearchBean.setLastEditFrom(new DateTime());
		eventSearchBean.setLastEditTo(new DateTime());
		events = eventsRepository.findEvents(eventSearchBean);
		assertTrue(events.isEmpty());
		
		DateTime editFrom = new DateTime("2018-03-16T10:03:01.537+03:00");
		DateTime editTo = new DateTime("2018-03-19T17:17:15.929+03:00");
		eventSearchBean.setLastEditFrom(editFrom);
		eventSearchBean.setLastEditTo(editTo);
		events = eventsRepository.findEvents(eventSearchBean);
		assertEquals(2, events.size());
		for (Event event : events) {
			assertTrue(event.getDateEdited().equals(editFrom) || event.getDateEdited().isAfter(editFrom));
			assertTrue(event.getDateEdited().equals(editTo) || event.getDateEdited().isBefore(editTo));
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testFindEventsByDynamicQuery() {
		eventsRepository.findEventsByDynamicQuery("baseEntityId:4234324");
	}
	
	@Test
	public void testFindByServerVersion() {
		assertEquals(15, eventsRepository.findByServerVersion(0).size());
		List<Event> events = eventsRepository.findByServerVersion(1521469045587l);
		assertEquals(3, events.size());
		List<String> expectedIds = Arrays.asList("05934ae338431f28bf6793b241780bac", "05934ae338431f28bf6793b241781149",
		    "05934ae338431f28bf6793b241781a1e");
		for (Event event : events) {
			assertTrue(event.getServerVersion() >= 1521469045587l);
			assertTrue(expectedIds.contains(event.getId()));
		}
		
		assertTrue(eventsRepository.findByServerVersion(1521469045597l).isEmpty());
	}
	
	@Test
	public void testNotInOpenMRSByServerVersion() {
		Calendar cal = Calendar.getInstance();
		assertEquals(7, eventsRepository.notInOpenMRSByServerVersion(0, cal).size());
		
		cal.setTimeInMillis(1521469045589l);
		List<Event> events = eventsRepository.notInOpenMRSByServerVersion(1521469045588l, cal);
		assertEquals(2, events.size());
		
		List<String> expectedIds = Arrays.asList("05934ae338431f28bf6793b241780bac", "05934ae338431f28bf6793b241781149");
		for (Event event : events) {
			assertTrue(event.getServerVersion() >= 1521469045588l && event.getServerVersion() <= cal.getTimeInMillis());
			assertTrue(expectedIds.contains(event.getId()));
		}
		
		assertTrue(eventsRepository.notInOpenMRSByServerVersion(1521469045597l, cal).isEmpty());
	}
	
	@Test
	public void testNotInOpenMRSByServerVersionAndType() {
		Calendar cal = Calendar.getInstance();
		assertEquals(4, eventsRepository.notInOpenMRSByServerVersionAndType("Growth Monitoring", 0, cal).size());
		
		cal.setTimeInMillis(1521469045589l);
		List<Event> events = eventsRepository.notInOpenMRSByServerVersionAndType("Growth Monitoring", 1521469045588l, cal);
		List<String> expectedIds = Arrays.asList("05934ae338431f28bf6793b241780bac", "05934ae338431f28bf6793b241781149");
		assertEquals(2, events.size());
		for (Event event : events) {
			assertTrue(event.getServerVersion() >= 1521469045588l && event.getServerVersion() <= cal.getTimeInMillis());
			assertTrue(expectedIds.contains(event.getId()));
			assertEquals("Growth Monitoring", event.getEventType());
		}
		
		assertTrue(eventsRepository.notInOpenMRSByServerVersion(1521469045597l, cal).isEmpty());
	}
	
	@Test
	public void testFindByClientAndConceptAndDate() {
		List<Event> events = eventsRepository.findByClientAndConceptAndDate("58b33379-dab2-4f5c-8f09-6d2bd63023d8",
		    "1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "1", "2018-01-10", new DateTime().toString("yyyy-MM-dd"));
		assertEquals(5, events.size());
		for (Event event : events) {
			assertTrue(event.getEventDate().equals(new DateTime("2018-01-10"))
			        || event.getEventDate().isAfter(new DateTime("2018-01-10")));
			assertEquals("58b33379-dab2-4f5c-8f09-6d2bd63023d8", event.getBaseEntityId());
			assertEquals("1", events.get(0).getObs(null, "1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA").getValues().get(0));
		}
		
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
		assertEquals("58b33379-dab2-4f5c-8f09-6d2bd63023d8", events.get(0).getBaseEntityId());
		assertEquals("05934ae338431f28bf6793b241bdb88c", events.get(0).getId());
		assertEquals("Happy Kids Clinic",
		    events.get(0).getObs(null, "163531AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA").getValues().get(0));
		assertTrue(events.get(0).getDateCreated().equals(new DateTime("2018-03-19"))
		        || events.get(0).getDateCreated().isAfter(new DateTime("2018-03-19")));
		
	}
	
	@Test
	public void testFindByBaseEntityIdAndConceptParentCode() {
		List<Event> events = eventsRepository.findByBaseEntityIdAndConceptParentCode("58b33379-dab2-4f5c-8f09-6d2bd63023d8",
		    "1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "783AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		assertEquals(2, events.size());
		
		events = eventsRepository.findByBaseEntityIdAndConceptParentCode("58b33379-dab2-4f5c-8f09-6d2bd63023d8",
		    "1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "886AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		
		assertEquals(1, events.size());
		assertEquals("05934ae338431f28bf6793b241bdbc55", events.get(0).getId());
		boolean found = false;
		for (Obs obs : events.get(0).getObs()) {
			if (obs.getParentCode().equals("886AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
			        && obs.getFieldCode().equals("1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")) {
				found = true;
				break;
			}
		}
		assertTrue(found);
		
		events = eventsRepository.findByBaseEntityIdAndConceptParentCode("58b33379-dab2-4f5c-8f09-6d2bd63023d8",
		    "1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "886AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		
		assertTrue(events.isEmpty());
	}
	
	@Test
	public void testFindByConceptAndValue() {
		List<Event> events = eventsRepository.findByConceptAndValue("1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "1");
		assertEquals(8, events.size());
		
		events = eventsRepository.findByConceptAndValue("1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "5");
		
		assertTrue(events.isEmpty());
		
		events = eventsRepository.findByConceptAndValue("1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "2018-02-21");
		
		assertEquals(4, events.size());
		for (Event event : events) {
			assertEquals("2018-02-21", event.getObs(null, "1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA").getValues().get(0));
		}
		
		events = eventsRepository.findByConceptAndValue("163531AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "Happy Kids Clinic");
		
		assertEquals(1, events.size());
		assertEquals("58b33379-dab2-4f5c-8f09-6d2bd63023d8", events.get(0).getBaseEntityId());
		assertEquals("05934ae338431f28bf6793b241bdb88c", events.get(0).getId());
		
	}
	
	@Test
	public void testFindEvents2() {
		EventSearchBean eventSearchBean = new EventSearchBean();
		eventSearchBean.setTeam("ATeam");
		eventSearchBean.setTeamId("3453hgb454-4j345n-llk345");
		List<Event> events = eventsRepository.findEvents(eventSearchBean, null, null, 20);
		assertEquals(2, events.size());
		
		eventSearchBean = new EventSearchBean();
		eventSearchBean.setTeam("ATeam,BTeam");
		events = eventsRepository.findEvents(eventSearchBean, null, null, 20);
		assertEquals(3, events.size());
		
		eventSearchBean = new EventSearchBean();
		eventSearchBean.setProviderId("biddemo,biddemo2");
		eventSearchBean.setLocationId("42b88545-7ebb-4e11-8d1a-3d3a924c8af4,42b88545-7ebb-4e11-8d1a-3d3a924c8af5");
		events = eventsRepository.findEvents(eventSearchBean, null, null, 20);
		assertEquals(7, events.size());
		
		eventSearchBean.setBaseEntityId("58b33379-dab2-4f5c-8f09-6d2bd63023d8");
		events = eventsRepository.findEvents(eventSearchBean, null, null, 20);
		assertEquals(0, events.size());
		
		eventSearchBean.setLocationId("42abc582-6658-488b-922e-7be500c070f3");
		events = eventsRepository.findEvents(eventSearchBean, null, null, 20);
		assertEquals(7, events.size());
		
		eventSearchBean.setTeam("ATeam,BTeam");
		eventSearchBean.setTeamId("3453hgb454-4j345n-llk345,3453hgb454-4j345n-llk348");
		eventSearchBean.setProviderId("biddemo");
		eventSearchBean.setServerVersion(0l);
		events = eventsRepository.findEvents(eventSearchBean, BaseEntity.SERVER_VERSIOIN, "asc", 20);
		assertEquals(3, events.size());
		
		long previousVersion = 0;
		for (Event event : events) {
			assertTrue(event.getTeam().equals("ATeam") || event.getTeam().equals("BTeam"));
			assertTrue(event.getTeamId().equals("3453hgb454-4j345n-llk345")
			        || event.getTeamId().equals("3453hgb454-4j345n-llk348"));
			assertEquals("42abc582-6658-488b-922e-7be500c070f3", event.getLocationId());
			assertEquals("biddemo", event.getProviderId());
			assertTrue(event.getServerVersion() >= previousVersion);
			previousVersion = event.getServerVersion();
		}
	}
	
	@Test
	public void testUpdate() {
		Event event = eventsRepository.get("05934ae338431f28bf6793b2419c64fb");
		long now = System.currentTimeMillis();
		event.setServerVersion(now);
		event.setDateEdited(new DateTime(now));
		Obs obs = new Obs("concept", "text", "1730AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, "25-Apr-2017", null,
		        "Date_Reaction");
		event.addObs(obs);
		eventsRepository.update(event);
		event = eventsRepository.get("05934ae338431f28bf6793b2419c64fb");
		assertEquals(now, event.getServerVersion().longValue());
		assertEquals(new DateTime(now), event.getDateEdited());
		assertEquals(3, event.getObs().size());
		assertEquals(obs.getValue(), event.getObs(null, "1730AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA").getValue());
	}
	
	@Test
	public void testFindByEmptyServerVersion() {
		assertEquals(0, eventsRepository.findByEmptyServerVersion().size());
		Event event = eventsRepository.get("05934ae338431f28bf6793b241bdb88c");
		event.setServerVersion(0l);
		eventsRepository.update(event);
		
		event = eventsRepository.get("05934ae338431f28bf6793b241bdbb60");
		event.setServerVersion(null);
		eventsRepository.update(event);
		long beforeFetch = System.currentTimeMillis();
		List<Event> events = eventsRepository.findByEmptyServerVersion();
		assertEquals(2, events.size());
		for (Event loopEvent : events) {
			assertTrue(loopEvent.getId().equals("05934ae338431f28bf6793b241bdb88c")
			        || loopEvent.getId().equals("05934ae338431f28bf6793b241bdbb60"));
			//if serverVersion is null will automatically be set to current timestamp in org.opensrp.domain.BaseDataObject.serverVersion
			assertTrue(loopEvent.getServerVersion() == 0 || loopEvent.getServerVersion() >= beforeFetch);
			
		}
	}
	
	@Test
	public void testFindEventByEventTypeBetweenTwoDates() {
		List<Event> events = eventsRepository.getAll();
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, 1);
		for (Event event : events) {
			event.setServerVersion(cal.getTimeInMillis());
			eventsRepository.update(event);
		}
		assertEquals(9, eventsRepository.findEventByEventTypeBetweenTwoDates("Vaccination").size());
		assertEquals(4, eventsRepository.findEventByEventTypeBetweenTwoDates("Growth Monitoring").size());
		events = eventsRepository.findEventByEventTypeBetweenTwoDates("Birth Registration");
		assertEquals(1, events.size());
		assertEquals("58b33379-dab2-4f5c-8f09-6d2bd63023d8", events.get(0).getBaseEntityId());
		assertEquals("05934ae338431f28bf6793b241bdb88c", events.get(0).getId());
	}
	
	@Test
	public void testSafeRemove() {
		Event event = eventsRepository.get("05934ae338431f28bf6793b241bdb88c");
		eventsRepository.safeRemove(event);
		List<Event> events = eventsRepository.getAll();
		assertEquals(14, events.size());
		for (Event e : events)
			assertNotEquals("05934ae338431f28bf6793b241bdb88c", e.getId());
		assertNull(eventsRepository.get("05934ae338431f28bf6793b241bdb88c"));
	}
	
	@Test
	public void testAdd() {
		Obs obs = new Obs("concept", "decimal", "1730AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, "3.5", null, "weight");
		Event event = new Event().withBaseEntityId("435534534543").withEventType("Growth Monitoring")
		        .withFormSubmissionId("gjhg34534 nvbnv3345345__4").withEventDate(new DateTime()).withObs(obs);
		
		eventsRepository.add(event);
		
		event = eventsRepository.findByFormSubmissionId("gjhg34534 nvbnv3345345__4");
		assertEquals("435534534543", event.getBaseEntityId());
		assertEquals("Growth Monitoring", event.getEventType());
		assertEquals(1, event.getObs().size());
		assertEquals("3.5", event.getObs(null, "1730AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA").getValue());
		
	}
	
}
