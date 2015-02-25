package org.opensrp.service.scheduling.fpMethodStrategy;

import org.opensrp.domain.FPProductInformation;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.opensrp.contract.Schedule;
import org.opensrp.service.ActionService;
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
public class IUDStrategy implements FPMethodStrategy {
    private static Logger logger = LoggerFactory.getLogger(IUDStrategy.class.toString());
    private final ScheduleTrackingService scheduleTrackingService;
    private final ActionService actionService;
    private final ScheduleService scheduleService;
    private final Schedule iudFollowupSchedule = new Schedule(EC_SCHEDULE_IUD_FOLLOWUP,
            asList(EC_SCHEDULE_IUD_FOLLOWUP_MILESTONE_1,
                    EC_SCHEDULE_IUD_FOLLOWUP_MILESTONE_2));

    @Autowired
    public IUDStrategy(ScheduleTrackingService scheduleTrackingService, ActionService actionService, ScheduleService scheduleService) {
        this.scheduleTrackingService = scheduleTrackingService;
        this.actionService = actionService;
        this.scheduleService = scheduleService;
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
        scheduleTrackingService.fulfillCurrentMilestone(fpInfo.entityId(), iudFollowupSchedule.name(), parse(fpInfo.fpFollowupDate()));
        actionService.markAlertAsClosed(fpInfo.entityId(), fpInfo.anmId(), currentMilestone, fpInfo.fpFollowupDate());
    }

    private void enrollECToIUDSchedule(String entityId, String referenceDate) {
        logger.info(format("Enrolling EC to IUD Followup schedule. entityId: {0}, Ref date: {1}", entityId, referenceDate));
        scheduleService.enroll(entityId, iudFollowupSchedule.name(), referenceDate);
    }

    private void unEnrollECFromIUDSchedule(String entityId, String anmId, String submissionDate) {
        scheduleTrackingService.unenroll(entityId, asList(iudFollowupSchedule.name()));
        actionService.markAlertAsClosed(entityId, anmId, iudFollowupSchedule.name(), submissionDate);
    }

    private String getCurrentMilestone(FPProductInformation fpInfo) {
        return scheduleTrackingService.getEnrollment(fpInfo.entityId(), iudFollowupSchedule.name()).getCurrentMilestoneName();
    }
}
