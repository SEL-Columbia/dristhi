package org.ei.drishti.service;

import org.ei.drishti.contract.AnteNatalCareOutcomeInformation;
import org.ei.drishti.contract.ChildImmunizationUpdationRequest;
import org.joda.time.LocalDate;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.EnrollmentsQuery;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang.StringUtils.join;
import static org.ei.drishti.scheduler.DrishtiSchedules.*;
import static org.motechproject.scheduletracking.api.domain.EnrollmentStatus.ACTIVE;

@Service
public class PNCSchedulesService {
    private final ScheduleTrackingService scheduleTrackingService;
    private static final String[] ALL_PNC_SCHEDULES = {CHILD_SCHEDULE_BCG, CHILD_SCHEDULE_DPT, CHILD_SCHEDULE_HEPATITIS,
            CHILD_SCHEDULE_MEASLES, CHILD_SCHEDULE_OPV};

    @Autowired
    public PNCSchedulesService(ScheduleTrackingService scheduleTrackingService) {
        this.scheduleTrackingService = scheduleTrackingService;
    }

    public void enrollChild(AnteNatalCareOutcomeInformation request) {
        for (String schedule : ALL_PNC_SCHEDULES) {
            scheduleTrackingService.enroll(new EnrollmentRequest(request.caseId(), schedule, null, request.dateOfBirth(), null, null, null, null, null));
        }
        updateEnrollments(new ChildImmunizationUpdationRequest(request.caseId(), request.anmIdentifier(), join(request.immunizationsProvided(), " "), LocalDate.now().toString()));
    }

    public void updateEnrollments(ChildImmunizationUpdationRequest request) {
        fulfillIfImmunizationIsProvided(request, "bcg", CHILD_SCHEDULE_BCG, "REMINDER");

        fulfillIfImmunizationIsProvided(request, "dpt_0", CHILD_SCHEDULE_DPT, "DPT 0");
        fulfillIfImmunizationIsProvided(request, "dpt_1", CHILD_SCHEDULE_DPT, "DPT 1");
        fulfillIfImmunizationIsProvided(request, "dpt_2", CHILD_SCHEDULE_DPT, "DPT 2");
        fulfillIfImmunizationIsProvided(request, "dpt_3", CHILD_SCHEDULE_DPT, "DPT 3");

        fulfillIfImmunizationIsProvided(request, "hepb_1", CHILD_SCHEDULE_HEPATITIS, "Hepatitis B1");
        fulfillIfImmunizationIsProvided(request, "hepb_2", CHILD_SCHEDULE_HEPATITIS, "Hepatitis B2");
        fulfillIfImmunizationIsProvided(request, "hepb_3", CHILD_SCHEDULE_HEPATITIS, "Hepatitis B3");
        fulfillIfImmunizationIsProvided(request, "hepb_4", CHILD_SCHEDULE_HEPATITIS, "Hepatitis B4");

        fulfillIfImmunizationIsProvided(request, "measles", CHILD_SCHEDULE_MEASLES, "REMINDER");

        fulfillIfImmunizationIsProvided(request, "opv_0", CHILD_SCHEDULE_OPV, "OPV 0");
        fulfillIfImmunizationIsProvided(request, "opv_1", CHILD_SCHEDULE_OPV, "OPV 1");
        fulfillIfImmunizationIsProvided(request, "opv_2", CHILD_SCHEDULE_OPV, "OPV 2");
        fulfillIfImmunizationIsProvided(request, "opv_3", CHILD_SCHEDULE_OPV, "OPV 3");
    }

    public void unenrollChild(String caseId) {
        List<EnrollmentRecord> openEnrollments = scheduleTrackingService.search(new EnrollmentsQuery().havingExternalId(caseId).havingState(ACTIVE));

        for (EnrollmentRecord enrollment : openEnrollments) {
            scheduleTrackingService.unenroll(caseId, Arrays.asList(enrollment.getScheduleName()));
        }
    }

    private void fulfillIfImmunizationIsProvided(ChildImmunizationUpdationRequest request, String expectedProvidedImmunization, String scheduleToFulfill, String correspondingMilestoneToFulfill) {
        EnrollmentRecord record = scheduleTrackingService.getEnrollment(request.caseId(), scheduleToFulfill);
        if (record == null) {
            return;
        }

        boolean isProvided = request.isImmunizationProvided(expectedProvidedImmunization);
        String currentMilestoneName = record.getCurrentMilestoneName();
        if (isProvided && currentMilestoneName.equals(correspondingMilestoneToFulfill)) {
            scheduleTrackingService.fulfillCurrentMilestone(request.caseId(), scheduleToFulfill, DateUtil.today());
        }
    }
}
