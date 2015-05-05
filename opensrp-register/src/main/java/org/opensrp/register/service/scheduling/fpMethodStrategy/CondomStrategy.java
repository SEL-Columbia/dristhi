package org.opensrp.register.service.scheduling.fpMethodStrategy;

import static java.text.MessageFormat.format;
import static java.util.Arrays.asList;
import static org.joda.time.LocalDate.parse;
import static org.opensrp.common.util.DateUtil.today;
import static org.opensrp.common.util.IntegerUtil.tryParse;
import static org.opensrp.dto.AlertStatus.upcoming;
import static org.opensrp.dto.BeneficiaryType.ec;
import static org.opensrp.register.DrishtiScheduleConstants.ECSchedulesConstants.EC_SCHEDULE_CONDOM_REFILL;
import static org.opensrp.register.DrishtiScheduleConstants.ECSchedulesConstants.EC_SCHEDULE_CONDOM_REFILL_MILESTONE;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.opensrp.domain.FPProductInformation;
import org.opensrp.scheduler.HealthSchedulerService;
import org.opensrp.scheduler.Schedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CondomStrategy implements FPMethodStrategy {
    private static final int DUE_WINDOW_PERIOD_IN_WEEKS = 1;
    private static Logger logger = LoggerFactory.getLogger(CondomStrategy.class.toString());

    private HealthSchedulerService scheduler;

    private final Schedule condomRefillSchedule = new Schedule(EC_SCHEDULE_CONDOM_REFILL, asList(EC_SCHEDULE_CONDOM_REFILL_MILESTONE));
    private LocalTime preferredTime;

    @Autowired
    public CondomStrategy(HealthSchedulerService scheduler, @Value("#{opensrp['preferred.time']}") int preferredTime) {
        this.scheduler = scheduler;
        this.preferredTime = new LocalTime(preferredTime, 0);
    }

    @Override
    public void registerEC(FPProductInformation fpInfo) {
        enrollECToCondomRefillSchedule(fpInfo.entityId(), fpInfo.anmId());
    }

    @Override
    public void unEnrollFromPreviousScheduleAsFPMethodChanged(FPProductInformation fpInfo) {
        logger.info(format("Un-enrolling EC from Condom Refill schedule as FP method changed. entityId: {0}, new fp method: {1}", fpInfo.entityId(), fpInfo.currentFPMethod()));
        scheduler.unEnrollAndCloseSchedule(fpInfo.entityId(), fpInfo.anmId(), condomRefillSchedule.name(), parse(fpInfo.fpMethodChangeDate()));
    }

    @Override
    public void enrollToNewScheduleForNewFPMethod(FPProductInformation fpInfo) {
        enrollECToCondomRefillSchedule(fpInfo.entityId(), fpInfo.anmId());
    }

    @Override
    public void renewFPProduct(FPProductInformation fpInfo) {
        if (tryParse(fpInfo.numberOfCondomsSupplied(), 0) <= 0) {
            return;
        }

        logger.info(format("Fulfilling Condom Refill schedule as FP product was renewed. entityId: {0}, condomRefillDate: {1}, numberOfCondomsSupplied: {2}", fpInfo.entityId(), fpInfo.submissionDate(), fpInfo.numberOfCondomsSupplied()));
        scheduler.fullfillMilestoneAndCloseAlert(fpInfo.entityId(), fpInfo.anmId(), condomRefillSchedule.name(), parse(fpInfo.submissionDate()));
        enrollECToCondomRefillSchedule(fpInfo.entityId(),fpInfo.anmId());
    }

    @Override
    public void fpFollowup(FPProductInformation fpInfo) {
    }

    private void enrollECToCondomRefillSchedule(String entityId, String anmIdentifier) {
        logger.info(format("Enrolling EC to Condom Refill schedule. entityId: {0}, Ref date: {1}", entityId, firstDayOfNextMonth()));
        scheduler.enrollIntoSchedule(entityId, condomRefillSchedule.name(), firstDayOfNextMonth());
        scheduler.alertFor(ec, entityId, anmIdentifier, condomRefillSchedule.name(), EC_SCHEDULE_CONDOM_REFILL_MILESTONE, upcoming,
                firstDayOfNextMonth().toDateTime(preferredTime), firstDayOfNextMonth().plusWeeks(DUE_WINDOW_PERIOD_IN_WEEKS).toDateTime(preferredTime));
    }

    private LocalDate firstDayOfNextMonth() {
        return today().plusMonths(1).withDayOfMonth(1);
    }

}
