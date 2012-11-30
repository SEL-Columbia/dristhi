package org.ei.drishti.service.scheduling;

import org.ei.drishti.contract.EligibleCoupleRegistrationRequest;
import org.ei.drishti.contract.Schedule;
import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.util.Arrays.asList;
import static org.ei.drishti.scheduler.DrishtiScheduleConstants.ECSchedulesConstants.EC_SCHEDULE_FP_COMPLICATION;
import static org.ei.drishti.scheduler.DrishtiScheduleConstants.ECSchedulesConstants.EC_SCHEDULE_FP_COMPLICATION_MILESTONE;
import static org.ei.drishti.scheduler.DrishtiScheduleConstants.PREFERED_TIME_FOR_SCHEDULES;

@Service
public class ECSchedulingService {
    private final ScheduleTrackingService scheduleTrackingService;
    private final Schedule fpComplicationSchedule = new Schedule(EC_SCHEDULE_FP_COMPLICATION,asList(EC_SCHEDULE_FP_COMPLICATION_MILESTONE));

    @Autowired
    public ECSchedulingService(ScheduleTrackingService scheduleTrackingService) {
        this.scheduleTrackingService = scheduleTrackingService;
    }

    public void enrollToFPComplications(EligibleCoupleRegistrationRequest request) {
        if(!(request.highPriority().equalsIgnoreCase("Yes"))){
            return;
        }

        if(!(request.currentMethod().equalsIgnoreCase("none") || request.currentMethod().isEmpty())){
            return;
        }

        scheduleTrackingService.enroll(new EnrollmentRequest(request.caseId(), fpComplicationSchedule.getName(), new Time(PREFERED_TIME_FOR_SCHEDULES), LocalDate.now(),null,null,null,null,null));
    }
}
