package org.ei.drishti.web.listener;

import org.ei.drishti.scheduler.ANMReportScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartupListener implements ApplicationListener<ContextRefreshedEvent> {
    public static final String APPLICATION_ID = "org.springframework.web.context.WebApplicationContext:/drishti";
    private ANMReportScheduler anmReportScheduler;

    @Autowired
    public ApplicationStartupListener(ANMReportScheduler anmReportScheduler) {
        this.anmReportScheduler = anmReportScheduler;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if (APPLICATION_ID.equals(contextRefreshedEvent.getApplicationContext().getId())) {
            anmReportScheduler.startTimedScheduler();
        }
    }
}
