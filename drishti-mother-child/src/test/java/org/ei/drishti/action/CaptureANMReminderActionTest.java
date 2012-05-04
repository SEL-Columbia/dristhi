package org.ei.drishti.action;

import org.ei.drishti.scheduler.router.MilestoneEvent;
import org.ei.drishti.service.ActionService;
import org.ei.drishti.util.Event;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.scheduletracking.api.domain.WindowName;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class CaptureANMReminderActionTest {
    @Mock
    private ActionService actionService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldCaptureAlertData() throws Exception {
        CaptureANMReminderAction reminderAction = new CaptureANMReminderAction(actionService);

        DateTime dueDate = DateTime.now();
        reminderAction.invoke(event("Case 1", "Schedule 1", "Milestone 1", WindowName.due, dueDate));

        verify(actionService).alertForMother("Case 1", "Milestone 1", "due", dueDate);
    }

    private MilestoneEvent event(String externalID, String scheduleName, String milestone, WindowName window, DateTime dueDate) {
        return new MilestoneEvent(Event.create().withSchedule(scheduleName).withMilestone(milestone).withWindow(window).withExternalId(externalID).withDueDate(dueDate).build());
    }
}
