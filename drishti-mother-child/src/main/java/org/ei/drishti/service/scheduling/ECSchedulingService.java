package org.ei.drishti.service.scheduling;

import org.ei.drishti.contract.EligibleCoupleRegistrationRequest;
import org.ei.drishti.contract.Schedule;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.repository.AllEligibleCouples;
import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static java.util.Arrays.asList;
import static org.ei.drishti.common.AllConstants.CommonCommCareFields.HIGH_PRIORITY_COMMCARE_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.FamilyPlanningCommCareFields.CURRENT_FP_METHOD_CHANGE_DATE_COMMCARE_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.FamilyPlanningCommCareFields.CURRENT_FP_METHOD_COMMCARE_FIELD_NAME;
import static org.ei.drishti.scheduler.DrishtiScheduleConstants.ECSchedulesConstants.EC_SCHEDULE_FP_COMPLICATION;
import static org.ei.drishti.scheduler.DrishtiScheduleConstants.ECSchedulesConstants.EC_SCHEDULE_FP_COMPLICATION_MILESTONE;
import static org.ei.drishti.scheduler.DrishtiScheduleConstants.PREFERED_TIME_FOR_SCHEDULES;

@Service
public class ECSchedulingService {
    private final AllEligibleCouples allEligibleCouples;
    private final ScheduleTrackingService scheduleTrackingService;

    private final Schedule fpComplicationSchedule = new Schedule(EC_SCHEDULE_FP_COMPLICATION, asList(EC_SCHEDULE_FP_COMPLICATION_MILESTONE));

    @Autowired
    public ECSchedulingService(AllEligibleCouples allEligibleCouples, ScheduleTrackingService scheduleTrackingService) {
        this.allEligibleCouples = allEligibleCouples;
        this.scheduleTrackingService = scheduleTrackingService;
    }

    public void enrollToFPComplications(EligibleCoupleRegistrationRequest request) {
        if (!(request.highPriority().equalsIgnoreCase("Yes"))) {
            return;
        }

        if (!(request.currentMethod().equalsIgnoreCase("none") || request.currentMethod().isEmpty())) {
            return;
        }

        scheduleTrackingService.enroll(new EnrollmentRequest(request.caseId(), fpComplicationSchedule.name(), new Time(PREFERED_TIME_FOR_SCHEDULES), LocalDate.now(), null, null, null, null, null));
    }

    public void updateFPComplications(String caseId, Map<String, String> details) {
        if ((details.get(CURRENT_FP_METHOD_COMMCARE_FIELD_NAME).equalsIgnoreCase("none") || details.get(CURRENT_FP_METHOD_COMMCARE_FIELD_NAME).isEmpty())) {
            if(isNotEnrolled(caseId, fpComplicationSchedule.name())) {
                EligibleCouple couple = allEligibleCouples.findByCaseId(caseId);
                if(couple.details().get(HIGH_PRIORITY_COMMCARE_FIELD_NAME).equalsIgnoreCase("Yes")){
                    scheduleTrackingService.enroll(new EnrollmentRequest(caseId, fpComplicationSchedule.name(),new Time(PREFERED_TIME_FOR_SCHEDULES), LocalDate.parse(details.get(CURRENT_FP_METHOD_CHANGE_DATE_COMMCARE_FIELD_NAME)), null, null, null, null, null));
                }
            }
        } else {
            scheduleTrackingService.fulfillCurrentMilestone(caseId, fpComplicationSchedule.name(), LocalDate.parse(details.get(CURRENT_FP_METHOD_CHANGE_DATE_COMMCARE_FIELD_NAME)));
        }
    }

    private boolean isNotEnrolled(String caseId, String scheduleName) {
        return scheduleTrackingService.getEnrollment(caseId, scheduleName) == null;
    }
}
