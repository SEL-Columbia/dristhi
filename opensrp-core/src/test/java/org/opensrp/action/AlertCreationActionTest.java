package org.opensrp.action;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.opensrp.dto.BeneficiaryType.child;
import static org.opensrp.dto.BeneficiaryType.mother;

import java.util.HashMap;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.opensrp.domain.Child;
import org.opensrp.domain.Mother;
import org.opensrp.repository.AllChildren;
import org.opensrp.repository.AllEligibleCouples;
import org.opensrp.repository.AllMothers;
import org.opensrp.scheduler.HealthSchedulerService;
import org.opensrp.scheduler.MilestoneEvent;
import org.opensrp.util.Event;

public class AlertCreationActionTest {
    @Mock
    private HealthSchedulerService scheduler;
    @Mock
    private AllMothers allMothers;
    @Mock
    private AllChildren allChildren;
    @Mock
    private AllEligibleCouples allEligibleCouples;
    
    private DateTime dueWindowStart;
    private DateTime lateWindowStart;
    private DateTime maxWindowStart;

    private AlertCreationAction reminderAction;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        reminderAction = new AlertCreationAction(scheduler, allMothers, allChildren, allEligibleCouples);

        dueWindowStart = DateTime.now();
        lateWindowStart = DateTime.now().plusDays(10);
        maxWindowStart = DateTime.now().plusDays(20);
    }

    @Test
    public void shouldRaiseUpcomingAlertActionsForDueWindowAlerts() throws Exception {
        when(allMothers.findByCaseId("Case 1")).thenReturn(new Mother("Case 1", "EC-CASE-1", "Thayi 1").withAnm("demo1"));

        HashMap<String, String> extraData = new HashMap<>();
        extraData.put("beneficiaryType", "mother");
        reminderAction.invoke(event("Case 1", "Schedule 1", "Milestone 1", WindowName.earliest, dueWindowStart, lateWindowStart, maxWindowStart), extraData);

        verify(scheduler).alertFor(WindowName.earliest.toString(), mother, "Case 1", "demo1", "Schedule 1", "Milestone 1", dueWindowStart, lateWindowStart, maxWindowStart);
    }

    @Test
    public void shouldRaiseNormalAlertActionsForDueWindowAlerts() throws Exception {
        when(allMothers.findByCaseId("Case 1")).thenReturn(new Mother("Case 1", "EC-CASE-1", "Thayi 1").withAnm("demo1"));

        HashMap<String, String> extraData = new HashMap<>();
        extraData.put("beneficiaryType", "mother");
        reminderAction.invoke(event("Case 1", "Schedule 1", "Milestone 1", WindowName.due, dueWindowStart, lateWindowStart, maxWindowStart), extraData);

        verify(scheduler).alertFor(WindowName.due.toString(), mother, "Case 1", "demo1", "Schedule 1", "Milestone 1", dueWindowStart, lateWindowStart, maxWindowStart);
    }

    @Test
    public void shouldRaiseUrgentAlertActionsForLateWindowAlerts() throws Exception {
        when(allChildren.findByCaseId("Case 1")).thenReturn(new Child("Case 1", "M-CASE-1", null, "", "M").withAnm("demo1"));

        HashMap<String, String> extraData = new HashMap<>();
        extraData.put("beneficiaryType", "child");
        reminderAction.invoke(event("Case 1", "Schedule 1", "Milestone 1", WindowName.late, dueWindowStart, lateWindowStart, maxWindowStart), extraData);

        verify(scheduler).alertFor(WindowName.late.toString(), child, "Case 1", "demo1", "Schedule 1", "Milestone 1", dueWindowStart, lateWindowStart, maxWindowStart);
    }

    private MilestoneEvent event(String externalID, String scheduleName, String milestone, WindowName window, DateTime dueWindowStart, DateTime lateWindowStart, DateTime maxWindowStart) {
        return new MilestoneEvent(Event.create().withSchedule(scheduleName).withMilestone(milestone).withWindow(window).withExternalId(externalID)
                .withDueWindowStartDate(dueWindowStart).withLateWindowStartDate(lateWindowStart).withMaxWindowStartDate(maxWindowStart).build());
    }
}
