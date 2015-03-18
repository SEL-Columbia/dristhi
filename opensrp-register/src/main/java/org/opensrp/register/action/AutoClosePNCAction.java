package org.opensrp.register.action;

import org.opensrp.scheduler.router.Action;
import org.opensrp.scheduler.router.MilestoneEvent;
import org.opensrp.register.service.PNCService;
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
