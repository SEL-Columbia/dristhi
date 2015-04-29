package org.opensrp.service.scheduling.fpMethodStrategy;

import static java.text.MessageFormat.format;
import static java.util.Arrays.asList;
import static org.joda.time.LocalDate.parse;
import static org.opensrp.scheduler.DrishtiScheduleConstants.ECSchedulesConstants.EC_SCHEDULE_FEMALE_STERILIZATION_FOLLOWUP;
import static org.opensrp.scheduler.DrishtiScheduleConstants.ECSchedulesConstants.EC_SCHEDULE_FEMALE_STERILIZATION_FOLLOWUP_MILESTONE_1;
import static org.opensrp.scheduler.DrishtiScheduleConstants.ECSchedulesConstants.EC_SCHEDULE_FEMALE_STERILIZATION_FOLLOWUP_MILESTONE_2;
import static org.opensrp.scheduler.DrishtiScheduleConstants.ECSchedulesConstants.EC_SCHEDULE_FEMALE_STERILIZATION_FOLLOWUP_MILESTONE_3;

import org.opensrp.domain.FPProductInformation;
import org.opensrp.scheduler.HealthSchedulerService;
import org.opensrp.scheduler.Schedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FemaleSterilizationStrategy implements FPMethodStrategy {
    private static Logger logger = LoggerFactory.getLogger(FemaleSterilizationStrategy.class.toString());
    private HealthSchedulerService scheduler;

    private final Schedule femaleSterilizationFollowupSchedule = new Schedule(EC_SCHEDULE_FEMALE_STERILIZATION_FOLLOWUP,
            asList(EC_SCHEDULE_FEMALE_STERILIZATION_FOLLOWUP_MILESTONE_1,
                    EC_SCHEDULE_FEMALE_STERILIZATION_FOLLOWUP_MILESTONE_2,
                    EC_SCHEDULE_FEMALE_STERILIZATION_FOLLOWUP_MILESTONE_3));

    @Autowired
    public FemaleSterilizationStrategy(HealthSchedulerService scheduler) {
        this.scheduler = scheduler;
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
        scheduler.fullfillMilestoneAndCloseAlert(fpInfo.entityId(), fpInfo.anmId(), femaleSterilizationFollowupSchedule.name(), currentMilestone, parse(fpInfo.fpFollowupDate()));
    }

    private void enrollECToFemaleSterilizationSchedule(String entityId, String referenceDate) {
        logger.info(format("Enrolling EC to Female sterilization Followup schedule. entityId: {0}, Ref date: {1}", entityId, referenceDate));
        scheduler.enrollIntoSchedule(entityId, femaleSterilizationFollowupSchedule.name(), referenceDate);
    }

    private void unEnrollECFromFemaleSterilizationSchedule(String entityId, String anmId, String submissionDate) {
        scheduler.unEnrollAndCloseSchedule(entityId, anmId, femaleSterilizationFollowupSchedule.name(), parse(submissionDate));
    }

    private String getCurrentMilestone(FPProductInformation fpInfo) {
        return scheduler.getEnrollment(fpInfo.entityId(), femaleSterilizationFollowupSchedule.name()).getCurrentMilestoneName();
    }
}
