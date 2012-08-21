package org.ei.drishti.action;

import org.ei.drishti.scheduler.router.Action;
import org.ei.drishti.scheduler.router.MilestoneEvent;
import org.ei.drishti.service.ANCSchedulesService;
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
