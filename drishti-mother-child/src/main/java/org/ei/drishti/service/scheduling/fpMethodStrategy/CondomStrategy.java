package org.ei.drishti.service.scheduling.fpMethodStrategy;

import org.ei.drishti.contract.Schedule;
import org.ei.drishti.domain.FPProductInformation;
import org.ei.drishti.service.ActionService;
import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.text.MessageFormat.format;
import static java.util.Arrays.asList;
import static org.ei.drishti.common.util.DateUtil.today;
import static org.ei.drishti.common.util.IntegerUtil.tryParse;
import static org.ei.drishti.scheduler.DrishtiScheduleConstants.ECSchedulesConstants.EC_SCHEDULE_CONDOM_REFILL;
import static org.ei.drishti.scheduler.DrishtiScheduleConstants.ECSchedulesConstants.EC_SCHEDULE_CONDOM_REFILL_MILESTONE;
import static org.ei.drishti.scheduler.DrishtiScheduleConstants.PREFERED_TIME_FOR_SCHEDULES;
import static org.joda.time.LocalDate.parse;

@Component
public class CondomStrategy implements FPMethodStrategy {
    private static Logger logger = LoggerFactory.getLogger(CondomStrategy.class.toString());

    private final ScheduleTrackingService scheduleTrackingService;
    private final ActionService actionService;
    private final Schedule condomRefillSchedule = new Schedule(EC_SCHEDULE_CONDOM_REFILL, asList(EC_SCHEDULE_CONDOM_REFILL_MILESTONE));

    @Autowired
    public CondomStrategy(ScheduleTrackingService scheduleTrackingService, ActionService actionService) {
        this.scheduleTrackingService = scheduleTrackingService;
        this.actionService = actionService;
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

        logger.info(format("Un-enrolling EC from Condom Refill schedule as FP product was renewed. entityId: {0}, condomRefillDate: {1}, numberOfCondomsSupplied: {2}", fpInfo.entityId(), fpInfo.submissionDate(), fpInfo.numberOfCondomsSupplied()));
        scheduleTrackingService.fulfillCurrentMilestone(fpInfo.entityId(), condomRefillSchedule.name(), parse(fpInfo.submissionDate()));
        actionService.markAlertAsClosed(fpInfo.entityId(), fpInfo.anmId(), condomRefillSchedule.name(), fpInfo.submissionDate());
        enrollECToCondomRefillSchedule(fpInfo.entityId());
    }

    @Override
    public void fpFollowup(FPProductInformation fpInfo) {
    }

    private void enrollECToCondomRefillSchedule(String entityId) {
        logger.info(format("Enrolling EC to Condom Refill schedule. entityId: {0}, Ref date: {1}", entityId, firstDayOfNextMonth()));
        scheduleTrackingService.enroll(new EnrollmentRequest(entityId, condomRefillSchedule.name(), new Time(PREFERED_TIME_FOR_SCHEDULES),
                firstDayOfNextMonth(), null, null, null, null, null));
    }

    private LocalDate firstDayOfNextMonth() {
        return today().plusMonths(1).withDayOfMonth(1);
    }

}
