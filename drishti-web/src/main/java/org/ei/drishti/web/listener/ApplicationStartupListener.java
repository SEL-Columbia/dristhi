package org.ei.drishti.web.listener;

import org.ei.drishti.scheduler.ANMReportScheduler;
import org.ei.drishti.scheduler.DrishtiFormScheduler;
import org.ei.drishti.scheduler.ReportAggregateScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartupListener implements ApplicationListener<ContextRefreshedEvent> {
    public static final String APPLICATION_ID = "org.springframework.web.context.WebApplicationContext:/drishti";
    private ANMReportScheduler anmReportScheduler;
    private DrishtiFormScheduler drishtiFormScheduler;
    private ReportAggregateScheduler reportAggregateScheduler;

    @Autowired
    public ApplicationStartupListener(ANMReportScheduler anmReportScheduler, DrishtiFormScheduler drishtiFormScheduler,
                                      ReportAggregateScheduler reportAggregateScheduler) {
        this.anmReportScheduler = anmReportScheduler;
        this.drishtiFormScheduler = drishtiFormScheduler;
        this.reportAggregateScheduler = reportAggregateScheduler;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if (APPLICATION_ID.equals(contextRefreshedEvent.getApplicationContext().getId())) {
            anmReportScheduler.startTimedScheduler();
            drishtiFormScheduler.startTimedScheduler();
            reportAggregateScheduler.startTimedScheduler();
        }
    }
}
