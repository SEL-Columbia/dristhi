package org.opensrp.register.action;

import java.util.Map;

import org.opensrp.register.service.scheduling.ANCSchedulesService;
import org.opensrp.scheduler.HookedEvent;
import org.opensrp.scheduler.MilestoneEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("ForceFulfillAction")
public class ForceFulfillAction implements HookedEvent {
    private final ANCSchedulesService schedulesService;

    @Autowired
    public ForceFulfillAction(ANCSchedulesService schedulesService) {
        this.schedulesService = schedulesService;
    }

    @Override
    public void invoke(MilestoneEvent event, Map<String, String> extraData) {
        schedulesService.forceFulfillMilestone(event.externalId(), event.scheduleName());
    }
}
