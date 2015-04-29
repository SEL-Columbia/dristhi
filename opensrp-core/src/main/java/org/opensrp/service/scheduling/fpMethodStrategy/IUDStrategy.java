package org.opensrp.service.scheduling.fpMethodStrategy;

import static java.text.MessageFormat.format;
import static java.util.Arrays.asList;
import static org.joda.time.LocalDate.parse;
import static org.opensrp.scheduler.DrishtiScheduleConstants.ECSchedulesConstants.EC_SCHEDULE_IUD_FOLLOWUP;
import static org.opensrp.scheduler.DrishtiScheduleConstants.ECSchedulesConstants.EC_SCHEDULE_IUD_FOLLOWUP_MILESTONE_1;
import static org.opensrp.scheduler.DrishtiScheduleConstants.ECSchedulesConstants.EC_SCHEDULE_IUD_FOLLOWUP_MILESTONE_2;

import org.opensrp.domain.FPProductInformation;
import org.opensrp.scheduler.HealthSchedulerService;
import org.opensrp.scheduler.Schedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IUDStrategy implements FPMethodStrategy {
    private static Logger logger = LoggerFactory.getLogger(IUDStrategy.class.toString());
    private HealthSchedulerService scheduler;

    private final Schedule iudFollowupSchedule = new Schedule(EC_SCHEDULE_IUD_FOLLOWUP,
            asList(EC_SCHEDULE_IUD_FOLLOWUP_MILESTONE_1,
                    EC_SCHEDULE_IUD_FOLLOWUP_MILESTONE_2));

    @Autowired
    public IUDStrategy(HealthSchedulerService scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public void registerEC(FPProductInformation fpInfo) {
        enrollECToIUDSchedule(fpInfo.entityId(), fpInfo.fpMethodChangeDate());
    }

    @Override
    public void unEnrollFromPreviousScheduleAsFPMethodChanged(FPProductInformation fpInfo) {
        logger.info(format("Un-enrolling EC from IUD Followup schedule as FP method changed. entityId: {0}, new fp method: {1}",
                fpInfo.entityId(), fpInfo.currentFPMethod()));
        unEnrollECFromIUDSchedule(fpInfo.entityId(), fpInfo.anmId(), fpInfo.fpMethodChangeDate());
    }

    @Override
    public void enrollToNewScheduleForNewFPMethod(FPProductInformation fpInfo) {
        enrollECToIUDSchedule(fpInfo.entityId(), fpInfo.fpMethodChangeDate());
    }

    @Override
    public void renewFPProduct(FPProductInformation fpInfo) {
    }

    @Override
    public void fpFollowup(FPProductInformation fpInfo) {
        String currentMilestone = getCurrentMilestone(fpInfo);
        logger.info(format("Fulfilling current milestone For IUD Followup schedule. entityId: {0}, Ref date: {1}, currentMilestone: {2}",
                fpInfo.entityId(), fpInfo.submissionDate(), currentMilestone));
        scheduler.fullfillMilestoneAndCloseAlert(fpInfo.entityId(), fpInfo.anmId(), iudFollowupSchedule.name(), currentMilestone, parse(fpInfo.fpFollowupDate()));
    }

    private void enrollECToIUDSchedule(String entityId, String referenceDate) {
        logger.info(format("Enrolling EC to IUD Followup schedule. entityId: {0}, Ref date: {1}", entityId, referenceDate));
        scheduler.enrollIntoSchedule(entityId, iudFollowupSchedule.name(), referenceDate);
    }

    private void unEnrollECFromIUDSchedule(String entityId, String anmId, String submissionDate) {
        scheduler.unEnrollAndCloseSchedule(entityId, anmId, iudFollowupSchedule.name(), parse(submissionDate));
    }

    private String getCurrentMilestone(FPProductInformation fpInfo) {
        return scheduler.getEnrollment(fpInfo.entityId(), iudFollowupSchedule.name()).getCurrentMilestoneName();
    }
}
