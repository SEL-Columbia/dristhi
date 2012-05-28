package org.ei.drishti.action;

import org.ei.drishti.scheduler.router.Action;
import org.ei.drishti.scheduler.router.MilestoneEvent;
import org.ei.drishti.service.ActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("CapturePNCReminderAction")
public class CapturePNCReminderAction implements Action {
    private ActionService actionService;

    @Autowired
    public CapturePNCReminderAction(ActionService actionService) {
        this.actionService = actionService;
    }

    @Override
    public void invoke(MilestoneEvent event) {
        actionService.alertForChild(event.externalId(), event.milestoneName(), event.windowName(), event.due());
    }
}
