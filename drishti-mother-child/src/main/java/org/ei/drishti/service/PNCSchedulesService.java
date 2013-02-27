package org.ei.drishti.service;

import org.ei.drishti.contract.AnteNatalCareOutcomeInformation;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.ei.drishti.scheduler.DrishtiScheduleConstants.MotherScheduleConstants.SCHEDULE_AUTO_CLOSE_PNC;
import static org.ei.drishti.scheduler.DrishtiScheduleConstants.PREFERED_TIME_FOR_SCHEDULES;
import static org.joda.time.LocalDate.parse;

@Service
public class PNCSchedulesService {
    private static Logger logger = LoggerFactory.getLogger(PNCSchedulesService.class.toString());

    private final ScheduleTrackingService trackingService;
    private ActionService actionService;

    @Autowired
    public PNCSchedulesService(ScheduleTrackingService trackingService, ActionService actionService) {
        this.trackingService = trackingService;
        this.actionService = actionService;
    }

    public void enrollMother(AnteNatalCareOutcomeInformation outcomeInformation) {
        trackingService.enroll(
                new EnrollmentRequest(outcomeInformation.motherCaseId(),
                        SCHEDULE_AUTO_CLOSE_PNC,
                        new Time(PREFERED_TIME_FOR_SCHEDULES),
                        parse(outcomeInformation.deliveryOutcomeDate()),
                        null, null, null, null, null));
    }
}
