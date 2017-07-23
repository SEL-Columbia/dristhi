package org.opensrp.scheduler;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.opensrp.dto.AlertStatus;
import org.opensrp.dto.BeneficiaryType;
import org.opensrp.scheduler.service.ActionService;
import org.opensrp.scheduler.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.opensrp.dto.AlertStatus.*;

@Component
public class HealthSchedulerService {
    public enum MetadataField {
        enrollmentEvent,
        fulfillmentEvent,
        unenrollmentEvent,
        lastUpdate
    }

    private final ActionService actionService;
    private final ScheduleService scheduleService;
    private final ScheduleConfig scheduleConfig;

    @Autowired
    public HealthSchedulerService(ActionService actionService, ScheduleService scheduleService, ScheduleConfig scheduleConfig) {
        this.actionService = actionService;
        this.scheduleService = scheduleService;
        this.scheduleConfig = scheduleConfig;
    }

    public List<Schedule> findAutomatedSchedules(String formSubmission) {
        List<Schedule> schl = new ArrayList<>();
        for (Schedule schedule : scheduleConfig.getSchedules()) {
            if (schedule.hasForm(formSubmission)) {
                schl.add(schedule);
            }
        }
        return schl;
    }

    public void enrollIntoSchedule(String entityId, String schedule, LocalDate referenceDate, String formSubmissionId) {
        scheduleService.enroll(entityId, schedule, referenceDate.toString(), formSubmissionId);
    }

    public void enrollIntoSchedule(String entityId, String schedule, String referenceDate, String formSubmissionId) {
        scheduleService.enroll(entityId, schedule, referenceDate, formSubmissionId);
    }

    public void enrollIntoSchedule(String entityId, String schedule, String milestone, String referenceDate, String formSubmissionId) {
        scheduleService.enroll(entityId, schedule, milestone, referenceDate, formSubmissionId);
    }

    public void fullfillMilestoneAndCloseAlert(String entityId, String providerId, String scheduleName, String milestone, LocalDate completionDate, String formSubmissionId) {
        scheduleService.fulfillMilestone(entityId, scheduleName, completionDate, formSubmissionId);
        actionService.markAlertAsClosed(entityId, providerId, milestone, completionDate.toString());
    }

    public void fullfillMilestoneAndCloseAlert(String entityId, String providerId, String scheduleName, LocalDate completionDate, String formSubmissionId) {
        scheduleService.fulfillMilestone(entityId, scheduleName, completionDate, formSubmissionId);
        actionService.markAlertAsClosed(entityId, providerId, scheduleName, completionDate.toString());
    }

    public void forceFullfillMilestonesAndCloseAlert(String entityId, String scheduleName, LocalDate completionDate) {
        scheduleService.fulfillMilestone(entityId, scheduleName, completionDate, "");
        actionService.markAlertAsClosed(entityId, scheduleName, completionDate.toString());
    }

    public void unEnrollFromSchedule(String entityId, String providerId, String scheduleName, String formSubmissionId) {
        scheduleService.unenroll(entityId, scheduleName, formSubmissionId);
        actionService.markAlertAsInactive(providerId, entityId, scheduleName);
    }

    public void unEnrollAndCloseSchedule(String entityId, String providerId, String scheduleName, LocalDate completionDate, String formSubmissionId) {
        scheduleService.unenroll(entityId, scheduleName, formSubmissionId);
        actionService.markAlertAsClosed(providerId, entityId, scheduleName, completionDate.toString());
    }

    public void unEnrollFromAllSchedules(String entityId, String formSubmissionId) {
        List<String> activeSchedules = scheduleService.findOpenEnrollmentNames(entityId);

        scheduleService.unenroll(entityId, activeSchedules, formSubmissionId);
        actionService.markAllAlertsAsInactive(entityId);
    }

    public void closeBeneficiary(BeneficiaryType beneficiary, String caseId, String anmIdentifier, String reasonForClose) {
        actionService.closeBeneficiary(beneficiary, caseId, anmIdentifier, reasonForClose);
    }

    public List<EnrollmentRecord> findActiveEnrollments(String entityId) {
        return scheduleService.findOpenEnrollments(entityId);
    }

    public List<String> findActiveSchedules(String entityId) {
        return scheduleService.findOpenEnrollmentNames(entityId);
    }

    public boolean isNotEnrolled(String entityId, String scheduleName) {
        return scheduleService.getEnrollment(entityId, scheduleName) == null;
    }

    public Enrollment getActiveEnrollment(String entityId, String scheduleName) {
        return scheduleService.getActiveEnrollment(entityId, scheduleName);
    }

    public Enrollment getEnrollment(String entityId, String scheduleName) {
        return scheduleService.getEnrollment(entityId, scheduleName);
    }

    public EnrollmentRecord getEnrollmentRecord(String entityId, String scheduleName) {
        return scheduleService.getEnrollmentRecord(entityId, scheduleName);
    }

    public void alertFor(String windowName, String beneficiaryType, String entityId,
                         String providerId, String schedule, String milestone,
                         DateTime startOfDueWindow, DateTime startOfLateWindow, DateTime startOfMaxWindow) {
        if (WindowName.late.toString().equals(windowName)) {
            actionService.alertForBeneficiary(beneficiaryType, entityId, providerId, schedule, milestone, urgent, startOfLateWindow, startOfMaxWindow);
        } else if (WindowName.earliest.toString().equals(windowName)) {
            actionService.alertForBeneficiary(beneficiaryType, entityId, providerId, schedule, milestone, upcoming, startOfDueWindow, startOfLateWindow);
        } else if (WindowName.due.toString().equals(windowName)) {
            actionService.alertForBeneficiary(beneficiaryType, entityId, providerId, schedule, milestone, normal, startOfDueWindow, startOfLateWindow);
        } else if (WindowName.max.toString().equals(windowName)) {
            actionService.alertForBeneficiary(beneficiaryType, entityId, providerId, schedule, milestone, expired, startOfMaxWindow, startOfMaxWindow);
        }
    }

    public void alertFor(String beneficiaryType, String entityId, String providerId, String schedule,
                         String milestone, AlertStatus alertStatus, DateTime startOfDueWindow, DateTime startDate, DateTime expiryDate) {
        actionService.alertForBeneficiary(beneficiaryType, entityId, providerId, schedule, milestone, alertStatus,
                startDate, expiryDate);
    }
}
