package org.ei.drishti.controller;

import org.ei.drishti.scheduler.router.Action;
import org.ei.drishti.scheduler.router.AlertRouter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import static org.ei.drishti.scheduler.router.Matcher.any;
import static org.ei.drishti.scheduler.router.Matcher.eq;

@Component
public class AlertController {
    @Autowired
    public AlertController(AlertRouter router, @Qualifier("groupSMSAction") Action groupSMS, @Qualifier("failAction") Action fail) {
        router.addRoute(eq("Ante Natal Care - Normal"), eq("ANC 1"), any(), groupSMS);
        router.addRoute(any(), any(), any(), fail);
    }
}
