package org.opensrp.register.service.scheduling.fpMethodStrategy;

import static java.text.MessageFormat.format;
import static java.util.Arrays.asList;
import static org.joda.time.LocalDate.parse;
import static org.opensrp.register.DrishtiScheduleConstants.ECSchedulesConstants.EC_SCHEDULE_MALE_STERILIZATION_FOLLOWUP;
import static org.opensrp.register.DrishtiScheduleConstants.ECSchedulesConstants.EC_SCHEDULE_MALE_STERILIZATION_FOLLOWUP_MILESTONE_1;
import static org.opensrp.register.DrishtiScheduleConstants.ECSchedulesConstants.EC_SCHEDULE_MALE_STERILIZATION_FOLLOWUP_MILESTONE_2;

import org.opensrp.register.domain.FPProductInformation;
import org.opensrp.scheduler.HealthSchedulerService;
import org.opensrp.scheduler.Schedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MaleSterilizationStrategy implements FPMethodStrategy {
    private static Logger logger = LoggerFactory.getLogger(MaleSterilizationStrategy.class.toString());
    private HealthSchedulerService scheduler;

    private final Schedule maleSterilizationFollowupSchedule = new Schedule(EC_SCHEDULE_MALE_STERILIZATION_FOLLOWUP,
            asList(EC_SCHEDULE_MALE_STERILIZATION_FOLLOWUP_MILESTONE_1,
                    EC_SCHEDULE_MALE_STERILIZATION_FOLLOWUP_MILESTONE_2));

    @Autowired
    public MaleSterilizationStrategy(HealthSchedulerService scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public void registerEC(FPProductInformation fpInfo) {
        enrollECToMaleSterilizationSchedule(fpInfo.entityId(), fpInfo.fpMethodChangeDate());
    }

    @Override
    public void unEnrollFromPreviousScheduleAsFPMethodChanged(FPProductInformation fpInfo) {
        logger.info(format("Un-enrolling EC from Male sterilization Followup schedule as FP method changed. entityId: {0}, new fp method: {1}",
                fpInfo.entityId(), fpInfo.currentFPMethod()));
        unEnrollECFromMaleSterilizationSchedule(fpInfo.entityId(), fpInfo.anmId(), fpInfo.fpMethodChangeDate());
    }

    @Override
    public void enrollToNewScheduleForNewFPMethod(FPProductInformation fpInfo) {
        enrollECToMaleSterilizationSchedule(fpInfo.entityId(), fpInfo.fpMethodChangeDate());
    }

    @Override
    public void renewFPProduct(FPProductInformation fpInfo) {
    }

    @Override
    public void fpFollowup(FPProductInformation fpInfo) {
        String currentMilestone = getCurrentMilestone(fpInfo);
        logger.info(format("Fulfilling current milestone For Male sterilization Followup schedule. entityId: {0}, Ref date: {1}, currentMilestone: {2}",
                fpInfo.entityId(), fpInfo.submissionDate(), currentMilestone));
        scheduler.fullfillMilestoneAndCloseAlert(fpInfo.entityId(), fpInfo.anmId(), maleSterilizationFollowupSchedule.name(), currentMilestone, parse(fpInfo.fpFollowupDate()));
    }

    private void enrollECToMaleSterilizationSchedule(String entityId, String referenceDate) {
        logger.info(format("Enrolling EC to Male sterilization Followup schedule. entityId: {0}, Ref date: {1}", entityId, referenceDate));
        scheduler.enrollIntoSchedule(entityId, maleSterilizationFollowupSchedule.name(), referenceDate);
    }

    private void unEnrollECFromMaleSterilizationSchedule(String entityId, String anmId, String submissionDate) {
        scheduler.unEnrollAndCloseSchedule(entityId, anmId, maleSterilizationFollowupSchedule.name(), parse(submissionDate));
    }

    private String getCurrentMilestone(FPProductInformation fpInfo) {
        return scheduler.getEnrollment(fpInfo.entityId(), maleSterilizationFollowupSchedule.name()).getCurrentMilestoneName();
    }
}
