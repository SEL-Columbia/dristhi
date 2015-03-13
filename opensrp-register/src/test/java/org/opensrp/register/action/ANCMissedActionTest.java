package org.opensrp.register.action;

import org.opensrp.scheduler.router.MilestoneEvent;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.scheduler.domain.MotechEvent;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.scheduletracking.api.events.constants.EventDataKeys.EXTERNAL_ID;
import static org.motechproject.scheduletracking.api.events.constants.EventDataKeys.SCHEDULE_NAME;
import org.opensrp.register.action.ForceFulfillAction;
import org.opensrp.register.service.scheduling.ANCSchedulesService;

public class ANCMissedActionTest {
    @Mock
    ANCSchedulesService schedulesService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldMentionThatANCVisitHasBeenMissedForScheduleAndExternalIdSpecifiedByEvent() {
        ForceFulfillAction action = new ForceFulfillAction(schedulesService);

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
