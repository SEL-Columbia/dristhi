package org.opensrp.scheduler.service;

import static java.util.Arrays.asList;
import static org.joda.time.LocalDate.parse;
import static org.joda.time.LocalTime.now;
import static org.motechproject.scheduletracking.api.domain.EnrollmentStatus.ACTIVE;
import static org.opensrp.common.util.DateUtil.today;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.domain.Milestone;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.repository.AllSchedules;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.EnrollmentsQuery;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ScheduleService {
    private final ScheduleTrackingService scheduleTrackingService;
    private final AllSchedules allSchedules;
    private int preferredTime;

    @Autowired
    public ScheduleService(ScheduleTrackingService scheduleTrackingService, AllSchedules allSchedules, @Value("#{opensrp['preferred.time']}") int preferredTime) {
        this.scheduleTrackingService = scheduleTrackingService;
        this.allSchedules = allSchedules;
        this.preferredTime = preferredTime;
    }

    public void enroll(String entityId, String scheduleName, String referenceDate) {
        String startingMilestoneName = getStartingMilestoneName(scheduleName, parse(referenceDate));
        EnrollmentRequest request = new EnrollmentRequest(entityId, scheduleName, new Time(new LocalTime(preferredTime, 0)),
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
                new Time(new LocalTime(preferredTime, 0)), parse(referenceDate), null, null, null, milestone, null);
        scheduleTrackingService.enroll(request);
    }
    
    public void fulfillMilestone(String entityId, String scheduleName, LocalDate completionDate) {
    	scheduleTrackingService.fulfillCurrentMilestone(entityId, scheduleName, completionDate, new Time(now()));
    }
    
    public void unenroll(String entityId, String scheduleName) {
    	scheduleTrackingService.unenroll(entityId, asList(scheduleName));
	}
    
    public void unenroll(String entityId, List<String> schedules) {
    	scheduleTrackingService.unenroll(entityId, schedules);
	}
    
    public List<EnrollmentRecord> findOpenEnrollments(String entityId) {
        return scheduleTrackingService.search(new EnrollmentsQuery().havingExternalId(entityId).havingState(ACTIVE));
	}
    
    public List<String> findOpenEnrollmentNames(String entityId) {
    	List<EnrollmentRecord> openEnrollments = findOpenEnrollments(entityId);
    	List<String> openSchedules = new ArrayList<>();
		for (EnrollmentRecord enrollment : openEnrollments ) {
			openSchedules.add(enrollment.getScheduleName());
        }
		
		return openSchedules;
	}
    
    public EnrollmentRecord getEnrollment(String entityId, String scheduleName) {
        return scheduleTrackingService.getEnrollment(entityId, scheduleName);
	}
}
