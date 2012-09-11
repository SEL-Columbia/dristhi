package org.ei.drishti.service;

import org.ei.drishti.contract.AnteNatalCareInformation;
import org.ei.drishti.dto.BeneficiaryType;
import org.ei.drishti.scheduler.util.DateUtil;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.Weeks;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.EnrollmentsQuery;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static org.ei.drishti.dto.AlertPriority.normal;
import static org.ei.drishti.scheduler.DrishtiSchedules.*;
import static org.joda.time.LocalTime.now;
import static org.motechproject.scheduletracking.api.domain.EnrollmentStatus.ACTIVE;
import static org.motechproject.util.DateUtil.today;

@Service
public class ANCSchedulesService {
    private final ScheduleTrackingService trackingService;
    private static final String[] NON_ANC_SCHEDULES = {SCHEDULE_EDD, SCHEDULE_IFA, SCHEDULE_LAB, SCHEDULE_TT};
    private ActionService actionService;

    @Autowired
    public ANCSchedulesService(ScheduleTrackingService trackingService, ActionService actionService) {
        this.trackingService = trackingService;
        this.actionService = actionService;
    }

    public void enrollMother(String caseId, LocalDate referenceDateForSchedule, Time referenceTime, Time preferredAlertTime) {
        for (String schedule : NON_ANC_SCHEDULES) {
            trackingService.enroll(new EnrollmentRequest(caseId, schedule, preferredAlertTime, referenceDateForSchedule, referenceTime, null, null, null, null));
        }
        enrollIntoCorrectMilestoneOfANCCare(caseId, referenceDateForSchedule, preferredAlertTime, referenceTime);
    }

    public void ancVisitHasHappened(AnteNatalCareInformation ancInformation) {
        fastForwardSchedule(ancInformation, ancInformation.visitNumber(), SCHEDULE_ANC, "ANC");
    }

    public void ttVisitHasHappened(String caseId, int visitNumber, LocalDate visitDate) {
        final AnteNatalCareInformation ancInformation = new AnteNatalCareInformation(caseId, "ANM 1", visitNumber, visitDate.toString());
        fastForwardSchedule(ancInformation, ancInformation.visitNumber(), SCHEDULE_TT, "TT");
    }

    public void ifaVisitHasHappened(AnteNatalCareInformation ancInformation) {
        fulfillCurrentMilestone(ancInformation, SCHEDULE_IFA, "IFA");
    }

    public void forceFulfillMilestone(String externalId, String scheduleName) {
        trackingService.fulfillCurrentMilestone(externalId, scheduleName, today(), new Time(now()));
    }

    public void closeCase(String caseId) {
        List<EnrollmentRecord> openEnrollments = trackingService.search(new EnrollmentsQuery().havingExternalId(caseId).havingState(ACTIVE));

        for (EnrollmentRecord enrollment : openEnrollments) {
            trackingService.unenroll(caseId, Arrays.asList(enrollment.getScheduleName()));
        }
        actionService.deleteAllAlertsForMother(caseId);
    }

    private void enrollIntoCorrectMilestoneOfANCCare(String caseId, LocalDate referenceDateForSchedule, Time preferredAlertTime, Time referenceTime) {
        String milestone = "ANC 1";

        if (DateUtil.isDateWithinGivenPeriodBeforeToday(referenceDateForSchedule, Weeks.weeks(16).toPeriod().minusDays(1))) {
            milestone = "ANC 1";
        } else if (DateUtil.isDateWithinGivenPeriodBeforeToday(referenceDateForSchedule, Weeks.weeks(28).toPeriod().minusDays(1))) {
            milestone = "ANC 2";
        } else if (DateUtil.isDateWithinGivenPeriodBeforeToday(referenceDateForSchedule, Weeks.weeks(34).toPeriod().minusDays(1))) {
            milestone = "ANC 3";
        } else if (DateUtil.isDateWithinGivenPeriodBeforeToday(referenceDateForSchedule, Weeks.weeks(40).toPeriod())) {
            milestone = "ANC 4";
        }

        trackingService.enroll(new EnrollmentRequest(caseId, SCHEDULE_ANC, preferredAlertTime, referenceDateForSchedule, referenceTime, null, null, milestone, null));
        actionService.alertForBeneficiary(BeneficiaryType.mother, caseId, milestone, normal, referenceDateForSchedule.toDateTime(new LocalTime(14, 0)), referenceDateForSchedule.plusWeeks(12).toDateTime(new LocalTime(14, 0)));
    }

    private void fulfillCurrentMilestone(AnteNatalCareInformation ancInformation, String scheduleName, String milestonePrefix) {
        int expectedMilestoneNumber = currentMilestoneNumber(ancInformation.caseId(), SCHEDULE_IFA, "IFA");
        if (expectedMilestoneNumber == 0) {
            return;
        }
        fastForwardSchedule(ancInformation, expectedMilestoneNumber, scheduleName, milestonePrefix);
    }

    private void fastForwardSchedule(AnteNatalCareInformation ancInformation, int visitNumberToFulfill, String scheduleName, String milestonePrefix) {
        String caseId = ancInformation.caseId();
        int currentMilestoneNumber = currentMilestoneNumber(caseId, scheduleName, milestonePrefix);

        for (int i = currentMilestoneNumber; i <= visitNumberToFulfill; i++) {
            trackingService.fulfillCurrentMilestone(caseId, scheduleName, ancInformation.visitDate(), new Time(now()));
            actionService.markAlertAsClosedForVisitForMother(caseId, ancInformation.anmIdentifier(), milestonePrefix + " " + i, ancInformation.visitDate().toString());
        }
    }

    private int currentMilestoneNumber(String caseId, String scheduleName, String milestonePrefix) {
        EnrollmentRecord record = trackingService.getEnrollment(caseId, scheduleName);
        if (record == null) {
            return 0;
        }

        return Integer.valueOf(record.getCurrentMilestoneName().replace(milestonePrefix + " ", ""));
    }
}
