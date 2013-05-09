package org.ei.drishti.service.scheduling.fpMethodStrategy;

import org.ei.drishti.contract.Schedule;
import org.ei.drishti.domain.FPProductInformation;
import org.ei.drishti.service.ActionService;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.text.MessageFormat.format;
import static java.util.Arrays.asList;
import static org.ei.drishti.common.util.DateUtil.tryParse;
import static org.ei.drishti.scheduler.DrishtiScheduleConstants.ECSchedulesConstants.EC_SCHEDULE_DMPA_INJECTABLE_REFILL;
import static org.ei.drishti.scheduler.DrishtiScheduleConstants.ECSchedulesConstants.EC_SCHEDULE_DMPA_INJECTABLE_REFILL_MILESTONE;
import static org.ei.drishti.scheduler.DrishtiScheduleConstants.PREFERED_TIME_FOR_SCHEDULES;
import static org.joda.time.LocalDate.parse;

public class DMPAInjectableStrategy implements FPMethodStrategy {
    private static Logger logger = LoggerFactory.getLogger(DMPAInjectableStrategy.class.toString());

    private final ScheduleTrackingService scheduleTrackingService;
    private final ActionService actionService;
    private final Schedule dmpaInjectableRefillSchedule = new Schedule(EC_SCHEDULE_DMPA_INJECTABLE_REFILL, asList(EC_SCHEDULE_DMPA_INJECTABLE_REFILL_MILESTONE));

    public DMPAInjectableStrategy(ScheduleTrackingService scheduleTrackingService, ActionService actionService) {
        this.scheduleTrackingService = scheduleTrackingService;
        this.actionService = actionService;
    }

    @Override
    public void registerEC(FPProductInformation fpInfo) {
        enrollECToDMPAInjectableSchedule(fpInfo.entityId(), fpInfo.dmpaInjectionDate());
    }

    @Override
    public void unEnrollFromRefillScheduleAsFPMethodChanged(FPProductInformation fpInfo) {
        logger.info(format("Un-enrolling EC from DMPA Injectable Refill schedule as FP method changed. entityId: {0}, new fp method: {1}", fpInfo.entityId(), fpInfo.currentFPMethod()));
        unEnrollECFromDMPAInjectableSchedule(fpInfo.entityId(), fpInfo.anmId(), fpInfo.fpMethodChangeDate());
    }

    @Override
    public void enrollToRefillScheduleForNewFPMethod(FPProductInformation fpInfo) {
        enrollECToDMPAInjectableSchedule(fpInfo.entityId(), fpInfo.fpMethodChangeDate());
    }

    @Override
    public void renewFPProduct(FPProductInformation fpInfo) {
        if (tryParse(fpInfo.dmpaInjectionDate(), null) == null) {
            return;
        }

        logger.info(format("Un-enrolling EC from DMPA Injectable Refill schedule as FP product was renewed. entityId: {0}, DMPA injection date: {1}", fpInfo.entityId(), fpInfo.dmpaInjectionDate()));
        unEnrollECFromDMPAInjectableSchedule(fpInfo.entityId(), fpInfo.anmId(), fpInfo.dmpaInjectionDate());
        enrollECToDMPAInjectableSchedule(fpInfo.entityId(), fpInfo.dmpaInjectionDate());
    }

    private void enrollECToDMPAInjectableSchedule(String entityId, String dmpaInjectionDate) {
        logger.info(format("Enrolling EC to DMPA Injectable Refill schedule. entityId: {0}, Injection date: {1}", entityId, dmpaInjectionDate));
        scheduleTrackingService.enroll(new EnrollmentRequest(entityId, dmpaInjectableRefillSchedule.name(), new Time(PREFERED_TIME_FOR_SCHEDULES),
                parse(dmpaInjectionDate), null, null, null, null, null));
    }

    private void unEnrollECFromDMPAInjectableSchedule(String entityId, String anmId, String dmpaInjectionDate) {
        scheduleTrackingService.unenroll(entityId, asList(dmpaInjectableRefillSchedule.name()));
        actionService.markAlertAsClosed(entityId, anmId, dmpaInjectableRefillSchedule.name(), dmpaInjectionDate);
    }
}
