package org.opensrp.action;

import org.opensrp.scheduler.router.Action;
import org.opensrp.scheduler.router.MilestoneEvent;
import org.opensrp.service.scheduling.ANCSchedulesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Qualifier("ForceFulfillAction")
public class ForceFulfillAction implements Action {
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
