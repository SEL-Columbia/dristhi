package org.opensrp.service.scheduling;

import static java.text.MessageFormat.format;
import static org.opensrp.scheduler.DrishtiScheduleConstants.MotherScheduleConstants.SCHEDULE_AUTO_CLOSE_PNC;

import org.joda.time.LocalDate;
import org.opensrp.scheduler.HealthSchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PNCSchedulesService {
    private static Logger logger = LoggerFactory.getLogger(PNCSchedulesService.class.toString());

    private HealthSchedulerService scheduler;

    @Autowired
    public PNCSchedulesService(HealthSchedulerService scheduler) {
        this.scheduler = scheduler;
    }

    public void deliveryOutcome(String entityId, String date) {
        logger.info(format("Enrolling mother into Auto Close PNC schedule. Id: ", entityId));

        scheduler.enrollIntoSchedule(entityId, SCHEDULE_AUTO_CLOSE_PNC, date);
    }

    private boolean fulfillMilestoneIfPossible(String entityId, String anmId, String scheduleName, String milestone, LocalDate fulfillmentDate) {
        if (isNotEnrolled(entityId, scheduleName)) {
            logger.warn(format("Tried to fulfill milestone {0} of {1} for entity id: {2}", milestone, scheduleName, entityId));
            return false;
        }

        logger.warn(format("Fulfilling milestone {0} of {1} for entity id: {2}", milestone, scheduleName, entityId));
        scheduler.fullfillMilestoneAndCloseAlert(entityId, anmId, scheduleName, milestone, fulfillmentDate);
        return true;
    }

    private boolean isNotEnrolled(String caseId, String scheduleName) {
        return scheduler.isNotEnrolled(caseId, scheduleName);
    }

    public void unEnrollFromSchedules(String entityId) {
    	scheduler.unEnrollFromAllSchedules(entityId);
    }

    public void fulfillPNCAutoCloseMilestone(String entityId, String anmIdentifier) {
        fulfillMilestoneIfPossible(entityId, anmIdentifier,
                SCHEDULE_AUTO_CLOSE_PNC, SCHEDULE_AUTO_CLOSE_PNC, new LocalDate());
    }
}
