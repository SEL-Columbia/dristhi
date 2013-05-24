package org.ei.drishti.service;

import org.ei.drishti.common.util.DateUtil;
import org.ei.drishti.contract.AnteNatalCareInformation;
import org.ei.drishti.dto.BeneficiaryType;
import org.joda.time.LocalDate;
import org.joda.time.Weeks;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.EnrollmentsQuery;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.text.MessageFormat.format;
import static java.util.Arrays.asList;
import static org.ei.drishti.common.AllConstants.ANCFormFields.*;
import static org.ei.drishti.common.util.DateUtil.today;
import static org.ei.drishti.dto.AlertStatus.normal;
import static org.ei.drishti.scheduler.DrishtiScheduleConstants.MotherScheduleConstants.*;
import static org.ei.drishti.scheduler.DrishtiScheduleConstants.PREFERED_TIME_FOR_SCHEDULES;
import static org.joda.time.LocalDate.parse;
import static org.joda.time.LocalTime.now;
import static org.motechproject.scheduletracking.api.domain.EnrollmentStatus.ACTIVE;

@Service
public class ANCSchedulesService {
    private static Logger logger = LoggerFactory.getLogger(ANCSchedulesService.class.toString());

    private final ScheduleTrackingService trackingService;
    private static final String[] NON_ANC_SCHEDULES = {SCHEDULE_EDD, SCHEDULE_IFA, SCHEDULE_LAB, SCHEDULE_TT_1};
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

    public void ancVisitHasHappened(String entityId, String anmId, int visitNumberToFulfill, String visitDate) {
        fastForwardSchedule(entityId, anmId, SCHEDULE_ANC, "ANC", visitNumberToFulfill, parse(visitDate));
    }

    public void ttVisitHasHappened(String entityId, String anmId, String ttDose, String ttDate) {
        if (TT1_DOSE_VALUE.equals(ttDose) || TT_BOOSTER__VALUE.equals(ttDose)) {
            fulfillMilestoneIfPossible(entityId, anmId, "TT 1", "TT 1", parse(ttDate));
            trackingService.enroll(new EnrollmentRequest(entityId, "TT 2", new Time(PREFERED_TIME_FOR_SCHEDULES),
                    parse(ttDate), new Time(now()), null, null, null, null));
        } else if (TT2_DOSE_VALUE.equals(ttDose)) {
            fulfillMilestoneIfPossible(entityId, anmId, "TT 2", "TT 2", parse(ttDate));
        }
    }

    @Deprecated
    public void ancVisitHasHappened(AnteNatalCareInformation ancInformation) {
        fastForwardSchedule(ancInformation, ancInformation.visitNumber(), SCHEDULE_ANC, "ANC");
    }

    @Deprecated
    public void ttVisitHasHappened(AnteNatalCareInformation ancInformation) {
        fastForwardSchedule(ancInformation, ancInformation.visitNumber(), SCHEDULE_TT_1, "TT");
    }

    public void ifaVisitHasHappened(AnteNatalCareInformation ancInformation) {
        fulfillCurrentMilestone(ancInformation, SCHEDULE_IFA, "IFA");
    }

    public void forceFulfillMilestone(String externalId, String scheduleName) {
        trackingService.fulfillCurrentMilestone(externalId, scheduleName, today(), new Time(now()));
    }

    public void unEnrollFromSchedules(String caseId) {
        List<EnrollmentRecord> openEnrollments = trackingService.search(new EnrollmentsQuery().havingExternalId(caseId).havingState(ACTIVE));

        for (EnrollmentRecord enrollment : openEnrollments) {
            trackingService.unenroll(caseId, asList(enrollment.getScheduleName()));
        }
        actionService.markAllAlertsAsInactive(caseId);
    }

    private void enrollIntoCorrectMilestoneOfANCCare(String caseId, LocalDate referenceDateForSchedule, Time preferredAlertTime, Time referenceTime) {
        String milestone;

        if (DateUtil.isDateWithinGivenPeriodBeforeToday(referenceDateForSchedule, Weeks.weeks(16).toPeriod().minusDays(1))) {
            milestone = "ANC 1";
        } else if (DateUtil.isDateWithinGivenPeriodBeforeToday(referenceDateForSchedule, Weeks.weeks(28).toPeriod().minusDays(1))) {
            milestone = "ANC 2";
        } else if (DateUtil.isDateWithinGivenPeriodBeforeToday(referenceDateForSchedule, Weeks.weeks(34).toPeriod().minusDays(1))) {
            milestone = "ANC 3";
        } else if (DateUtil.isDateWithinGivenPeriodBeforeToday(referenceDateForSchedule, Weeks.weeks(40).toPeriod())) {
            milestone = "ANC 4";
        } else {
            logger.warn("Too late to enroll " + caseId + " into ANC schedule. Reference date is: " + referenceDateForSchedule);
            return;
        }

        trackingService.enroll(new EnrollmentRequest(caseId, SCHEDULE_ANC, preferredAlertTime, referenceDateForSchedule, referenceTime, null, null, milestone, null));
        actionService.alertForBeneficiary(BeneficiaryType.mother, caseId, SCHEDULE_ANC, milestone, normal, referenceDateForSchedule.toDateTime(PREFERED_TIME_FOR_SCHEDULES), referenceDateForSchedule.plusWeeks(12).toDateTime(PREFERED_TIME_FOR_SCHEDULES));
    }

    private void fulfillCurrentMilestone(AnteNatalCareInformation ancInformation, String scheduleName, String milestonePrefix) {
        int expectedMilestoneNumber = currentMilestoneNumber(ancInformation.caseId(), scheduleName, milestonePrefix);
        fulfillMilestoneIfPossible(ancInformation, scheduleName, milestonePrefix, expectedMilestoneNumber);
    }

    private void fastForwardSchedule(AnteNatalCareInformation ancInformation, int visitNumberToFulfill, String scheduleName, String milestonePrefix) {
        int currentMilestoneNumber = currentMilestoneNumber(ancInformation.caseId(), scheduleName, milestonePrefix);
        for (int i = currentMilestoneNumber; i <= visitNumberToFulfill; i++) {
            fulfillMilestoneIfPossible(ancInformation, scheduleName, milestonePrefix, i);
        }
    }

    private void fulfillMilestoneIfPossible(AnteNatalCareInformation ancInformation, String scheduleName, String milestonePrefix, int visitNumber) {
        String caseId = ancInformation.caseId();

        if (isNotEnrolled(caseId, scheduleName)) {
            logger.warn(format("Tried to fulfill milestone {0} of {1} {2} for visit: {3}", milestonePrefix, scheduleName, ancInformation.caseId(), ancInformation.visitNumber()));
            return;
        }

        trackingService.fulfillCurrentMilestone(caseId, scheduleName, ancInformation.visitDate(), new Time(now()));
        actionService.markAlertAsClosed(caseId, ancInformation.anmIdentifier(), milestonePrefix + " " + visitNumber, ancInformation.visitDate().toString());
    }

    private void fastForwardSchedule(String entityId, String anmId, String scheduleName, String milestonePrefix, int visitNumberToFulfill, LocalDate visitDate) {
        int currentMilestoneNumber = currentMilestoneNumber(entityId, scheduleName, milestonePrefix);
        for (int i = currentMilestoneNumber; i <= visitNumberToFulfill; i++) {
            fulfillMilestoneIfPossible(entityId, anmId, scheduleName, milestonePrefix + " " + i, visitDate);
        }
    }

    private void fulfillMilestoneIfPossible(String entityId, String anmId, String scheduleName, String milestone, LocalDate fulfillmentDate) {
        if (isNotEnrolled(entityId, scheduleName)) {
            logger.warn(format("Tried to fulfill milestone {0} of {1} for visit: {2}", milestone, scheduleName, entityId));
            return;
        }

        trackingService.fulfillCurrentMilestone(entityId, scheduleName, fulfillmentDate, new Time(now()));
        actionService.markAlertAsClosed(entityId, anmId, milestone, fulfillmentDate.toString());
    }

    private int currentMilestoneNumber(String caseId, String scheduleName, String milestonePrefix) {
        if (isNotEnrolled(caseId, scheduleName)) {
            return Integer.MAX_VALUE;
        }

        EnrollmentRecord record = trackingService.getEnrollment(caseId, scheduleName);
        return Integer.valueOf(record.getCurrentMilestoneName().replace(milestonePrefix + " ", ""));
    }

    private boolean isNotEnrolled(String caseId, String scheduleName) {
        return trackingService.getEnrollment(caseId, scheduleName) == null;
    }
}
