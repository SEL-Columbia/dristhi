package org.ei.drishti.controller;

import org.ei.drishti.scheduler.router.Action;
import org.ei.drishti.scheduler.router.AlertRouter;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import static org.ei.drishti.scheduler.DrishtiSchedules.SCHEDULE_ANC;
import static org.ei.drishti.scheduler.router.Matcher.any;
import static org.ei.drishti.scheduler.router.Matcher.eq;

@Component
public class AlertController {
    @Autowired
    public AlertController(AlertRouter router, @Qualifier("ANMGroupSMSAction") Action anmGroupSMS, @Qualifier("ANCMissedAction") Action ancMissed) {
        router.addRoute(eq(SCHEDULE_ANC), any(), eq(WindowName.max.toString()), ancMissed);
        router.addRoute(any(), any(), any(), anmGroupSMS);
    }
}
