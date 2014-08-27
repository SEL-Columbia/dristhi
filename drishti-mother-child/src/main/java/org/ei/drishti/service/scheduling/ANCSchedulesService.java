package org.ei.drishti.service.scheduling;

import org.ei.drishti.common.AllConstants;
import org.ei.drishti.common.util.DateUtil;
import org.ei.drishti.service.ActionService;
import org.joda.time.LocalDate;
import org.joda.time.Weeks;
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
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.ei.drishti.common.util.DateUtil.today;
import static org.ei.drishti.common.util.IntegerUtil.tryParse;
import static org.ei.drishti.scheduler.DrishtiScheduleConstants.MotherScheduleConstants.*;
import static org.joda.time.LocalDate.parse;
import static org.joda.time.LocalTime.now;
import static org.motechproject.scheduletracking.api.domain.EnrollmentStatus.ACTIVE;

@Service
public class ANCSchedulesService {
    public static final int NUMBER_OF_WEEKS_BEFORE_HB_TEST_2_BECOMES_DUE = 28;
    private static Logger logger = LoggerFactory.getLogger(ANCSchedulesService.class.toString());

    private final ScheduleTrackingService trackingService;
    private static final String[] NON_ANC_SCHEDULES = {SCHEDULE_EDD, SCHEDULE_LAB, SCHEDULE_TT_1, SCHEDULE_IFA_1,
            SCHEDULE_HB_TEST_1, SCHEDULE_DELIVERY_PLAN};
    private ActionService actionService;
    private final ScheduleService scheduleService;

    @Autowired
    public ANCSchedulesService(ScheduleTrackingService trackingService, ActionService actionService, ScheduleService scheduleService) {
        this.trackingService = trackingService;
        this.actionService = actionService;
        this.scheduleService = scheduleService;
    }

    public void enrollMother(String caseId, LocalDate referenceDateForSchedule) {
        for (String schedule : NON_ANC_SCHEDULES) {
            scheduleService.enroll(caseId, schedule, referenceDateForSchedule.toString());
        }
        enrollIntoCorrectMilestoneOfANCCare(caseId, referenceDateForSchedule);
    }

    public void ancVisitHasHappened(String entityId, String anmId, int visitNumberToFulfill, String visitDate) {
        fastForwardSchedule(entityId, anmId, SCHEDULE_ANC, SCHEDULE_ANC_MILESTONE_PREFIX, visitNumberToFulfill, parse(visitDate));
    }

    public void deliveryHasBeenPlanned(String entityId, String anmId, String deliveryPlanDate) {
        fulfillMilestoneIfPossible(entityId, anmId, SCHEDULE_DELIVERY_PLAN, SCHEDULE_DELIVERY_PLAN, parse(deliveryPlanDate));
    }

    public void ttVisitHasHappened(String entityId, String anmId, String ttDose, String ttDate) {
        if (AllConstants.ANCFormFields.TT_BOOSTER_DOSE_VALUE.equals(ttDose)) {
            unEnrollFromSchedule(entityId, anmId, SCHEDULE_TT_1);
        } else if (AllConstants.ANCFormFields.TT1_DOSE_VALUE.equals(ttDose)) {
            fulfillMilestoneIfPossible(entityId, anmId, SCHEDULE_TT_1, SCHEDULE_TT_1, parse(ttDate));
            scheduleService.enroll(entityId, SCHEDULE_TT_2, ttDate);
        } else if (AllConstants.ANCFormFields.TT2_DOSE_VALUE.equals(ttDose)) {
            fulfillMilestoneIfPossible(entityId, anmId, SCHEDULE_TT_2, SCHEDULE_TT_2, parse(ttDate));
        }
    }

    public void ifaTabletsGiven(String entityId, String anmId, String numberOfIFATabletsGiven, String ifaGivenDate) {
        if (tryParse(numberOfIFATabletsGiven, 0) <= 0) {
            logger.info("Number of IFA tablets given is zero so not updating schedules for entity: " + entityId);
            return;
        }
        if (fulfillMilestoneIfPossible(entityId, anmId, SCHEDULE_IFA_1, SCHEDULE_IFA_1, parse(ifaGivenDate))) {
            logger.info("Enrolling ANC to IFA 2 schedule. Entity id: " + entityId);

            scheduleService.enroll(entityId, SCHEDULE_IFA_2, ifaGivenDate);
            return;
        }
        if (fulfillMilestoneIfPossible(entityId, anmId, SCHEDULE_IFA_2, SCHEDULE_IFA_2, parse(ifaGivenDate))) {
            logger.info("Enrolling ANC to IFA 3 schedule. Entity id: " + entityId);

            scheduleService.enroll(entityId, SCHEDULE_IFA_3, ifaGivenDate);
            return;
        }
        fulfillMilestoneIfPossible(entityId, anmId, SCHEDULE_IFA_3, SCHEDULE_IFA_3, parse(ifaGivenDate));
    }

    public void hbTestDone(String entityId, String anmId, String date, String anaemicStatus, LocalDate lmp) {
        if (fulfillMilestoneIfPossible(entityId, anmId, SCHEDULE_HB_TEST_1, SCHEDULE_HB_TEST_1, parse(date))) {
            if (isNotBlank(anaemicStatus)) {
                logger.info(format("ANC is anaemic so enrolling her to Hb Followup Test schedule: Entity id:{0}, Anaemic status: {1}", entityId, anaemicStatus));
                scheduleService.enroll(entityId, SCHEDULE_HB_FOLLOWUP_TEST, date);
            } else {
                enrollANCToHbTest2Schedule(entityId, lmp);
            }
        } else if (fulfillMilestoneIfPossible(entityId, anmId, SCHEDULE_HB_FOLLOWUP_TEST, SCHEDULE_HB_FOLLOWUP_TEST, parse(date))) {
            if (parse(date).isAfter(lmp.plusWeeks(NUMBER_OF_WEEKS_BEFORE_HB_TEST_2_BECOMES_DUE))) {
                fulfillMilestoneIfPossible(entityId, anmId, SCHEDULE_HB_TEST_2, SCHEDULE_HB_TEST_2, parse(date));
            } else {
                enrollANCToHbTest2Schedule(entityId, lmp);
            }
        } else {
            fulfillMilestoneIfPossible(entityId, anmId, SCHEDULE_HB_TEST_2, SCHEDULE_HB_TEST_2, parse(date));
        }
    }

    private void enrollANCToHbTest2Schedule(String entityId, LocalDate lmp) {
        logger.info(format("Enrolling ANC to Hb Test 2 schedule: {0} Entity id:{1}", SCHEDULE_HB_TEST_2, entityId));
        scheduleService.enroll(entityId, SCHEDULE_HB_TEST_2, lmp.toString());
    }

    public void forceFulfillMilestone(String externalId, String scheduleName) {
        trackingService.fulfillCurrentMilestone(externalId, scheduleName, today(), new Time(now()));
    }

    public void unEnrollFromAllSchedules(String entityId) {
        List<EnrollmentRecord> openEnrollments = trackingService.search(new EnrollmentsQuery().havingExternalId(entityId).havingState(ACTIVE));

        for (EnrollmentRecord enrollment : openEnrollments) {
            logger.info(format("Un-enrolling ANC with Entity id:{0} from schedule: {1} as Delivery happened.", entityId, enrollment.getScheduleName()));
            trackingService.unenroll(entityId, asList(enrollment.getScheduleName()));
        }
        actionService.markAllAlertsAsInactive(entityId);
    }

    private void unEnrollFromSchedule(String entityId, String anmId, String scheduleName) {
        logger.info(format("Un-enrolling ANC with Entity id:{0} from schedule: {1}", entityId, scheduleName));
        trackingService.unenroll(entityId, asList(scheduleName));
        actionService.markAlertAsInactive(anmId, entityId, scheduleName);
    }

    private void enrollIntoCorrectMilestoneOfANCCare(String entityId, LocalDate referenceDateForSchedule) {
        String milestone;

        if (DateUtil.isDateWithinGivenPeriodBeforeToday(referenceDateForSchedule, Weeks.weeks(14).toPeriod().minusDays(1))) {
            milestone = SCHEDULE_ANC_1;
        } else if (DateUtil.isDateWithinGivenPeriodBeforeToday(referenceDateForSchedule, Weeks.weeks(28).toPeriod().minusDays(1))) {
            milestone = SCHEDULE_ANC_2;
        } else if (DateUtil.isDateWithinGivenPeriodBeforeToday(referenceDateForSchedule, Weeks.weeks(36).toPeriod().minusDays(1))) {
            milestone = SCHEDULE_ANC_3;
        } else {
            milestone = SCHEDULE_ANC_4;
        }

        logger.info(format("Enrolling ANC with Entity id:{0} to ANC schedule, milestone: {1}.", entityId, milestone));
        scheduleService.enroll(entityId, SCHEDULE_ANC, milestone, referenceDateForSchedule.toString());
    }

    private void fastForwardSchedule(String entityId, String anmId, String scheduleName, String milestonePrefix, int visitNumberToFulfill, LocalDate visitDate) {
        int currentMilestoneNumber = currentMilestoneNumber(entityId, scheduleName, milestonePrefix);
        for (int i = currentMilestoneNumber; i <= visitNumberToFulfill; i++) {
            fulfillMilestoneIfPossible(entityId, anmId, scheduleName, milestonePrefix + " " + i, visitDate);
        }
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
