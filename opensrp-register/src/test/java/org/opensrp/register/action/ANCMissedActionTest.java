package org.opensrp.register.action;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.scheduletracking.api.events.constants.EventDataKeys.EXTERNAL_ID;
import static org.motechproject.scheduletracking.api.events.constants.EventDataKeys.SCHEDULE_NAME;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.scheduler.domain.MotechEvent;
import org.opensrp.register.ziggy.scheduling.ANCSchedulesService;
import org.opensrp.scheduler.AlertRouter;
import org.opensrp.scheduler.MilestoneEvent;

public class ANCMissedActionTest {
    @Mock
    private ANCSchedulesService schedulesService;
    @Mock
	private AlertRouter alertRouter;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldMentionThatANCVisitHasBeenMissedForScheduleAndExternalIdSpecifiedByEvent() {
        ForceFulfillAction action = new ForceFulfillAction(schedulesService, alertRouter);

        action.invoke(event("Schedule X", "Case Y"), new HashMap<String, String>());

        verify(schedulesService).forceFulfillMilestone("Case Y", "Schedule X");
    }

    private MilestoneEvent event(String scheduleName, String externalId) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(SCHEDULE_NAME, scheduleName);
        parameters.put(EXTERNAL_ID, externalId);

        return new MilestoneEvent(new MotechEvent("Subject", parameters));
    }

}
