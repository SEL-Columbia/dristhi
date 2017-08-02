package org.opensrp.repository.it;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.EnrollmentStatus;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.opensrp.domain.Client;
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
	
	@Autowired
	private AllEnrollments allEnrollments;
	
	@Test
	public void name() {
		List<String> keys = new ArrayList<String>();
//		Enrollment l2 = allEnrollments.getActiveEnrollment("4634", "hshdsd");
//		l2.toString();
	}

}
