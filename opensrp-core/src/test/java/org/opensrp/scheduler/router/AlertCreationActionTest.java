package org.opensrp.scheduler.router;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.opensrp.util.ScheduleBuilder.*;

import java.io.IOException;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.scheduletracking.api.domain.EnrollmentStatus;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.opensrp.form.domain.FormSubmission;
import org.opensrp.form.service.FormSubmissionService;
import org.opensrp.scheduler.AlertCreationAction;
import org.opensrp.scheduler.HealthSchedulerService;
import org.opensrp.util.TestResourceLoader;

public class AlertCreationActionTest extends TestResourceLoader{
    public AlertCreationActionTest() throws IOException {
		super();
	}

	@Mock
    private HealthSchedulerService scheduler;
    
    private DateTime dueWindowStart;
    private DateTime lateWindowStart;
    private DateTime maxWindowStart;

    private AlertCreationAction reminderAction;

    @Mock
	private FormSubmissionService formSubmissionService;

	private FormSubmission fs;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        reminderAction = new AlertCreationAction(scheduler, formSubmissionService);

        dueWindowStart = DateTime.now();
        lateWindowStart = DateTime.now().plusDays(10);
        maxWindowStart = DateTime.now().plusDays(20);
        
        fs = getFormSubmissionFor("child_enrollment", 1);
        when(formSubmissionService.findByInstanceId(fs.instanceId())).thenReturn(fs);
        when(scheduler.getEnrollment(fs.entityId(), "PENTAVALENT 1"))
        		.thenReturn(enrollment(fs.entityId(), "PENTAVALENT 1", "pentavalent_1", 
        				null, null, EnrollmentStatus.ACTIVE, fs.instanceId()));
    }

    @Test
    public void shouldRaiseUpcomingAlertActionsForDueWindowAlerts() throws Exception {
        reminderAction.invoke(event(fs.entityId(), "PENTAVALENT 1", "pentavalent_1", WindowName.earliest, dueWindowStart, lateWindowStart, maxWindowStart), null);

        verify(scheduler).alertFor(WindowName.earliest.toString(), "pkchild", fs.entityId(), "demotest", "PENTAVALENT 1", "pentavalent_1", dueWindowStart, lateWindowStart, maxWindowStart);
    }

    @Test
    public void shouldRaiseNormalAlertActionsForDueWindowAlerts() throws Exception {
        reminderAction.invoke(event(fs.entityId(), "PENTAVALENT 1", "pentavalent_1", WindowName.due, dueWindowStart, lateWindowStart, maxWindowStart), null);

        verify(scheduler).alertFor(WindowName.due.toString(), "pkchild", fs.entityId(), "demotest", "PENTAVALENT 1", "pentavalent_1", dueWindowStart, lateWindowStart, maxWindowStart);
    }

    @Test
    public void shouldRaiseUrgentAlertActionsForLateWindowAlerts() throws Exception {
        reminderAction.invoke(event(fs.entityId(), "PENTAVALENT 1", "pentavalent_1", WindowName.late, dueWindowStart, lateWindowStart, maxWindowStart), null);

        verify(scheduler).alertFor(WindowName.late.toString(), "pkchild", fs.entityId(), "demotest", "PENTAVALENT 1", "pentavalent_1", dueWindowStart, lateWindowStart, maxWindowStart);
    }
    
    @Test
    public void shouldRaiseExpiredAlertActionsForMaxWindowAlerts() throws Exception {
        reminderAction.invoke(event(fs.entityId(), "PENTAVALENT 1", "pentavalent_1", WindowName.max, dueWindowStart, lateWindowStart, maxWindowStart), null);

        verify(scheduler).alertFor(WindowName.max.toString(), "pkchild", fs.entityId(), "demotest", "PENTAVALENT 1", "pentavalent_1", dueWindowStart, lateWindowStart, maxWindowStart);
    }
}
