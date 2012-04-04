package org.ei.drishti.action;

import org.ei.drishti.scheduler.router.Action;
import org.ei.drishti.scheduler.router.MilestoneEvent;
import org.ei.drishti.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("CaptureANMReminderAction")
public class CaptureANMReminderAction implements Action {
    AlertService alertService;

    @Autowired
    public CaptureANMReminderAction(AlertService alertService) {
        this.alertService = alertService;
    }

    @Override
    public void invoke(MilestoneEvent event) {
        alertService.alertForMother(event.externalId(), event.milestoneName(), event.windowName());
    }
}
