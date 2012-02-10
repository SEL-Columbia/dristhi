package org.ei.drishti.scheduler;

import org.ei.commcare.listener.event.CommCareListener;
import org.joda.time.DateTime;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.RepeatingSchedulableJob;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.event.annotations.MotechListener;
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

    @Autowired
    public CommCareScheduler(MotechSchedulerService service, CommCareListener careListener) {
        this.service = service;
        this.careListener = careListener;
    }

    public void startTimedScheduler() {
        System.out.println("Scheduling ...");

        Date startTime = DateTime.now().plusSeconds(10).toDate();
        MotechEvent event = new MotechEvent(SUBJECT, new HashMap<String, Object>());
        RepeatingSchedulableJob job = new RepeatingSchedulableJob(event, startTime, null, 10 * MILLIS_PER_SECOND);

        service.safeScheduleRepeatingJob(job);
    }

    @MotechListener(subjects = SUBJECT)
    public void fetchFromCommCareHQ(MotechEvent event) throws Exception {
        System.out.println("Hello! Fetching from CommCare HQ!");
        careListener.fetchFromServer();
    }
}
