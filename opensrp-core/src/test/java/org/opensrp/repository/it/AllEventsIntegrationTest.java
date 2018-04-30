package org.opensrp.repository.it;

import static org.mockito.MockitoAnnotations.initMocks;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensrp.domain.Event;
import org.opensrp.domain.Obs;
import org.opensrp.repository.couch.AllEvents;
import org.opensrp.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-opensrp.xml")
public class AllEventsIntegrationTest {
//TODO Detailed testing
	@Autowired
	private EventService eventService;
	@Autowired
	private AllEvents allEvents;

	@Before
	public void setUp() throws Exception {
		allEvents.removeAll();
		initMocks(this);
	}

	private void addEvents() {
		for (int i = 0; i < 20; i++) {
			Event e = new Event("entityid"+i, "Immunization", new DateTime(), 
					"testentity", "demotest", "location"+i, "formSubmission"+i+100);
			e.addObs(new Obs("concept", "txt", "1025AAAAAAAAAAAAAAAA", null, "2015-01-01" , "comments test"+i, "bcg"));
			e.addObs(new Obs("concept", "txt", "1026AAAAAAAAAAAAAAAA", null, "2015-01-01" , "comments test"+i, "penta1"));
			e.addObs(new Obs("concept", "txt", "1027AAAAAAAAAAAAAAAA", null, "2015-01-01" , "comments test"+i, "penta2"));
			e.addObs(new Obs("concept", "txt", "1028AAAAAAAAAAAAAAAA", null, "2015-01-01" , "comments test"+i, "penta3"));
			e.addObs(new Obs("concept", "txt", "1029AAAAAAAAAAAAAAAA", null, "2015-01-01" , "comments test"+i, "mealses1"));
			e.addObs(new Obs("concept", "txt", "1030AAAAAAAAAAAAAAAA", null, "2015-01-01" , "comments test"+i, "measles2"));
			e.addObs(new Obs("concept", "txt", "1029AAAAAAAAAAAAAAAA", null, "2015-01-01" , "comments test"+i, "tt1"));
			e.addObs(new Obs("concept", "txt", "1030AAAAAAAAAAAAAAAA", null, "2016-02-01" , "comments test"+i, "tt2"));
			eventService.addEvent(e);
		}
	}
	
	@Test
	public void test(){
		addEvents();
		assertTrue(eventService.getByBaseEntityAndFormSubmissionId("entityid0", "formSubmission0100")!=null);
	}
}
