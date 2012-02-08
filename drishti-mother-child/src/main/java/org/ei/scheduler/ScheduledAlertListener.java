package org.ei.scheduler;

import org.motechproject.model.MotechEvent;
import org.motechproject.scheduletracking.api.events.constants.EventSubject;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.sms.api.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScheduledAlertListener {
    private final SmsService smsService;

    @Autowired
    public ScheduledAlertListener(SmsService smsService) {
        this.smsService = smsService;
    }

    @MotechListener(subjects = {EventSubject.MILESTONE_ALERT})
    public void handleX(MotechEvent event) {
        System.out.println("Sending to 9590377135: " + event.getParameters().toString());
        smsService.sendSMS("9590377135", event.getParameters().toString());
    }
}
