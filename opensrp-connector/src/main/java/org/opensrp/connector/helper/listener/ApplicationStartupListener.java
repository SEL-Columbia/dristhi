package org.opensrp.connector.helper.listener;

import org.opensrp.connector.openmrs.OpenmrsPusherScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * Class must perform all the initialization tasks including starting schedulers
 *
 */
@Component
public class ApplicationStartupListener implements ApplicationListener<ContextRefreshedEvent> {
    public static final String APPLICATION_ID = "org.springframework.web.context.WebApplicationContext:/drishti";
    private OpenmrsPusherScheduler openmrsPusherScheduler;

    @Autowired
    public ApplicationStartupListener(OpenmrsPusherScheduler openmrsPusherScheduler) {
        this.openmrsPusherScheduler = openmrsPusherScheduler;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        /*if (APPLICATION_ID.equals(contextRefreshedEvent.getApplicationContext().getId())) {
            anmReportScheduler.startTimedScheduler();
            drishtiFormScheduler.startTimedScheduler();
            mctsReportScheduler.startTimedScheduler();
        }*/
    	openmrsPusherScheduler.startTimedScheduler();
    }
}
