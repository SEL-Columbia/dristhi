package org.ei.drishti.scheduler;

import org.joda.time.DateTime;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduler.domain.RepeatingSchedulableJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;

import static org.joda.time.DateTimeConstants.MILLIS_PER_HOUR;

@Component
public class MCTSReportScheduler {
    public static final String SUBJECT = "MCTS-REPORT-SCHEDULE";
    private static final int START_DELAY = 10;
    private final MotechSchedulerService schedulerService;
    private static Logger logger = LoggerFactory.getLogger(MCTSReportScheduler.class.toString());
    private long pollInterval;

    @Autowired
    public MCTSReportScheduler(MotechSchedulerService schedulerService, @Value("#{drishti['mcts.poll.time.interval.in.hours']}") Long pollInterval) {
        this.schedulerService = schedulerService;
        this.pollInterval = pollInterval;
    }

    public void startTimedScheduler() {
        logger.info("Scheduling SMS to fetch ...");

        Date startTime = DateTime.now().plusMinutes(START_DELAY).toDate();
        MotechEvent event = new MotechEvent(SUBJECT, new HashMap<String, Object>());
        RepeatingSchedulableJob job = new RepeatingSchedulableJob(event, startTime, null, pollInterval * MILLIS_PER_HOUR);

        schedulerService.safeScheduleRepeatingJob(job);
    }
}
