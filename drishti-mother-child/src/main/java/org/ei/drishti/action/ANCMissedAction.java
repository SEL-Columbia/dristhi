package org.ei.drishti.action;

import org.ei.drishti.scheduler.router.Action;
import org.ei.drishti.scheduler.router.MilestoneEvent;
import org.ei.drishti.scheduler.service.ANCSchedulesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("ANCMissedAction")
public class ANCMissedAction implements Action {
    private final ANCSchedulesService schedulesService;

    @Autowired
    public ANCMissedAction(ANCSchedulesService schedulesService) {
        this.schedulesService = schedulesService;
    }

    @Override
    public void invoke(MilestoneEvent event) {
        schedulesService.ancVisitHasBeenMissed(event.externalId(), event.scheduleName());
    }
}
