package org.ei.drishti.service.scheduling;

import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.domain.Milestone;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.repository.AllSchedules;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.ei.drishti.common.util.DateUtil.today;
import static org.ei.drishti.scheduler.DrishtiScheduleConstants.PREFERED_TIME_FOR_SCHEDULES;
import static org.joda.time.LocalDate.parse;

@Service
public class ScheduleService {
    private final ScheduleTrackingService scheduleTrackingService;
    private final AllSchedules allSchedules;

    @Autowired
    public ScheduleService(ScheduleTrackingService scheduleTrackingService, AllSchedules allSchedules) {
        this.scheduleTrackingService = scheduleTrackingService;
        this.allSchedules = allSchedules;
    }

    public void enroll(String entityId, String scheduleName, String referenceDate) {
        String startingMilestoneName = getStartingMilestoneName(scheduleName, parse(referenceDate));
        EnrollmentRequest request = new EnrollmentRequest(entityId, scheduleName, new Time(PREFERED_TIME_FOR_SCHEDULES),
                parse(referenceDate), null, null, null, startingMilestoneName, null);
        scheduleTrackingService.enroll(request);
    }

    private String getStartingMilestoneName(String name, LocalDate referenceDate) {
        Schedule schedule = allSchedules.getByName(name);
        for (Milestone milestone : schedule.getMilestones()) {
            if (referenceDate.plus(milestone.getMaximumDuration()).isAfter(today()))
                return milestone.getName();
        }
        return null;
    }

    public void enroll(String entityId, String scheduleName, String milestone, String referenceDate) {
        EnrollmentRequest request = new EnrollmentRequest(entityId, scheduleName,
                new Time(PREFERED_TIME_FOR_SCHEDULES), parse(referenceDate), null, null, null, milestone, null);
        scheduleTrackingService.enroll(request);
    }
}
