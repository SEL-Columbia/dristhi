package org.ei.drishti.listener;

import org.ei.drishti.scheduler.AggregateReportsScheduler;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AggregateReportsEventListener {
    private static Logger logger = LoggerFactory.getLogger(AggregateReportsEventListener.class);

    @Autowired
    public AggregateReportsEventListener() {
    }

    @MotechListener(subjects = AggregateReportsScheduler.SUBJECT)
    public void aggregate(MotechEvent event) {
        logger.info("Aggregate Reports Event fired");
    }
}
