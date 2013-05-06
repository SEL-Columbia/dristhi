package org.ei.drishti.service.scheduling;

import org.ei.drishti.common.util.DateUtil;
import org.ei.drishti.contract.FamilyPlanningUpdateRequest;
import org.ei.drishti.contract.Schedule;
import org.ei.drishti.domain.EligibleCouple;
import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.lang.Integer.parseInt;
import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.ei.drishti.common.AllConstants.CommonCommCareFields.BOOLEAN_TRUE_COMMCARE_VALUE;
import static org.ei.drishti.common.AllConstants.CommonCommCareFields.HIGH_PRIORITY_COMMCARE_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.FamilyPlanningCommCareFields.*;
import static org.ei.drishti.common.util.DateUtil.today;
import static org.ei.drishti.scheduler.DrishtiScheduleConstants.ECSchedulesConstants.*;
import static org.ei.drishti.scheduler.DrishtiScheduleConstants.PREFERED_TIME_FOR_SCHEDULES;
import static org.joda.time.DateTimeConstants.DAYS_PER_WEEK;
import static org.joda.time.LocalDate.parse;

@Service
public class ECSchedulingService {
    public static final int NUMBER_OF_PILLS_IN_ONE_OCP_STRIP = 28;
    private final ScheduleTrackingService scheduleTrackingService;

    private final Schedule fpComplicationSchedule = new Schedule(EC_SCHEDULE_FP_COMPLICATION, asList(EC_SCHEDULE_FP_COMPLICATION_MILESTONE));
    private final Schedule dmpaInjectableRefillSchedule = new Schedule(EC_SCHEDULE_DMPA_INJECTABLE_REFILL, asList(EC_SCHEDULE_DMPA_INJECTABLE_REFILL_MILESTONE));
    private final Schedule ocpRefillSchedule = new Schedule(EC_SCHEDULE_OCP_REFILL, asList(EC_SCHEDULE_OCP_REFILL_MILESTONE));

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

    public void enrollToRenewFPProducts(String entityId, String currentFPMethod, String dmpaInjectionDate, String numberOfOCPStripsSupplied, String ocpRefillDate) {
        if (DMPA_INJECTABLE_FP_METHOD_VALUE.equals(currentFPMethod)) {
            scheduleTrackingService.enroll(new EnrollmentRequest(entityId, dmpaInjectableRefillSchedule.name(), new Time(PREFERED_TIME_FOR_SCHEDULES),
                    parse(dmpaInjectionDate), null, null, null, null, null));
            return;
        }
        if (OCP_FP_METHOD_VALUE.equals(currentFPMethod)) {
            LocalDate scheduleStartDate = (parseInt(numberOfOCPStripsSupplied) == 0) ? today() : twoWeeksBeforeOCPPillsRunOut(numberOfOCPStripsSupplied, ocpRefillDate);
            scheduleTrackingService.enroll(new EnrollmentRequest(entityId, ocpRefillSchedule.name(), new Time(PREFERED_TIME_FOR_SCHEDULES),
                    scheduleStartDate, null, null, null, null, null));
            return;
        }
    }

    private LocalDate twoWeeksBeforeOCPPillsRunOut(String numberOfOCPStripsSupplied, String ocpRefillDate) {
        return parse(ocpRefillDate)
                .plusDays(parseInt(numberOfOCPStripsSupplied) * NUMBER_OF_PILLS_IN_ONE_OCP_STRIP)
                .minusDays(2 * DAYS_PER_WEEK);
    }
}