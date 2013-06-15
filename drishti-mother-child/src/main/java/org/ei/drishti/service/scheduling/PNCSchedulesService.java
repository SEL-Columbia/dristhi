package org.ei.drishti.service.scheduling;

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
import static org.ei.drishti.scheduler.DrishtiScheduleConstants.MotherScheduleConstants.SCHEDULE_AUTO_CLOSE_PNC;
import static org.motechproject.scheduletracking.api.domain.EnrollmentStatus.ACTIVE;

@Service
public class PNCSchedulesService {
    private static Logger logger = LoggerFactory.getLogger(PNCSchedulesService.class.toString());

    private final ScheduleTrackingService trackingService;
    private final ScheduleService scheduleService;

    @Autowired
    public PNCSchedulesService(ScheduleTrackingService trackingService, ScheduleService scheduleService) {
        this.trackingService = trackingService;
        this.scheduleService = scheduleService;
    }

    public void deliveryOutcome(String entityId, String date) {
        logger.info(format("Enrolling mother into Auto Close PNC schedule. Id: ", entityId));

        scheduleService.enroll(entityId,
                SCHEDULE_AUTO_CLOSE_PNC, date);
    }

    public void unEnrollFromSchedules(String entityId) {
        List<EnrollmentRecord> openEnrollments = trackingService.search(new EnrollmentsQuery().havingExternalId(entityId).havingState(ACTIVE));

        for (EnrollmentRecord enrollment : openEnrollments) {
            logger.info(format("Un-enrolling PNC with Entity id:{0} from schedule: {1}.", entityId, enrollment.getScheduleName()));

            trackingService.unenroll(entityId, asList(enrollment.getScheduleName()));
        }
    }
}
