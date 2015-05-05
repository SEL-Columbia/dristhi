package org.opensrp.register.service.scheduling;

import static java.text.MessageFormat.format;
import static java.util.Arrays.asList;
import static org.joda.time.LocalDate.parse;
import static org.opensrp.common.AllConstants.Form.BOOLEAN_TRUE_VALUE;
import static org.opensrp.register.DrishtiScheduleConstants.ECSchedulesConstants.EC_SCHEDULE_FP_FOLLOWUP;
import static org.opensrp.register.DrishtiScheduleConstants.ECSchedulesConstants.EC_SCHEDULE_FP_FOLLOWUP_MILESTONE;
import static org.opensrp.register.DrishtiScheduleConstants.ECSchedulesConstants.EC_SCHEDULE_FP_REFERRAL_FOLLOWUP;
import static org.opensrp.register.DrishtiScheduleConstants.ECSchedulesConstants.EC_SCHEDULE_FP_REFERRAL_FOLLOWUP_MILESTONE;

import org.opensrp.domain.FPProductInformation;
import org.opensrp.register.service.scheduling.fpMethodStrategy.FPMethodStrategyFactory;
import org.opensrp.scheduler.HealthSchedulerService;
import org.opensrp.scheduler.Schedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ECSchedulingService {
    private static Logger logger = LoggerFactory.getLogger(ECSchedulingService.class.toString());

    private FPMethodStrategyFactory fpMethodStrategyFactory;
    private final HealthSchedulerService scheduler;
    private final Schedule fpFollowupSchedule = new Schedule(EC_SCHEDULE_FP_FOLLOWUP, asList(EC_SCHEDULE_FP_FOLLOWUP_MILESTONE));
    private final Schedule fpReferralFollowupSchedule = new Schedule(EC_SCHEDULE_FP_REFERRAL_FOLLOWUP, asList(EC_SCHEDULE_FP_REFERRAL_FOLLOWUP_MILESTONE));

    @Autowired
    public ECSchedulingService(FPMethodStrategyFactory fpMethodStrategyFactory, HealthSchedulerService scheduler) {
        this.fpMethodStrategyFactory = fpMethodStrategyFactory;
        this.scheduler = scheduler;
    }

    public void registerEC(FPProductInformation fpInfo) {
        fpMethodStrategyFactory.getStrategyFor(fpInfo.currentFPMethod()).registerEC(fpInfo);
    }

    public void fpChange(FPProductInformation fpInfo) {
        fpMethodStrategyFactory.getStrategyFor(fpInfo.previousFPMethod()).unEnrollFromPreviousScheduleAsFPMethodChanged(fpInfo);

        fpMethodStrategyFactory.getStrategyFor(fpInfo.currentFPMethod()).enrollToNewScheduleForNewFPMethod(fpInfo);
    }

    public void renewFPProduct(FPProductInformation fpInfo) {
        fpMethodStrategyFactory.getStrategyFor(fpInfo.currentFPMethod()).renewFPProduct(fpInfo);
    }

    public void fpFollowup(FPProductInformation fpInfo) {
        fpMethodStrategyFactory.getStrategyFor(fpInfo.currentFPMethod()).fpFollowup(fpInfo);
        updateFPFollowupSchedule(fpInfo);
        updateFPReferralFollowupSchedule(fpInfo);
    }

    public void reportFPComplications(FPProductInformation fpInfo) {
        updateFPFollowupSchedule(fpInfo);
        updateFPReferralFollowupSchedule(fpInfo);
    }

    public void reportReferralFollowup(FPProductInformation fpInfo) {
        updateFPFollowupSchedule(fpInfo);
        updateFPReferralFollowupSchedule(fpInfo);
    }

    private void updateFPFollowupSchedule(FPProductInformation fpInfo) {
        if (BOOLEAN_TRUE_VALUE.equalsIgnoreCase(fpInfo.needsFollowup())) {
            logger.info(format("Enrolling EC to FP Followup schedule. entityId: {0}, Ref date: {1}", fpInfo.entityId(), fpInfo.fpFollowupDate()));
            scheduler.enrollIntoSchedule(fpInfo.entityId(), fpFollowupSchedule.name(), fpInfo.fpFollowupDate());
        } else {
            if(scheduler.isNotEnrolled(fpInfo.entityId(), fpFollowupSchedule.name())) {
                return;
            }

            logger.info(format("Fulfilling FP Followup schedule. entityId: {0}, completionDate: {1}", fpInfo.entityId(), fpInfo.submissionDate()));
            scheduler.fullfillMilestoneAndCloseAlert(fpInfo.entityId(), fpInfo.anmId(), fpFollowupSchedule.name(), parse(fpInfo.submissionDate()));
        }
    }

    private void updateFPReferralFollowupSchedule(FPProductInformation fpInfo) {
        if (BOOLEAN_TRUE_VALUE.equalsIgnoreCase(fpInfo.needsReferralFollowup())) {
            logger.info(format("Enrolling EC to FP Referral Followup schedule. entityId: {0}, Ref date: {1}", fpInfo.entityId(), fpInfo.fpFollowupDate()));
            scheduler.enrollIntoSchedule(fpInfo.entityId(), fpReferralFollowupSchedule.name(), fpInfo.fpFollowupDate());
        } else {
            if(scheduler.isNotEnrolled(fpInfo.entityId(), fpReferralFollowupSchedule.name())){
                return;
            }

            logger.info(format("Fulfilling FP Referral Followup schedule. entityId: {0}, completionDate: {1}", fpInfo.entityId(), fpInfo.submissionDate()));
            scheduler.fullfillMilestoneAndCloseAlert(fpInfo.entityId(), fpInfo.anmId(), fpReferralFollowupSchedule.name(), parse(fpInfo.submissionDate()));
        }
    }
}
