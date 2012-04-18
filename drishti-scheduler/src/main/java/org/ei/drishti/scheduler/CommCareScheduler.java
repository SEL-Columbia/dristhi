package org.ei.drishti.scheduler;

import org.ei.commcare.listener.CommCareListener;
import org.joda.time.DateTime;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.RepeatingSchedulableJob;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.event.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;

import static org.joda.time.DateTimeConstants.MILLIS_PER_SECOND;

@Component
public class CommCareScheduler {
    private static final String SUBJECT = "DRISHTI-TIMED-SCHEDULE";
    private final MotechSchedulerService service;
    private final CommCareListener careListener;
    private static Logger logger = LoggerFactory.getLogger(CommCareScheduler.class.toString());
    private boolean shouldFetchFromCommCare = false;

    @Autowired
    public CommCareScheduler(MotechSchedulerService service, CommCareListener careListener) {
        this.service = service;
        this.careListener = careListener;
    }

    public void startTimedScheduler() {
        logger.info("Scheduling timer ...");

        Date startTime = DateTime.now().plusSeconds(60).toDate();
        MotechEvent event = new MotechEvent(SUBJECT, new HashMap<String, Object>());
        RepeatingSchedulableJob job = new RepeatingSchedulableJob(event, startTime, null, 60 * MILLIS_PER_SECOND);

        service.safeScheduleRepeatingJob(job);

        startListening();
    }

    private void startListening() {
        this.shouldFetchFromCommCare = true;
    }

    @MotechListener(subjects = SUBJECT)
    public void fetchFromCommCareHQ(MotechEvent event) throws Exception {
        if (!shouldFetchFromCommCare) return;

        careListener.fetchFromServer();
    }
}
