package org.ei.drishti.service;

import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.EnrollmentsQuery;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static org.motechproject.scheduletracking.api.domain.EnrollmentStatus.ACTIVE;

@Service
public class ANCSchedulesService {
    private final ScheduleTrackingService trackingService;
    public static final String SCHEDULE_ANC = "Ante Natal Care - Normal";
    public static final String SCHEDULE_EDD = "Expected Date Of Delivery";
    public static final String SCHEDULE_IFA = "Iron Folic Acid Supplements";
    public static final String SCHEDULE_LAB = "Lab Reminders";
    public static final String SCHEDULE_TT = "Tetatnus Toxoid Vaccination";

    private static final String[] ALL_SCHEDULES = {SCHEDULE_ANC, SCHEDULE_EDD, SCHEDULE_IFA, SCHEDULE_LAB, SCHEDULE_TT};

    public ANCSchedulesService(ScheduleTrackingService trackingService) {
        this.trackingService = trackingService;
    }

    public void enrollMother(String caseId, LocalDate referenceDateForSchedule, Time preferredAlertTime) {
        for (String schedule : ALL_SCHEDULES) {
            trackingService.enroll(new EnrollmentRequest(caseId, schedule, preferredAlertTime, referenceDateForSchedule, null, null, null, null));
        }
    }

    public void ancVisitHasHappened(String caseId, LocalDate visitDate) {
        trackingService.fulfillCurrentMilestone(caseId, SCHEDULE_ANC, visitDate);
    }

    public void closeCase(String caseId) {
        List<EnrollmentRecord> openEnrollments = trackingService.search(new EnrollmentsQuery().havingExternalId(caseId).havingState(ACTIVE.toString()));

        for (EnrollmentRecord enrollment : openEnrollments) {
            trackingService.unenroll(caseId, Arrays.asList(enrollment.getScheduleName()));
        }
    }

}
