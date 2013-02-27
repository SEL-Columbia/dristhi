package org.ei.drishti.action;

import org.ei.drishti.scheduler.router.Action;
import org.ei.drishti.scheduler.router.MilestoneEvent;
import org.ei.drishti.service.PNCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Qualifier("AutoClosePNCAction")
public class AutoClosePNCAction implements Action {
    PNCService pncService;

    @Autowired
    public AutoClosePNCAction(PNCService pncService) {
        this.pncService = pncService;
    }

    @Override
    public void invoke(MilestoneEvent event, Map<String, String> extraData) {
        pncService.autoClosePNCCase(event.externalId());
    }
}
