package org.ei.drishti.service.scheduling.fpMethodStrategy;

import org.ei.drishti.contract.Schedule;
import org.ei.drishti.domain.FPProductInformation;
import org.ei.drishti.service.ActionService;
import org.ei.drishti.service.scheduling.ScheduleService;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static java.lang.Integer.parseInt;
import static java.text.MessageFormat.format;
import static java.util.Arrays.asList;
import static org.ei.drishti.common.util.DateUtil.today;
import static org.ei.drishti.common.util.IntegerUtil.tryParse;
import static org.ei.drishti.dto.AlertStatus.upcoming;
import static org.ei.drishti.dto.BeneficiaryType.ec;
import static org.ei.drishti.scheduler.DrishtiScheduleConstants.ECSchedulesConstants.EC_SCHEDULE_OCP_REFILL;
import static org.ei.drishti.scheduler.DrishtiScheduleConstants.ECSchedulesConstants.EC_SCHEDULE_OCP_REFILL_MILESTONE;
import static org.joda.time.DateTimeConstants.DAYS_PER_WEEK;
import static org.joda.time.LocalDate.parse;

@Component
public class OCPStrategy implements FPMethodStrategy {
    public static final int NUMBER_OF_PILLS_IN_ONE_OCP_STRIP = 28;
    public static final int DUE_WINDOW_PERIOD_IN_WEEKS = 1;

    private static Logger logger = LoggerFactory.getLogger(OCPStrategy.class.toString());

    private final ScheduleTrackingService scheduleTrackingService;
    private final ActionService actionService;
    private final ScheduleService scheduleService;
    private final LocalTime preferredTime;
    private final Schedule ocpRefillSchedule = new Schedule(EC_SCHEDULE_OCP_REFILL, asList(EC_SCHEDULE_OCP_REFILL_MILESTONE));

    @Autowired
    public OCPStrategy(ScheduleTrackingService scheduleTrackingService, ActionService actionService, ScheduleService scheduleService,
                       @Value("#{drishti['preferred.time']}") int preferredTime) {
        this.scheduleTrackingService = scheduleTrackingService;
        this.actionService = actionService;
        this.scheduleService = scheduleService;
        this.preferredTime = new LocalTime(preferredTime, 0);
    }

    @Override
    public void registerEC(FPProductInformation fpInfo) {
        enrollECToOCPRefillSchedule(fpInfo.entityId(), fpInfo.numberOfOCPStripsSupplied(), fpInfo.ocpRefillDate());
    }

    @Override
    public void unEnrollFromPreviousScheduleAsFPMethodChanged(FPProductInformation fpInfo) {
        logger.info(format("Un-enrolling EC from OCP Refill schedule. entityId: {0}, new fp method: {1}", fpInfo.entityId(), fpInfo.currentFPMethod()));
        scheduleTrackingService.unenroll(fpInfo.entityId(), asList(ocpRefillSchedule.name()));
        actionService.markAlertAsClosed(fpInfo.entityId(), fpInfo.anmId(), ocpRefillSchedule.name(), fpInfo.fpMethodChangeDate());
    }

    @Override
    public void enrollToNewScheduleForNewFPMethod(FPProductInformation fpInfo) {
        enrollECToOCPRefillSchedule(fpInfo.entityId(), fpInfo.numberOfOCPStripsSupplied(), fpInfo.fpMethodChangeDate());
    }

    @Override
    public void renewFPProduct(FPProductInformation fpInfo) {
        if (tryParse(fpInfo.numberOfOCPStripsSupplied(), 0) <= 0) {
            return;
        }

        logger.info(format("Fulfilling OCP Refill schedule as FP product was renewed. entityId: {0}, ocpRefillDate: {1}, numberOfOCPStripsSupplied: {2}", fpInfo.entityId(), fpInfo.ocpRefillDate(), fpInfo.numberOfOCPStripsSupplied()));
        scheduleTrackingService.fulfillCurrentMilestone(fpInfo.entityId(), ocpRefillSchedule.name(), parse(fpInfo.submissionDate()));
        actionService.markAlertAsClosed(fpInfo.entityId(), fpInfo.anmId(), ocpRefillSchedule.name(), fpInfo.ocpRefillDate());
        enrollECToOCPRefillSchedule(fpInfo.entityId(), fpInfo.numberOfOCPStripsSupplied(), fpInfo.ocpRefillDate());
    }

    @Override
    public void fpFollowup(FPProductInformation fpInfo) {
    }

    private void enrollECToOCPRefillSchedule(String entityId, String numberOfOCPStripsSupplied, String ocpRefillDate) {
        LocalDate scheduleStartDate = (tryParse(numberOfOCPStripsSupplied, 0) == 0) ? today() : twoWeeksBeforeOCPPillsRunOut(numberOfOCPStripsSupplied, ocpRefillDate);
        logger.info(format("Enrolling EC to OCP Refill schedule. entityId: {0}, Refill date: {1}, Ref date: {2}, Number of OCP Strips : {3}", entityId, ocpRefillDate, scheduleStartDate, numberOfOCPStripsSupplied));
        scheduleService.enroll(entityId, ocpRefillSchedule.name(), scheduleStartDate.toString());
        actionService.alertForBeneficiary(ec, entityId, EC_SCHEDULE_OCP_REFILL, EC_SCHEDULE_OCP_REFILL_MILESTONE, upcoming,
                scheduleStartDate.toDateTime(preferredTime), scheduleStartDate.plusWeeks(DUE_WINDOW_PERIOD_IN_WEEKS).toDateTime(preferredTime));

    }

    private LocalDate twoWeeksBeforeOCPPillsRunOut(String numberOfOCPStripsSupplied, String ocpRefillDate) {
        return parse(ocpRefillDate)
                .plusDays(parseInt(numberOfOCPStripsSupplied) * NUMBER_OF_PILLS_IN_ONE_OCP_STRIP)
                .minusDays(2 * DAYS_PER_WEEK);
    }

}
