package org.ei.drishti.action;

import org.ei.drishti.scheduler.router.Action;
import org.ei.drishti.scheduler.router.MilestoneEvent;
import org.ei.drishti.service.ReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("CaptureANMReminderAction")
public class CaptureANMReminderAction implements Action {
    ReminderService reminderService;

    @Autowired
    public CaptureANMReminderAction(ReminderService reminderService) {
        this.reminderService = reminderService;
    }

    @Override
    public void invoke(MilestoneEvent event) {
        reminderService.reminderForMother(event.externalId(), event.milestoneName(), event.windowName());
    }
}
