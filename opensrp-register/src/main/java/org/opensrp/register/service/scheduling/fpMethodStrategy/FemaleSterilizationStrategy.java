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
import static org.opensrp.scheduler.DrishtiScheduleConstants.ECSchedulesConstants.*;
import static org.joda.time.LocalDate.parse;

@Component
public class FemaleSterilizationStrategy implements FPMethodStrategy {
    private static Logger logger = LoggerFactory.getLogger(FemaleSterilizationStrategy.class.toString());
    private final ScheduleTrackingService scheduleTrackingService;
    private final ActionService actionService;
    private final ScheduleService scheduleService;
    private final Schedule femaleSterilizationFollowupSchedule = new Schedule(EC_SCHEDULE_FEMALE_STERILIZATION_FOLLOWUP,
            asList(EC_SCHEDULE_FEMALE_STERILIZATION_FOLLOWUP_MILESTONE_1,
                    EC_SCHEDULE_FEMALE_STERILIZATION_FOLLOWUP_MILESTONE_2,
                    EC_SCHEDULE_FEMALE_STERILIZATION_FOLLOWUP_MILESTONE_3));

    @Autowired
    public FemaleSterilizationStrategy(ScheduleTrackingService scheduleTrackingService, ActionService actionService, ScheduleService scheduleService) {
        this.scheduleTrackingService = scheduleTrackingService;
        this.actionService = actionService;
        this.scheduleService = scheduleService;
    }

    @Override
    public void registerEC(FPProductInformation fpInfo) {
        enrollECToFemaleSterilizationSchedule(fpInfo.entityId(), fpInfo.fpMethodChangeDate());
    }

    @Override
    public void unEnrollFromPreviousScheduleAsFPMethodChanged(FPProductInformation fpInfo) {
        logger.info(format("Un-enrolling EC from Female sterilization Followup schedule as FP method changed. entityId: {0}, new fp method: {1}",
                fpInfo.entityId(), fpInfo.currentFPMethod()));
        unEnrollECFromFemaleSterilizationSchedule(fpInfo.entityId(), fpInfo.anmId(), fpInfo.fpMethodChangeDate());
    }

    @Override
    public void enrollToNewScheduleForNewFPMethod(FPProductInformation fpInfo) {
        enrollECToFemaleSterilizationSchedule(fpInfo.entityId(), fpInfo.fpMethodChangeDate());
    }

    @Override
    public void renewFPProduct(FPProductInformation fpInfo) {
    }

    @Override
    public void fpFollowup(FPProductInformation fpInfo) {
        String currentMilestone = getCurrentMilestone(fpInfo);
        logger.info(format("Fulfilling current milestone For Female sterilization Followup schedule. entityId: {0}, Ref date: {1}, currentMilestone: {2}",
                fpInfo.entityId(), fpInfo.submissionDate(), currentMilestone));
        scheduleTrackingService.fulfillCurrentMilestone(fpInfo.entityId(), femaleSterilizationFollowupSchedule.name(), parse(fpInfo.fpFollowupDate()));
        actionService.markAlertAsClosed(fpInfo.entityId(), fpInfo.anmId(), currentMilestone, fpInfo.fpFollowupDate());
    }

    private void enrollECToFemaleSterilizationSchedule(String entityId, String referenceDate) {
        logger.info(format("Enrolling EC to Female sterilization Followup schedule. entityId: {0}, Ref date: {1}", entityId, referenceDate));
        scheduleService.enroll(entityId, femaleSterilizationFollowupSchedule.name(), referenceDate);
    }

    private void unEnrollECFromFemaleSterilizationSchedule(String entityId, String anmId, String submissionDate) {
        scheduleTrackingService.unenroll(entityId, asList(femaleSterilizationFollowupSchedule.name()));
        actionService.markAlertAsClosed(entityId, anmId, femaleSterilizationFollowupSchedule.name(), submissionDate);
    }

    private String getCurrentMilestone(FPProductInformation fpInfo) {
        return scheduleTrackingService.getEnrollment(fpInfo.entityId(), femaleSterilizationFollowupSchedule.name()).getCurrentMilestoneName();
    }
}
