package org.ei.drishti.reporting.listener;

import org.ei.drishti.scheduler.AggregateReportsScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

@Component
public class ReportApplicationStartupListener implements ApplicationListener<ContextRefreshedEvent> {
    private static Logger logger = LoggerFactory.getLogger(ReportApplicationStartupListener.class.toString());
    private static String APPLICATION_ID;
    private AggregateReportsScheduler aggregateReportsScheduler;

    @Autowired
    public ReportApplicationStartupListener(@Value("#{drishti['application.context']}") String applicationContext, AggregateReportsScheduler aggregateReportsScheduler) {
        APPLICATION_ID = applicationContext;
        this.aggregateReportsScheduler = aggregateReportsScheduler;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        logger.info(MessageFormat.format("Dristhi report service is starting. Application id: {0}",
                contextRefreshedEvent.getApplicationContext().getId()));
        if (APPLICATION_ID.equals(contextRefreshedEvent.getApplicationContext().getId())) {
            aggregateReportsScheduler.startTimedScheduler();
            logger.info("Aggregation of Reports scheduled...");
        }
    }
}
