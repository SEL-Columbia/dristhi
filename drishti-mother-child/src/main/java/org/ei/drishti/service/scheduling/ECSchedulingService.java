package org.ei.drishti.service.scheduling;

import org.ei.drishti.contract.FamilyPlanningUpdateRequest;
import org.ei.drishti.contract.Schedule;
import org.ei.drishti.domain.EligibleCouple;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.ei.drishti.common.AllConstants.CommonCommCareFields.*;
import static org.ei.drishti.common.AllConstants.FamilyPlanningCommCareFields.*;
import static org.ei.drishti.scheduler.DrishtiScheduleConstants.ECSchedulesConstants.EC_SCHEDULE_FP_COMPLICATION;
import static org.ei.drishti.scheduler.DrishtiScheduleConstants.ECSchedulesConstants.EC_SCHEDULE_FP_COMPLICATION_MILESTONE;
import static org.ei.drishti.scheduler.DrishtiScheduleConstants.PREFERED_TIME_FOR_SCHEDULES;
import static org.joda.time.LocalDate.parse;

@Service
public class ECSchedulingService {
    private final ScheduleTrackingService scheduleTrackingService;

    private final Schedule fpComplicationSchedule = new Schedule(EC_SCHEDULE_FP_COMPLICATION, asList(EC_SCHEDULE_FP_COMPLICATION_MILESTONE));

    @Autowired
    public ECSchedulingService(ScheduleTrackingService scheduleTrackingService) {
        this.scheduleTrackingService = scheduleTrackingService;
    }

    public void enrollToFPComplications(String entityId, String currentFPMethod, String isHighPriority, String submissionDate) {
        if (isCoupleHighPriority(isHighPriority) && isFPMethodNone(currentFPMethod)) {
            scheduleTrackingService.enroll(new EnrollmentRequest(entityId, fpComplicationSchedule.name(), new Time(PREFERED_TIME_FOR_SCHEDULES),
                    parse(submissionDate), null, null, null, null, null));
        }
    }

    public void updateFPComplications(FamilyPlanningUpdateRequest request, EligibleCouple couple) {
        if (!FP_METHOD_CHANGED_COMMCARE_FIELD_VALUE.equals(request.fpUpdate())) {
            return;
        }
        if (isFPMethodNone(request.currentMethod())
                && isCoupleHighPriority(couple.details().get(HIGH_PRIORITY_COMMCARE_FIELD_NAME))
                && !isEnrolledToSchedule(request.caseId(), fpComplicationSchedule.name())) {
            scheduleTrackingService.enroll(new EnrollmentRequest(request.caseId(), fpComplicationSchedule.name(), new Time(PREFERED_TIME_FOR_SCHEDULES),
                    parse(request.familyPlanningMethodChangeDate()), null, null, null, null, null));
        } else if (isEnrolledToSchedule(request.caseId(), fpComplicationSchedule.name())) {
            scheduleTrackingService.fulfillCurrentMilestone(request.caseId(), fpComplicationSchedule.name(), parse(request.familyPlanningMethodChangeDate()));
        }
    }

    private boolean isCoupleHighPriority(String isHighPriorityField) {
        return BOOLEAN_TRUE_COMMCARE_VALUE.equalsIgnoreCase(isHighPriorityField);
    }

    private boolean isFPMethodNone(String currentFPMethod) {
        return isBlank(currentFPMethod) || NO_FP_METHOD_COMMCARE_FIELD_VALUE.equalsIgnoreCase(currentFPMethod);
    }

    private boolean isEnrolledToSchedule(String caseId, String scheduleName) {
        return scheduleTrackingService.getEnrollment(caseId, scheduleName) != null;
    }
}