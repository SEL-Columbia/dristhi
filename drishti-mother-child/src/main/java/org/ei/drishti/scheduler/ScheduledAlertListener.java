package org.ei.drishti.scheduler;

import org.ei.drishti.domain.Mother;
import org.ei.drishti.repository.AllMothers;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduletracking.api.events.constants.EventDataKey;
import org.motechproject.scheduletracking.api.events.constants.EventSubject;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.sms.api.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScheduledAlertListener {
    private final AllMothers mothers;
    private final SmsService smsService;

    @Autowired
    public ScheduledAlertListener(AllMothers mothers, SmsService smsService) {
        this.mothers = mothers;
        this.smsService = smsService;
    }

    @MotechListener(subjects = {EventSubject.MILESTONE_ALERT})
    public void handleAlert(MotechEvent event) {
        String thaayiCardNumber = event.getParameters().get(EventDataKey.EXTERNAL_ID).toString();
        Mother mother = mothers.findByThaayiCardNumber(thaayiCardNumber);

        System.out.println("SMS: Go visit " + mother);
//        smsService.sendSMS("9590377135", event.getParameters().toString());
    }
}
