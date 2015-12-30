package org.opensrp.repository.it;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensrp.scheduler.service.ScheduleService;
import org.opensrp.util.TestResourceLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-opensrp.xml")
public class ScheduleLifeCycleTest extends TestResourceLoader{

	public ScheduleLifeCycleTest() throws IOException {
		super();
	}
	
	@Autowired
	private ScheduleService scheduleService;
	
	@Test
	public void name() {
		
	}

}
