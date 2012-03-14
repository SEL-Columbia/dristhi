package org.ei.drishti.controller;

import org.ei.drishti.scheduler.router.Action;
import org.ei.drishti.scheduler.router.AlertRouter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import static org.ei.drishti.scheduler.router.Matcher.any;

@Component
public class AlertController {
    @Autowired
    public AlertController(AlertRouter router, @Qualifier("anmGroupSMSAction") Action anmGroupSMS, @Qualifier("failAction") Action fail) {
        router.addRoute(any(), any(), any(), anmGroupSMS);
    }
}
