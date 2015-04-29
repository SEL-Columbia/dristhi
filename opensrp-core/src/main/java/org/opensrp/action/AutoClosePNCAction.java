package org.opensrp.action;

import java.util.Map;

import org.opensrp.scheduler.HookedEvent;
import org.opensrp.scheduler.MilestoneEvent;
import org.opensrp.service.PNCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("AutoClosePNCAction")
public class AutoClosePNCAction implements HookedEvent {
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
