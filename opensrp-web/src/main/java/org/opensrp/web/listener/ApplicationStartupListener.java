package org.opensrp.web.listener;

import java.util.concurrent.TimeUnit;

import org.opensrp.scheduler.DrishtiScheduleConstants;
import org.opensrp.scheduler.RepeatingSchedule;
import org.opensrp.scheduler.TaskSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartupListener implements ApplicationListener<ContextRefreshedEvent> {
    public static final String APPLICATION_ID = "org.springframework.web.context.WebApplicationContext:/opensrp";

    private TaskSchedulerService scheduler;
    
    private RepeatingSchedule formSchedule;
    private RepeatingSchedule anmReportScheduler;
    private RepeatingSchedule mctsReportScheduler;
    
    @Autowired
    public ApplicationStartupListener(TaskSchedulerService scheduler, 
    		@Value("#{opensrp['form.poll.time.interval']}") int formPollInterval,
    		@Value("#{opensrp['mcts.poll.time.interval.in.minutes']}") int mctsPollIntervalInHours) {
        this.scheduler = scheduler;
        formSchedule = new RepeatingSchedule(DrishtiScheduleConstants.FORM_SCHEDULE_SUBJECT, 2, TimeUnit.MINUTES, formPollInterval, TimeUnit.MINUTES);
        anmReportScheduler = new RepeatingSchedule(DrishtiScheduleConstants.ANM_REPORT_SCHEDULE_SUBJECT, 10, TimeUnit.MINUTES, 6, TimeUnit.HOURS);
        mctsReportScheduler = new RepeatingSchedule(DrishtiScheduleConstants.MCTS_REPORT_SCHEDULE_SUBJECT, 10, TimeUnit.MINUTES, mctsPollIntervalInHours, TimeUnit.HOURS);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if (APPLICATION_ID.equals(contextRefreshedEvent.getApplicationContext().getId())) {
            scheduler.startJob(formSchedule);
            scheduler.startJob(anmReportScheduler);
            scheduler.startJob(mctsReportScheduler);
        }
    }
}
