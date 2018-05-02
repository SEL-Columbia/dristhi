package org.opensrp.service.formSubmission;

import com.google.gson.JsonIOException;
import org.joda.time.LocalDate;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.opensrp.form.domain.FormSubmission;
import org.opensrp.scheduler.HealthSchedulerService;
import org.opensrp.scheduler.ScheduleConfig;
import org.opensrp.scheduler.service.ScheduleService;
import org.opensrp.util.TestResourceLoader;

import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class FormSubmissionProcessorScheduleTest extends TestResourceLoader {
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
    public void setup() throws IOException, JSONException {
        initMocks(this);

        ScheduleConfig scheduleConfig = new ScheduleConfig("/schedules/schedule-config.xls");
        scheduleService = new HealthSchedulerService(null, schService, scheduleConfig);
        fsp = new FormSubmissionProcessor(null, null, null, scheduleService, null, null,null,null);
    }

    @Test
    public void shouldScheduleP1WhenNoneGivenOnEnrollment() throws JsonIOException, IOException, JSONException {
        FormSubmission fs = getFormSubmissionFor("child_enrollment", 3);

        spy(fsp).handleSchedules(fs);

        verify(schService).enroll("8cc8cbca-7c39-4c57-b8c7-5ccd5cf88af7", "PENTAVALENT 1", "penta1", "2014-12-13", "1996b940-181e-46d5-bf8f-630bef2880a9");
    }

    @Test
    public void shouldEnrollScheduleProperly() throws IOException, JSONException {
        FormSubmission fs = getFormSubmissionFor("child_followup");
        HealthSchedulerService mockScheduleService = mock(HealthSchedulerService.class);
        when(mockScheduleService.findAutomatedSchedules(fs.formName())).thenReturn(scheduleService.findAutomatedSchedules(fs.formName()));
        fsp = new FormSubmissionProcessor(null, null, null, mockScheduleService, null, null,null,null);

        fsp.handleSchedules(fs);

        verify(mockScheduleService).fullfillMilestoneAndCloseAlert("101aab44-5377-4846-95ad-442b857b54d2", fs.anmId(),
                "PENTAVALENT 1", new LocalDate("2016-09-20"), fs.instanceId());
        verify(mockScheduleService).unEnrollFromSchedule("101aab44-5377-4846-95ad-442b857b54d2", fs.anmId(),
                "PENTAVALENT 1", fs.instanceId());
        verify(mockScheduleService).enrollIntoSchedule("101aab44-5377-4846-95ad-442b857b54d2", "PENTAVALENT 2",
                "penta2", "2016-09-20", fs.instanceId());
    }

    @Test
    public void shouldEnrollScheduleForSubforms() throws IOException, JSONException {
        FormSubmission fs = getFormSubmissionFor("new_household_registration", 2);
        HealthSchedulerService mockScheduleService = mock(HealthSchedulerService.class);
        when(mockScheduleService.findAutomatedSchedules(fs.formName())).thenReturn(scheduleService.findAutomatedSchedules(fs.formName()));
        fsp = new FormSubmissionProcessor(null, null, null, mockScheduleService, null, null,null,null);

        fsp.handleSchedules(fs);

        verify(mockScheduleService).enrollIntoSchedule("0aac6d81-b51f-4096-b354-5a5786e406c8", "FW CENSUS",
                "FW CENSUS", "2015-05-06", fs.instanceId());
        verify(mockScheduleService).enrollIntoSchedule("b19db74f-6e96-4652-a765-5078beb12434", "Psrf",
                "psrf", "2000-12-12", fs.instanceId());
    }


}
