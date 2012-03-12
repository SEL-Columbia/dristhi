package org.ei.drishti.scheduler;

import org.ei.drishti.domain.Mother;
import org.ei.drishti.repository.AllMothers;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduletracking.api.events.constants.EventDataKeys;
import org.motechproject.scheduletracking.api.events.constants.EventSubjects;
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

    @MotechListener(subjects = {EventSubjects.MILESTONE_ALERT})
    public void handleAlert(MotechEvent event) {
        String caseId = event.getParameters().get(EventDataKeys.EXTERNAL_ID).toString();
        Mother mother = mothers.findByCaseId(caseId);

        System.out.println("======= Event for " + mother + ": " + event);
//        smsService.sendSMS("9590377135", event.getParameters().toString());
    }
}
