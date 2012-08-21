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

public class CapturePNCReminderActionTest {
    @Mock
    private ActionService actionService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldCreateAnAlertForChildForEveryCapturedReminder() {
        CapturePNCReminderAction action = new CapturePNCReminderAction(actionService);

        DateTime dueDate = DateTime.now();
        action.invoke(event("Case X", "Schedule 1", "Milestone 1", WindowName.due, dueDate));

        verify(actionService).alertForChild("Case X", "Milestone 1", "due", dueDate);
    }

    private MilestoneEvent event(String externalID, String scheduleName, String milestone, WindowName window, DateTime dueDate) {
        return new MilestoneEvent(Event.create().withSchedule(scheduleName).withMilestone(milestone).withWindow(window).withExternalId(externalID).withDueWindowStartDate(dueDate).build());
    }

}
