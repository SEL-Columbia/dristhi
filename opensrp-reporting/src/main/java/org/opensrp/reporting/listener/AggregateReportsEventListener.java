package org.opensrp.reporting.listener;

import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.opensrp.reporting.service.AggregateReportsService;
import org.opensrp.scheduler.AggregateReportsScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AggregateReportsEventListener {
    private AggregateReportsService aggregateReportsService;

    @Autowired
    public AggregateReportsEventListener(AggregateReportsService aggregateReportsService) {
        this.aggregateReportsService = aggregateReportsService;
    }

    @MotechListener(subjects = AggregateReportsScheduler.SUBJECT)
    public void aggregate(MotechEvent event) {
        aggregateReportsService.sendReportsToAggregator();
    }
}
