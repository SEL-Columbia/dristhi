package org.opensrp.register.service.scheduling;

import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentsQuery;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.text.MessageFormat.format;
import static java.util.Arrays.asList;
import static org.opensrp.scheduler.DrishtiScheduleConstants.MotherScheduleConstants.SCHEDULE_AUTO_CLOSE_PNC;
import static org.joda.time.LocalTime.now;
import static org.motechproject.scheduletracking.api.domain.EnrollmentStatus.ACTIVE;

import org.opensrp.register.service.ActionService;
import org.opensrp.service.scheduling.ScheduleService;

@Service
public class PNCSchedulesService {
    private static Logger logger = LoggerFactory.getLogger(PNCSchedulesService.class.toString());

    private final ScheduleTrackingService trackingService;
    private final ScheduleService scheduleService;
    private ActionService actionService;

    @Autowired
    public PNCSchedulesService(ScheduleTrackingService trackingService, ScheduleService scheduleService, ActionService actionService) {
        this.trackingService = trackingService;
        this.scheduleService = scheduleService;
        this.actionService = actionService;
    }

    public void deliveryOutcome(String entityId, String date) {
        logger.info(format("Enrolling mother into Auto Close PNC schedule. Id: ", entityId));

        scheduleService.enroll(entityId,
                SCHEDULE_AUTO_CLOSE_PNC, date);
    }

    private boolean fulfillMilestoneIfPossible(String entityId, String anmId, String scheduleName, String milestone, LocalDate fulfillmentDate) {
        if (isNotEnrolled(entityId, scheduleName)) {
            logger.warn(format("Tried to fulfill milestone {0} of {1} for entity id: {2}", milestone, scheduleName, entityId));
            return false;
        }

        logger.warn(format("Fulfilling milestone {0} of {1} for entity id: {2}", milestone, scheduleName, entityId));
        trackingService.fulfillCurrentMilestone(entityId, scheduleName, fulfillmentDate, new Time(now()));
        actionService.markAlertAsClosed(entityId, anmId, milestone, fulfillmentDate.toString());
        return true;
    }

    private boolean isNotEnrolled(String caseId, String scheduleName) {
        return trackingService.getEnrollment(caseId, scheduleName) == null;
    }

    public void unEnrollFromSchedules(String entityId) {
        List<EnrollmentRecord> openEnrollments = trackingService.search(new EnrollmentsQuery().havingExternalId(entityId).havingState(ACTIVE));

        for (EnrollmentRecord enrollment : openEnrollments) {
            logger.info(format("Un-enrolling PNC with Entity id:{0} from schedule: {1}.", entityId, enrollment.getScheduleName()));

            trackingService.unenroll(entityId, asList(enrollment.getScheduleName()));
        }
    }

    public void fulfillPNCAutoCloseMilestone(String entityId, String anmIdentifier) {
        fulfillMilestoneIfPossible(entityId, anmIdentifier,
                SCHEDULE_AUTO_CLOSE_PNC, SCHEDULE_AUTO_CLOSE_PNC, new LocalDate());
    }
}
