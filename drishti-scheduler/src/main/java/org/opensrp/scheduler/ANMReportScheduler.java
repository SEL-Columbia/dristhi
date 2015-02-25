package org.ei.drishti.scheduler;

import org.joda.time.DateTime;
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
public class ANMReportScheduler {
    public static final String SUBJECT = "DRISHTI-ANM-REPORT-FETCH-SCHEDULE";
    public static final int START_DELAY_IN_MINUTES = 10;
    public static final long REPEAT_INTERVAL_IN_HOUR = 6L;
    private MotechSchedulerService schedulerService;
    private static Logger logger = LoggerFactory.getLogger(ANMReportScheduler.class.toString());

    @Autowired
    public ANMReportScheduler(MotechSchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    public void startTimedScheduler() {
        logger.info("Scheduling anm report fetch ...");

        Date startTime = DateTime.now().plusMinutes(START_DELAY_IN_MINUTES).toDate();
        MotechEvent event = new MotechEvent(SUBJECT, new HashMap<String, Object>());
        RepeatingSchedulableJob job = new RepeatingSchedulableJob(event, startTime, null,
                REPEAT_INTERVAL_IN_HOUR * MILLIS_PER_HOUR);

        schedulerService.safeScheduleRepeatingJob(job);
    }
}
