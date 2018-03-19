package org.opensrp.repository.postgres;

import static org.junit.Assert.*;

import java.util.List;

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
	public void test4findAllByIdentifierAndType() {
		assertTrue(eventsRepository.findAllByIdentifier(null, null).isEmpty());
		List<Event> events = eventsRepository.findAllByIdentifier("OPENMRS_UUID", "06c8644b-b560-45fd-9af5-b6b1484e3504");
		assertEquals("d59504cc-09ef-4d09-9dc3-8f7eb65882fd", events.get(0).getFormSubmissionId());
		assertEquals("05934ae338431f28bf6793b241bdb88c", events.get(0).getId());
	}
	
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
