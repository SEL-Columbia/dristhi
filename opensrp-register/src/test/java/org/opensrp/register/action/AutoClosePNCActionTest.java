package org.opensrp.register.action;

import org.opensrp.scheduler.router.MilestoneEvent;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.scheduletracking.api.domain.WindowName;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import org.opensrp.register.action.AutoClosePNCAction;
import org.opensrp.register.service.PNCService;
import org.opensrp.register.util.Event;

public class AutoClosePNCActionTest {
    @Mock
    private PNCService pncService;

    private DateTime dueWindowStart;
    private DateTime lateWindowStart;
    private DateTime maxWindowStart;

    private AutoClosePNCAction action;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        action = new AutoClosePNCAction(pncService);

        dueWindowStart = DateTime.now();
        lateWindowStart = DateTime.now().plusDays(10);
        maxWindowStart = DateTime.now().plusDays(20);
    }

    @Test
    public void shouldDelegateCloseActionToPNCServiceForDueWindowAlerts() throws Exception {
        action.invoke(event("Case 1", "Schedule 1", "Milestone 1", WindowName.due, dueWindowStart, lateWindowStart, maxWindowStart), null);

        verify(pncService).autoClosePNCCase("Case 1");
    }

    private MilestoneEvent event(String externalID, String scheduleName, String milestone, WindowName window,
                                 DateTime dueWindowStart, DateTime lateWindowStart, DateTime maxWindowStart) {
        return new MilestoneEvent(Event.create()
                .withSchedule(scheduleName)
                .withMilestone(milestone)
                .withWindow(window)
                .withExternalId(externalID)
                .withDueWindowStartDate(dueWindowStart)
                .withLateWindowStartDate(lateWindowStart)
                .withMaxWindowStartDate(maxWindowStart)
                .build());
    }
}
