package org.opensrp.repository.it;

import static org.mockito.MockitoAnnotations.initMocks;
import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensrp.domain.Event;
import org.opensrp.repository.AllEvents;
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
			Event e = new Event("entity"+i, "event"+i, "Test Event", new DateTime(), 
					"testentity", "testprovider", "location"+i, "formSubmission"+i);
			eventService.addEvent(e);
		}
	}
	
	@Test
	public void test(){
		addEvents();
		assertTrue(eventService.getByBaseEntityAndFormSubmissionId("entity0", "formSubmission0")!=null);
	}
}
