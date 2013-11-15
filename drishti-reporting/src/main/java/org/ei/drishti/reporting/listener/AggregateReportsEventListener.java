package org.ei.drishti.reporting.listener;

import org.ei.drishti.reporting.service.AggregateReportsService;
import org.ei.drishti.scheduler.AggregateReportsScheduler;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
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
