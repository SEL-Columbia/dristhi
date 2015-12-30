package org.opensrp.register.action;

import java.util.Map;

import static org.opensrp.register.RegisterConstants.MotherScheduleConstants.*;
import static org.opensrp.scheduler.Matcher.*;
import static org.motechproject.scheduletracking.api.domain.WindowName.*;
import org.opensrp.register.ziggy.scheduling.ANCSchedulesService;
import org.opensrp.scheduler.AlertRouter;
import org.opensrp.scheduler.HookedEvent;
import org.opensrp.scheduler.MilestoneEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("ForceFulfillAction")
public class ForceFulfillAction implements HookedEvent {
    private final ANCSchedulesService schedulesService;
    private AlertRouter alertRouter;

    @Autowired
    public ForceFulfillAction(ANCSchedulesService schedulesService, AlertRouter alertRouter) {
        this.schedulesService = schedulesService;
        this.alertRouter = alertRouter;
        alertRouter.addRoute(eq(SCHEDULE_ANC), any(), eq(max.toString()), this);
        alertRouter.addRoute(eq(SCHEDULE_LAB), any(), eq(max.toString()), this);
    }

    @Override
    public void invoke(MilestoneEvent event, Map<String, String> extraData) {
        schedulesService.forceFulfillMilestone(event.externalId(), event.scheduleName());
    }
}
