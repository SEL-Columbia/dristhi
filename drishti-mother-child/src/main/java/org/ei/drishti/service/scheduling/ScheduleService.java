package org.ei.drishti.service.scheduling;

import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.ei.drishti.scheduler.DrishtiScheduleConstants.PREFERED_TIME_FOR_SCHEDULES;
import static org.joda.time.LocalDate.parse;

@Service
public class ScheduleService {
    private final ScheduleTrackingService scheduleTrackingService;

    @Autowired
    public ScheduleService(ScheduleTrackingService scheduleTrackingService) {
        this.scheduleTrackingService = scheduleTrackingService;
    }

    public void enroll(String entityId, String scheduleName, String referenceDate) {
        EnrollmentRequest request = new EnrollmentRequest(entityId, scheduleName, new Time(PREFERED_TIME_FOR_SCHEDULES),
                parse(referenceDate), null, null, null, null, null);
        scheduleTrackingService.enroll(request);
    }

    public void enroll(String entityId, String scheduleName, String milestone, String referenceDate) {
        EnrollmentRequest request = new EnrollmentRequest(entityId, scheduleName,
                new Time(PREFERED_TIME_FOR_SCHEDULES), parse(referenceDate), null, null, null, milestone, null);
        scheduleTrackingService.enroll(request);
    }
}
