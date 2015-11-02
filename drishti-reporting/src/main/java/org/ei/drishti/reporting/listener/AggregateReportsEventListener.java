package org.ei.drishti.reporting.listener;

import org.ei.drishti.reporting.service.AggregateReportsService;
import org.ei.drishti.scheduler.AggregateReportsScheduler;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class AggregateReportsEventListener {
    private AggregateReportsService aggregateReportsService;
    private static Logger logger = LoggerFactory
			.getLogger(AggregateReportsEventListener.class.toString());

    @Autowired
    public AggregateReportsEventListener(AggregateReportsService aggregateReportsService) {
        this.aggregateReportsService = aggregateReportsService;
    }

    @MotechListener(subjects = AggregateReportsScheduler.SUBJECT)
    public void aggregate(MotechEvent event) {
        logger.info("send reports to Aggregator");
        aggregateReportsService.sendReportsToAggregator();
    }
}
