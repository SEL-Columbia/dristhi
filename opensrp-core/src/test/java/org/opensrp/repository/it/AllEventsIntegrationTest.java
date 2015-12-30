package org.opensrp.repository.it;

import static org.mockito.MockitoAnnotations.initMocks;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensrp.domain.Event;
import org.opensrp.repository.AllEvents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-opensrp.xml")
public class AllEventsIntegrationTest {
//TODO Detailed testing
	@Autowired
	private AllEvents allEvents;

	@Before
	public void setUp() throws Exception {
		allEvents.removeAll();
		initMocks(this);
	}

	@Test
	public void test(){
		/*Event e = new Event("testclient1", "eid", "type1", new DateTime(), "pkchild", "provider1", "loc1", "fs1");
		allEvents.add(e);
		
		allEvents.*/
	}
}
