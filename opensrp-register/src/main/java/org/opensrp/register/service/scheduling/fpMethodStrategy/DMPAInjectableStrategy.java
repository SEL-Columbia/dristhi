package org.opensrp.register.service.scheduling.fpMethodStrategy;

import org.opensrp.domain.FPProductInformation;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.opensrp.contract.Schedule;
import org.opensrp.register.service.ActionService;
import org.opensrp.service.scheduling.ScheduleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.text.MessageFormat.format;
import static java.util.Arrays.asList;
import static org.opensrp.common.util.DateUtil.tryParse;
import static org.opensrp.scheduler.DrishtiScheduleConstants.ECSchedulesConstants.EC_SCHEDULE_DMPA_INJECTABLE_REFILL;
import static org.opensrp.scheduler.DrishtiScheduleConstants.ECSchedulesConstants.EC_SCHEDULE_DMPA_INJECTABLE_REFILL_MILESTONE;
import static org.joda.time.LocalDate.parse;

@Component
public class DMPAInjectableStrategy implements FPMethodStrategy {
    private static Logger logger = LoggerFactory.getLogger(DMPAInjectableStrategy.class.toString());

    private final ScheduleTrackingService scheduleTrackingService;
    private final ActionService actionService;
    private final ScheduleService scheduleService;
    private final Schedule dmpaInjectableRefillSchedule = new Schedule(EC_SCHEDULE_DMPA_INJECTABLE_REFILL, asList(EC_SCHEDULE_DMPA_INJECTABLE_REFILL_MILESTONE));

    @Autowired
    public DMPAInjectableStrategy(ScheduleTrackingService scheduleTrackingService, ActionService actionService, ScheduleService scheduleService) {
        this.scheduleTrackingService = scheduleTrackingService;
        this.actionService = actionService;
        this.scheduleService = scheduleService;
    }

    @Override
    public void registerEC(FPProductInformation fpInfo) {
        enrollECToDMPAInjectableSchedule(fpInfo.entityId(), fpInfo.dmpaInjectionDate());
    }

    @Override
    public void unEnrollFromPreviousScheduleAsFPMethodChanged(FPProductInformation fpInfo) {
        logger.info(format("Un-enrolling EC from DMPA Injectable Refill schedule as FP method changed. entityId: {0}, new fp method: {1}", fpInfo.entityId(), fpInfo.currentFPMethod()));
        scheduleTrackingService.unenroll(fpInfo.entityId(), asList(dmpaInjectableRefillSchedule.name()));
        actionService.markAlertAsClosed(fpInfo.entityId(), fpInfo.anmId(), dmpaInjectableRefillSchedule.name(), fpInfo.fpMethodChangeDate());
    }

    @Override
    public void enrollToNewScheduleForNewFPMethod(FPProductInformation fpInfo) {
        enrollECToDMPAInjectableSchedule(fpInfo.entityId(), fpInfo.fpMethodChangeDate());
    }

    @Override
    public void renewFPProduct(FPProductInformation fpInfo) {
        if (tryParse(fpInfo.dmpaInjectionDate(), null) == null) {
            return;
        }

        logger.info(format("Fulfilling DMPA Injectable Refill schedule as FP product was renewed. entityId: {0}, DMPA injection date: {1}", fpInfo.entityId(), fpInfo.dmpaInjectionDate()));
        scheduleTrackingService.fulfillCurrentMilestone(fpInfo.entityId(), dmpaInjectableRefillSchedule.name(), parse(fpInfo.submissionDate()));
        actionService.markAlertAsClosed(fpInfo.entityId(), fpInfo.anmId(), dmpaInjectableRefillSchedule.name(), fpInfo.dmpaInjectionDate());
        enrollECToDMPAInjectableSchedule(fpInfo.entityId(), fpInfo.dmpaInjectionDate());
    }

    @Override
    public void fpFollowup(FPProductInformation fpInfo) {
    }

    private void enrollECToDMPAInjectableSchedule(String entityId, String dmpaInjectionDate) {
        logger.info(format("Enrolling EC to DMPA Injectable Refill schedule. entityId: {0}, Injection date: {1}", entityId, dmpaInjectionDate));
        scheduleService.enroll(entityId, dmpaInjectableRefillSchedule.name(), dmpaInjectionDate);
    }

}
