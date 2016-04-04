package org.opensrp.service.formSubmission;

import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Spy;
import org.opensrp.form.domain.FormSubmission;
import org.opensrp.scheduler.HealthSchedulerService;
import org.opensrp.scheduler.ScheduleConfig;
import org.opensrp.scheduler.service.ScheduleService;
import org.opensrp.util.TestResourceLoader;

import com.google.gson.JsonIOException;

public class FormSubmissionProcessorScheduleTest extends TestResourceLoader{
	public FormSubmissionProcessorScheduleTest() throws IOException {
		super();
	}


	@Mock
	private FormSubmissionProcessor fsp;
	@Mock
	private HealthSchedulerService scheduleService;
	@Mock
	private ScheduleService schService;
	
	@Before
	public void setup() throws IOException, JSONException{
		initMocks(this);
		
		ScheduleConfig scheduleConfig = new ScheduleConfig("/schedules/schedule-config.xls");
		scheduleService = new HealthSchedulerService(null, schService, scheduleConfig );
		fsp = new FormSubmissionProcessor(null, null, null, scheduleService, null, null);
	}
	
	@Test
	public void shouldScheduleP1WhenNoneGivenOnEnrollment() throws JsonIOException, IOException, JSONException {
		FormSubmission fs = getFormSubmissionFor("child_enrollment", 3);
		
		spy(fsp).handleSchedules(fs);
		
		verify(schService).enroll("8cc8cbca-7c39-4c57-b8c7-5ccd5cf88af7", "PENTAVALENT 1", "penta1", "2014-12-13", "1996b940-181e-46d5-bf8f-630bef2880a9");
	}
	
	
}
