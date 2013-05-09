package org.ei.drishti.service.scheduling;

import org.ei.drishti.contract.FamilyPlanningUpdateRequest;
import org.ei.drishti.contract.Schedule;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.domain.FPProductInformation;
import org.ei.drishti.service.ActionService;
import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.lang.Integer.parseInt;
import static java.text.MessageFormat.format;
import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.ei.drishti.common.AllConstants.CommonCommCareFields.BOOLEAN_TRUE_COMMCARE_VALUE;
import static org.ei.drishti.common.AllConstants.CommonCommCareFields.HIGH_PRIORITY_COMMCARE_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.FamilyPlanningCommCareFields.*;
import static org.ei.drishti.common.util.DateUtil.today;
import static org.ei.drishti.common.util.DateUtil.tryParse;
import static org.ei.drishti.common.util.IntegerUtil.tryParse;
import static org.ei.drishti.scheduler.DrishtiScheduleConstants.ECSchedulesConstants.*;
import static org.ei.drishti.scheduler.DrishtiScheduleConstants.PREFERED_TIME_FOR_SCHEDULES;
import static org.joda.time.DateTimeConstants.DAYS_PER_WEEK;
import static org.joda.time.LocalDate.parse;

@Service
public class ECSchedulingService {
    private static Logger logger = LoggerFactory.getLogger(ECSchedulingService.class.toString());

    public static final int NUMBER_OF_PILLS_IN_ONE_OCP_STRIP = 28;
    private final ScheduleTrackingService scheduleTrackingService;
    private final ActionService actionService;

    private final Schedule fpComplicationSchedule = new Schedule(EC_SCHEDULE_FP_COMPLICATION, asList(EC_SCHEDULE_FP_COMPLICATION_MILESTONE));
    private final Schedule dmpaInjectableRefillSchedule = new Schedule(EC_SCHEDULE_DMPA_INJECTABLE_REFILL, asList(EC_SCHEDULE_DMPA_INJECTABLE_REFILL_MILESTONE));
    private final Schedule ocpRefillSchedule = new Schedule(EC_SCHEDULE_OCP_REFILL, asList(EC_SCHEDULE_OCP_REFILL_MILESTONE));
    private final Schedule condomRefillSchedule = new Schedule(EC_SCHEDULE_CONDOM_REFILL, asList(EC_SCHEDULE_CONDOM_REFILL_MILESTONE));

    @Autowired
    public ECSchedulingService(ScheduleTrackingService scheduleTrackingService, ActionService actionService) {
        this.scheduleTrackingService = scheduleTrackingService;
        this.actionService = actionService;
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

    public void registerEC(FPProductInformation fpInfo) {
        if (DMPA_INJECTABLE_FP_METHOD_VALUE.equalsIgnoreCase(fpInfo.currentFPMethod())) {
            enrollECToDMPAInjectableSchedule(fpInfo.entityId(), fpInfo.dmpaInjectionDate());
            return;
        }
        if (OCP_FP_METHOD_VALUE.equalsIgnoreCase(fpInfo.currentFPMethod())) {
            enrollECToOCPRefillSchedule(fpInfo.entityId(), fpInfo.numberOfOCPStripsSupplied(), fpInfo.ocpRefillDate());
            return;
        }
        if (CONDOM_FP_METHOD_VALUE.equalsIgnoreCase(fpInfo.currentFPMethod())) {
            enrollECToCondomRefillSchedule(fpInfo.entityId());
        }
    }

    public void fpChange(FPProductInformation fpInfo) {
        unEnrollECFromPreviousRefillSchedule(fpInfo.anmId(), fpInfo.entityId(),
                fpInfo.previousFPMethod(), fpInfo.currentFPMethod(), fpInfo.fpMethodChangeDate());
        registerEC(new FPProductInformation(fpInfo.entityId(), fpInfo.anmId(),
                fpInfo.currentFPMethod(), fpInfo.previousFPMethod(), fpInfo.fpMethodChangeDate(), fpInfo.numberOfOCPStripsSupplied(),
                fpInfo.fpMethodChangeDate(), fpInfo.numberOfCondomsSupplied(), fpInfo.submissionDate(),
                fpInfo.fpMethodChangeDate()));
    }

    public void renewFPProduct(FPProductInformation fpProductInformation) {
        if (DMPA_INJECTABLE_FP_METHOD_VALUE.equalsIgnoreCase(fpProductInformation.currentFPMethod()) && tryParse(fpProductInformation.dmpaInjectionDate(), null) != null) {
            logger.info(format("Un-enrolling EC from DMPA Injectable Refill schedule as FP product was renewed. entityId: {0}, DMPA injection date: {1}", fpProductInformation.entityId(), fpProductInformation.dmpaInjectionDate()));
            unEnrollECFromDMPAInjectableSchedule(fpProductInformation.entityId(), fpProductInformation.anmId(), fpProductInformation.dmpaInjectionDate());
            enrollECToDMPAInjectableSchedule(fpProductInformation.entityId(), fpProductInformation.dmpaInjectionDate());
            return;
        }
        if (OCP_FP_METHOD_VALUE.equalsIgnoreCase(fpProductInformation.currentFPMethod()) && tryParse(fpProductInformation.numberOfOCPStripsSupplied(), 0) > 0) {
            logger.info(format("Un-enrolling EC from OCP Refill schedule as FP product was renewed. entityId: {0}, ocpRefillDate: {1}, numberOfOCPStripsSupplied: {2}", fpProductInformation.entityId(), fpProductInformation.ocpRefillDate(), fpProductInformation.numberOfOCPStripsSupplied()));
            unEnrollECFromOCPRefillSchedule(fpProductInformation.entityId(), fpProductInformation.anmId(), fpProductInformation.ocpRefillDate());
            enrollECToOCPRefillSchedule(fpProductInformation.entityId(), fpProductInformation.numberOfOCPStripsSupplied(), fpProductInformation.ocpRefillDate());
            return;
        }
        if (CONDOM_FP_METHOD_VALUE.equalsIgnoreCase(fpProductInformation.currentFPMethod()) && tryParse(fpProductInformation.numberOfCondomsSupplied(), 0) > 0) {
            logger.info(format("Un-enrolling EC from Condom Refill schedule as FP product was renewed. entityId: {0}, condomRefillDate: {1}, numberOfCondomsSupplied: {2}", fpProductInformation.entityId(), fpProductInformation.submissionDate(), fpProductInformation.numberOfCondomsSupplied()));
            unEnrollECFromCondomRefillSchedule(fpProductInformation.entityId(), fpProductInformation.anmId(), fpProductInformation.submissionDate());
            enrollECToCondomRefillSchedule(fpProductInformation.entityId());
        }
    }

    private void unEnrollECFromPreviousRefillSchedule(String anmId, String entityId, String previousFPMethod, String newFPMethod, String fpMethodChangeDate) {
        if (DMPA_INJECTABLE_FP_METHOD_VALUE.equalsIgnoreCase(previousFPMethod)) {
            logger.info(format("Un-enrolling EC from DMPA Injectable Refill schedule as FP method changed. entityId: {0}, new fp method: {1}", entityId, newFPMethod));

            unEnrollECFromDMPAInjectableSchedule(entityId, anmId, fpMethodChangeDate);
            return;
        }
        if (OCP_FP_METHOD_VALUE.equalsIgnoreCase(previousFPMethod)) {
            logger.info(format("Un-enrolling EC from OCP Refill schedule. entityId: {0}, new fp method: {1}", entityId, newFPMethod));

            unEnrollECFromOCPRefillSchedule(entityId, anmId, fpMethodChangeDate);
            return;
        }
        if (CONDOM_FP_METHOD_VALUE.equalsIgnoreCase(previousFPMethod)) {
            logger.info(format("Un-enrolling EC from Condom Refill schedule as FP method changed. entityId: {0}, new fp method: {1}", entityId, newFPMethod));

            unEnrollECFromCondomRefillSchedule(entityId, anmId, fpMethodChangeDate);
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

    private void enrollECToCondomRefillSchedule(String entityId) {
        logger.info(format("Enrolling EC to Condom Refill schedule. entityId: {0}, Ref date: {1}", entityId, firstDayOfNextMonth()));
        scheduleTrackingService.enroll(new EnrollmentRequest(entityId, condomRefillSchedule.name(), new Time(PREFERED_TIME_FOR_SCHEDULES),
                firstDayOfNextMonth(), null, null, null, null, null));
    }

    private void enrollECToDMPAInjectableSchedule(String entityId, String dmpaInjectionDate) {
        logger.info(format("Enrolling EC to DMPA Injectable Refill schedule. entityId: {0}, Injection date: {1}", entityId, dmpaInjectionDate));
        scheduleTrackingService.enroll(new EnrollmentRequest(entityId, dmpaInjectableRefillSchedule.name(), new Time(PREFERED_TIME_FOR_SCHEDULES),
                parse(dmpaInjectionDate), null, null, null, null, null));
    }

    private void enrollECToOCPRefillSchedule(String entityId, String numberOfOCPStripsSupplied, String ocpRefillDate) {
        LocalDate scheduleStartDate = (tryParse(numberOfOCPStripsSupplied, 0) == 0) ? today() : twoWeeksBeforeOCPPillsRunOut(numberOfOCPStripsSupplied, ocpRefillDate);
        logger.info(format("Enrolling EC to OCP Refill schedule. entityId: {0}, Refill date: {1}, Ref date: {2}, Number of OCP Strips : {3}", entityId, ocpRefillDate, scheduleStartDate, numberOfOCPStripsSupplied));
        scheduleTrackingService.enroll(new EnrollmentRequest(entityId, ocpRefillSchedule.name(), new Time(PREFERED_TIME_FOR_SCHEDULES),
                scheduleStartDate, null, null, null, null, null));
    }

    private void unEnrollECFromDMPAInjectableSchedule(String entityId, String anmId, String dmpaInjectionDate) {
        scheduleTrackingService.unenroll(entityId, asList(dmpaInjectableRefillSchedule.name()));
        actionService.markAlertAsClosed(entityId, anmId, dmpaInjectableRefillSchedule.name(), dmpaInjectionDate);
    }

    private void unEnrollECFromOCPRefillSchedule(String entityId, String anmId, String ocpRefillDate) {
        scheduleTrackingService.unenroll(entityId, asList(ocpRefillSchedule.name()));
        actionService.markAlertAsClosed(entityId, anmId, ocpRefillSchedule.name(), ocpRefillDate);
    }

    private void unEnrollECFromCondomRefillSchedule(String entityId, String anmId, String submissionDate) {
        scheduleTrackingService.unenroll(entityId, asList(condomRefillSchedule.name()));
        actionService.markAlertAsClosed(entityId, anmId, condomRefillSchedule.name(), submissionDate);
    }

    private LocalDate firstDayOfNextMonth() {
        return today().plusMonths(1).withDayOfMonth(1);
    }

    private LocalDate twoWeeksBeforeOCPPillsRunOut(String numberOfOCPStripsSupplied, String ocpRefillDate) {
        return parse(ocpRefillDate)
                .plusDays(parseInt(numberOfOCPStripsSupplied) * NUMBER_OF_PILLS_IN_ONE_OCP_STRIP)
                .minusDays(2 * DAYS_PER_WEEK);
    }
}