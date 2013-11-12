package org.ei.drishti.scheduler;

import org.ei.drishti.common.util.DateTimeUtil;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduler.domain.RepeatingSchedulableJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;

import static org.joda.time.DateTimeConstants.MILLIS_PER_HOUR;

@Component
public class AggregateReportsScheduler {
    private static Logger logger = LoggerFactory.getLogger(ANMReportScheduler.class.toString());

    public static final String SUBJECT = "REPORT_AGGREGATOR_SCHEDULE";
    public static final int START_DELAY_IN_MINUTES = 10;
    public static final long REPEAT_INTERVAL_IN_HOUR = 1L;
    private MotechSchedulerService schedulerService;

    @Autowired
    public AggregateReportsScheduler(MotechSchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    public void startTimedScheduler() {
        logger.info("Scheduling report aggregator job...");

        Date startTime = DateTimeUtil.now().plusMinutes(START_DELAY_IN_MINUTES).toDate();
        MotechEvent event = new MotechEvent(SUBJECT, new HashMap<String, Object>());
        RepeatingSchedulableJob job = new RepeatingSchedulableJob(event, startTime, null,
                REPEAT_INTERVAL_IN_HOUR * MILLIS_PER_HOUR);
        schedulerService.safeScheduleRepeatingJob(job);
    }
}

