package org.opensrp.scheduler;

import org.opensrp.common.util.DateTimeUtil;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduler.domain.RepeatingSchedulableJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

import static org.joda.time.DateTimeConstants.MILLIS_PER_MINUTE;

@Component
public class AggregateReportsScheduler {
    private static Logger logger = LoggerFactory.getLogger(AggregateReportsScheduler.class.toString());

    public static final String SUBJECT = "REPORT_AGGREGATOR_SCHEDULE";
    public static final int START_DELAY_IN_MINUTES = 10;
    public static long REPEAT_INTERVAL_IN_MINUTES = 10L;
    private MotechSchedulerService schedulerService;

    @Autowired
    public AggregateReportsScheduler(@Value("#{opensrp['batch.update.time.interval']}") long batchUpdateTimeInterval, MotechSchedulerService schedulerService) {
        this.schedulerService = schedulerService;
        REPEAT_INTERVAL_IN_MINUTES = batchUpdateTimeInterval;
    }

    public void startTimedScheduler() {
        logger.info("Scheduling report aggregator job...");

        Date startTime = DateTimeUtil.now().plusMinutes(START_DELAY_IN_MINUTES).toDate();
        MotechEvent event = new MotechEvent(SUBJECT);
        RepeatingSchedulableJob job = new RepeatingSchedulableJob(event, startTime, null,
                REPEAT_INTERVAL_IN_MINUTES * MILLIS_PER_MINUTE);
        schedulerService.safeScheduleRepeatingJob(job);
    }
}

