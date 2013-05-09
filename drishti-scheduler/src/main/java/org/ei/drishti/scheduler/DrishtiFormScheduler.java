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

import static org.joda.time.DateTimeConstants.MILLIS_PER_SECOND;

@Component
public class DrishtiFormScheduler {
    public static final String SUBJECT = "DRISHTI-FORM-SCHEDULE";
    private static final int START_DELAY = 1;
    private final MotechSchedulerService schedulerService;
    private static Logger logger = LoggerFactory.getLogger(DrishtiFormScheduler.class.toString());
    private long formPollInterval;

    @Autowired
    public DrishtiFormScheduler(MotechSchedulerService schedulerService, @Value("#{drishti['form.poll.time.interval']}") Long formPollInterval) {
        this.schedulerService = schedulerService;
        this.formPollInterval = formPollInterval;
    }

    public void startTimedScheduler() {
        logger.info("Scheduling Form fetch ...");

        Date startTime = DateTime.now().plusMinutes(START_DELAY).toDate();
        MotechEvent event = new MotechEvent(SUBJECT, new HashMap<String, Object>());
        RepeatingSchedulableJob job = new RepeatingSchedulableJob(event, startTime, null, formPollInterval * MILLIS_PER_SECOND);

        schedulerService.safeScheduleRepeatingJob(job);
    }
}
