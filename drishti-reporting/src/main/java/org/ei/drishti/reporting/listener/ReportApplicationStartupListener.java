package org.ei.drishti.reporting.listener;

import org.ei.drishti.scheduler.AggregateReportsScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

@Component
public class ReportApplicationStartupListener implements ApplicationListener<ContextRefreshedEvent> {
    private static Logger logger = LoggerFactory.getLogger(ReportApplicationStartupListener.class.toString());
    public static final String APPLICATION_ID = "org.springframework.web.context.WebApplicationContext:/drishti-report";
    private AggregateReportsScheduler aggregateReportsScheduler;

    @Autowired
    public ReportApplicationStartupListener(AggregateReportsScheduler aggregateReportsScheduler) {
        this.aggregateReportsScheduler = aggregateReportsScheduler;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        logger.info(MessageFormat.format("Dristhi report service is starting. Application id: {0}",
                contextRefreshedEvent.getApplicationContext().getId()));
        if (APPLICATION_ID.equals(contextRefreshedEvent.getApplicationContext().getId())) {
            aggregateReportsScheduler.startTimedScheduler();
        }
    }
}
