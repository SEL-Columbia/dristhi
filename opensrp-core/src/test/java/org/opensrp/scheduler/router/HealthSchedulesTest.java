package org.opensrp.scheduler.router;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;
import java.util.List;

import org.hamcrest.Matchers;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.opensrp.form.domain.FormSubmission;
import org.opensrp.scheduler.HealthSchedulerService;
import org.opensrp.scheduler.Schedule;
import org.opensrp.scheduler.Schedule.ActionType;
import org.opensrp.scheduler.ScheduleConfig;
import org.opensrp.scheduler.service.ScheduleService;
import org.opensrp.util.TestResourceLoader;

import com.google.gson.JsonIOException;

public class HealthSchedulesTest extends TestResourceLoader{
	public HealthSchedulesTest() throws IOException {
		super();
	}

	private HealthSchedulerService sch;
	private ScheduleConfig scheduleConfig;
	@Mock
	private ScheduleService schService;
	
	@Before
	public void setUp() throws IOException, JSONException {
		initMocks(this);
		
		scheduleConfig = new ScheduleConfig("/schedules/schedule-config.xls");
		sch = new HealthSchedulerService(null, schService, scheduleConfig);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void test() throws JsonIOException, IOException {
		FormSubmission fs = getFormSubmissionFor("child_enrollment", 3);
		List<Schedule> s = sch.findAutomatedSchedules(fs.formName());
		assertThat(s, hasItem(Matchers.<Schedule>allOf(
				Matchers.<Schedule>hasProperty("schedule",equalTo("PENTAVALENT 1")),
				Matchers.<Schedule>hasProperty("milestone",equalTo("pentavalent_1")),
				Matchers.<Schedule>hasProperty("action",equalTo(ActionType.enroll)),
				Matchers.<Schedule>hasProperty("entityType",equalTo("pkchild")),
				Matchers.<Schedule>hasProperty("triggerDateFields",hasItem("child_birth_date"))
				)));
	}
	
	@Test
	public void test2() {
	}
}
