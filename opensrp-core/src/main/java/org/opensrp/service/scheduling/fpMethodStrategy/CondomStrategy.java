package org.opensrp.service.scheduling.fpMethodStrategy;

import org.opensrp.domain.FPProductInformation;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static java.text.MessageFormat.format;
import static java.util.Arrays.asList;
import static org.opensrp.common.util.DateUtil.today;
import static org.opensrp.common.util.IntegerUtil.tryParse;
import static org.opensrp.scheduler.DrishtiScheduleConstants.ECSchedulesConstants.EC_SCHEDULE_CONDOM_REFILL;
import static org.opensrp.scheduler.DrishtiScheduleConstants.ECSchedulesConstants.EC_SCHEDULE_CONDOM_REFILL_MILESTONE;
import static org.joda.time.LocalDate.parse;
import org.opensrp.contract.Schedule;
import static org.opensrp.dto.AlertStatus.upcoming;
import static org.opensrp.dto.BeneficiaryType.ec;

import org.opensrp.service.ActionService;
import org.opensrp.service.scheduling.ScheduleService;

@Component
public class CondomStrategy implements FPMethodStrategy {
    private static final int DUE_WINDOW_PERIOD_IN_WEEKS = 1;
    private static Logger logger = LoggerFactory.getLogger(CondomStrategy.class.toString());

    private final ScheduleTrackingService scheduleTrackingService;
    private final ActionService actionService;
    private final ScheduleService scheduleService;
    private final Schedule condomRefillSchedule = new Schedule(EC_SCHEDULE_CONDOM_REFILL, asList(EC_SCHEDULE_CONDOM_REFILL_MILESTONE));
    private LocalTime preferredTime;

    @Autowired
    public CondomStrategy(ScheduleTrackingService scheduleTrackingService, ActionService actionService, ScheduleService scheduleService,
                          @Value("#{opensrp['preferred.time']}") int preferredTime) {
        this.scheduleTrackingService = scheduleTrackingService;
        this.actionService = actionService;
        this.scheduleService = scheduleService;
        this.preferredTime = new LocalTime(preferredTime, 0);
    }

    @Override
    public void registerEC(FPProductInformation fpInfo) {
        enrollECToCondomRefillSchedule(fpInfo.entityId());
    }

    @Override
    public void unEnrollFromPreviousScheduleAsFPMethodChanged(FPProductInformation fpInfo) {
        logger.info(format("Un-enrolling EC from Condom Refill schedule as FP method changed. entityId: {0}, new fp method: {1}", fpInfo.entityId(), fpInfo.currentFPMethod()));
        scheduleTrackingService.unenroll(fpInfo.entityId(), asList(condomRefillSchedule.name()));
        actionService.markAlertAsClosed(fpInfo.entityId(), fpInfo.anmId(), condomRefillSchedule.name(), fpInfo.fpMethodChangeDate());
    }

    @Override
    public void enrollToNewScheduleForNewFPMethod(FPProductInformation fpInfo) {
        enrollECToCondomRefillSchedule(fpInfo.entityId());
    }

    @Override
    public void renewFPProduct(FPProductInformation fpInfo) {
        if (tryParse(fpInfo.numberOfCondomsSupplied(), 0) <= 0) {
            return;
        }

        logger.info(format("Fulfilling Condom Refill schedule as FP product was renewed. entityId: {0}, condomRefillDate: {1}, numberOfCondomsSupplied: {2}", fpInfo.entityId(), fpInfo.submissionDate(), fpInfo.numberOfCondomsSupplied()));
        scheduleTrackingService.fulfillCurrentMilestone(fpInfo.entityId(), condomRefillSchedule.name(), parse(fpInfo.submissionDate()));
        actionService.markAlertAsClosed(fpInfo.entityId(), fpInfo.anmId(), condomRefillSchedule.name(), fpInfo.submissionDate());
        enrollECToCondomRefillSchedule(fpInfo.entityId());
    }

    @Override
    public void fpFollowup(FPProductInformation fpInfo) {
    }

    private void enrollECToCondomRefillSchedule(String entityId) {
        logger.info(format("Enrolling EC to Condom Refill schedule. entityId: {0}, Ref date: {1}", entityId, firstDayOfNextMonth()));
        scheduleService.enroll(entityId, condomRefillSchedule.name(), firstDayOfNextMonth().toString());
        actionService.alertForBeneficiary(ec, entityId, condomRefillSchedule.name(), EC_SCHEDULE_CONDOM_REFILL_MILESTONE, upcoming,
                firstDayOfNextMonth().toDateTime(preferredTime), firstDayOfNextMonth().plusWeeks(DUE_WINDOW_PERIOD_IN_WEEKS).toDateTime(preferredTime));
    }

    private LocalDate firstDayOfNextMonth() {
        return today().plusMonths(1).withDayOfMonth(1);
    }

}
