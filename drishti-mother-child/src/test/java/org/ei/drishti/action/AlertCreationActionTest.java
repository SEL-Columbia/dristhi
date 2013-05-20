package org.ei.drishti.action;

import org.ei.drishti.scheduler.router.MilestoneEvent;
import org.ei.drishti.service.ActionService;
import org.ei.drishti.util.Event;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.scheduletracking.api.domain.WindowName;

import java.util.HashMap;

import static org.ei.drishti.dto.AlertPriority.upcoming;
import static org.ei.drishti.dto.BeneficiaryType.child;
import static org.ei.drishti.dto.BeneficiaryType.mother;
import static org.ei.drishti.dto.AlertPriority.normal;
import static org.ei.drishti.dto.AlertPriority.urgent;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class AlertCreationActionTest {
    @Mock
    private ActionService actionService;

    private DateTime dueWindowStart;
    private DateTime lateWindowStart;
    private DateTime maxWindowStart;

    private AlertCreationAction reminderAction;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        reminderAction = new AlertCreationAction(actionService);

        dueWindowStart = DateTime.now();
        lateWindowStart = DateTime.now().plusDays(10);
        maxWindowStart = DateTime.now().plusDays(20);
    }

    @Test
    public void shouldRaiseUpcomingAlertActionsForDueWindowAlerts() throws Exception {
        HashMap<String, String> extraData = new HashMap<>();
        extraData.put("beneficiaryType", "mother");
        reminderAction.invoke(event("Case 1", "Schedule 1", "Milestone 1", WindowName.earliest, dueWindowStart, lateWindowStart, maxWindowStart), extraData);

        verify(actionService).alertForBeneficiary(mother, "Case 1", "Schedule 1", "Milestone 1", upcoming, dueWindowStart, lateWindowStart);
    }

    @Test
    public void shouldRaiseNormalAlertActionsForDueWindowAlerts() throws Exception {
        HashMap<String, String> extraData = new HashMap<>();
        extraData.put("beneficiaryType", "mother");
        reminderAction.invoke(event("Case 1", "Schedule 1", "Milestone 1", WindowName.due, dueWindowStart, lateWindowStart, maxWindowStart), extraData);

        verify(actionService).alertForBeneficiary(mother, "Case 1", "Schedule 1", "Milestone 1", normal, dueWindowStart, lateWindowStart);
    }

    @Test
    public void shouldRaiseUrgentAlertActionsForLateWindowAlerts() throws Exception {
        HashMap<String, String> extraData = new HashMap<>();
        extraData.put("beneficiaryType", "child");
        reminderAction.invoke(event("Case 1", "Schedule 1", "Milestone 1", WindowName.late, dueWindowStart, lateWindowStart, maxWindowStart), extraData);

        verify(actionService).alertForBeneficiary(child, "Case 1", "Schedule 1", "Milestone 1", urgent, lateWindowStart, maxWindowStart);
    }

    private MilestoneEvent event(String externalID, String scheduleName, String milestone, WindowName window, DateTime dueWindowStart, DateTime lateWindowStart, DateTime maxWindowStart) {
        return new MilestoneEvent(Event.create().withSchedule(scheduleName).withMilestone(milestone).withWindow(window).withExternalId(externalID)
                .withDueWindowStartDate(dueWindowStart).withLateWindowStartDate(lateWindowStart).withMaxWindowStartDate(maxWindowStart).build());
    }
}
