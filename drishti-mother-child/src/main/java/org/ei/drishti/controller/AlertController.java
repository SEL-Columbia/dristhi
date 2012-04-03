package org.ei.drishti.controller;

import org.ei.drishti.scheduler.router.Action;
import org.ei.drishti.scheduler.router.AlertRouter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import static org.ei.drishti.scheduler.DrishtiSchedules.*;
import static org.ei.drishti.scheduler.router.Matcher.any;
import static org.ei.drishti.scheduler.router.Matcher.anyOf;
import static org.ei.drishti.scheduler.router.Matcher.eq;
import static org.motechproject.scheduletracking.api.domain.WindowName.due;
import static org.motechproject.scheduletracking.api.domain.WindowName.late;
import static org.motechproject.scheduletracking.api.domain.WindowName.max;

@Component
public class AlertController {
    @Autowired
    public AlertController(AlertRouter router, @Qualifier("ANMGroupSMSAction") Action anmGroupSMS, @Qualifier("ForceFulfillAction") Action forceFulfill, @Qualifier("CaptureANMReminderAction") Action captureANMReminder) {
        router.addRoute(eq(SCHEDULE_ANC), any(), eq(max.toString()), forceFulfill);
        router.addRoute(eq(SCHEDULE_LAB), any(), eq(max.toString()), forceFulfill);
        router.addRoute(anyOf(SCHEDULE_ANC, SCHEDULE_TT, SCHEDULE_IFA), any(), anyOf(due.toString(), late.toString()), captureANMReminder);
        router.addRoute(any(), any(), any(), anmGroupSMS);
    }
}
