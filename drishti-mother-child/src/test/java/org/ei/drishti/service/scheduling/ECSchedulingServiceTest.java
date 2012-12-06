package org.ei.drishti.service.scheduling;

import org.ei.drishti.contract.EligibleCoupleRegistrationRequest;
import org.ei.drishti.contract.FamilyPlanningUpdateRequest;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.repository.AllEligibleCouples;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;

import java.util.Map;

import static java.util.Arrays.asList;
import static org.ei.drishti.common.AllConstants.CommonCommCareFields.HIGH_PRIORITY_COMMCARE_FIELD_NAME;
import static org.ei.drishti.scheduler.DrishtiScheduleConstants.ECSchedulesConstants.EC_SCHEDULE_FP_COMPLICATION;
import static org.ei.drishti.util.EasyMap.create;
import static org.ei.drishti.util.EasyMap.mapOf;
import static org.joda.time.LocalDate.parse;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ECSchedulingServiceTest {
    @Mock
    private ScheduleTrackingService scheduleTrackingService;

    @Mock
    private AllEligibleCouples allEligibleCouples;

    private ECSchedulingService ecSchedulingService;

    public ECSchedulingServiceTest() {
        initMocks(this);
        ecSchedulingService = new ECSchedulingService(scheduleTrackingService);
    }

    @Test
    public void shouldEnrollHighPriorityECIntoFPComplicationsSchedulesWhenECIsRegisteredAndFPMethodIsNone() {
        EligibleCoupleRegistrationRequest request = new EligibleCoupleRegistrationRequest("CASE X", "EC Number 1", "Wife 1", "Husband 1", "ANM X", "Village X", "SubCenter X", "PHC X", "none", "Yes");

        Map<String, String> details = create("submissionDate", "2012-01-01").map();
        ecSchedulingService.enrollToFPComplications(request, details);

        verify(scheduleTrackingService).enroll(enrollmentFor(request.caseId(), EC_SCHEDULE_FP_COMPLICATION, parse("2012-01-01")));
    }

    @Test
    public void shouldEnrollHighPriorityECIntoFPComplicationsSchedulesWhenECIsRegisteredAndFPMethodIsEmpty() {
        EligibleCoupleRegistrationRequest request = new EligibleCoupleRegistrationRequest("CASE X", "EC Number 1", "Wife 1", "Husband 1", "ANM X", "Village X", "SubCenter X", "PHC X", "", "Yes");

        Map<String, String> details = create("submissionDate", "2012-01-01").map();
        ecSchedulingService.enrollToFPComplications(request, details);

        verify(scheduleTrackingService).enroll(enrollmentFor(request.caseId(), EC_SCHEDULE_FP_COMPLICATION, parse("2012-01-01")));
    }

    @Test
    public void shouldNotEnrollHighPriorityECIntoFPComplicationsSchedulesWhenECIsRegisteredAndFPMethodIsNeitherNoneNorEmpty() {
        EligibleCoupleRegistrationRequest request = new EligibleCoupleRegistrationRequest("CASE X", "EC Number 1", "Wife 1", "Husband 1", "ANM X", "Village X", "SubCenter X", "PHC X", "some method", "Yes");

        Map<String, String> details = create("submissionDate", "2012-01-01").map();
        ecSchedulingService.enrollToFPComplications(request, details);

        verifyZeroInteractions(scheduleTrackingService);
    }

    @Test
    public void shouldNotEnrollNormalPriorityECIntoFPComplicationsSchedulesWhenECIsRegistered() {
        EligibleCoupleRegistrationRequest request = new EligibleCoupleRegistrationRequest("CASE X", "EC Number 1", "Wife 1", "Husband 1", "ANM X", "Village X", "SubCenter X", "PHC X", "some method", "no");

        Map<String, String> details = create("submissionDate", "2012-01-01").map();
        ecSchedulingService.enrollToFPComplications(request, details);

        verifyZeroInteractions(scheduleTrackingService);
    }

    @Test
    public void shouldUnEnrollECFromFPComplicationsSchedulesIfFPUpdateHappensAndFPMethodIsNotNone() {
        EligibleCouple couple = new EligibleCouple("CASE X", "EC 1").withDetails(mapOf(HIGH_PRIORITY_COMMCARE_FIELD_NAME, "Yes"));
        EnrollmentRecord enrollment = new EnrollmentRecord("CASE X", EC_SCHEDULE_FP_COMPLICATION, null, null, null, null, null, null, null, null);
        when(scheduleTrackingService.getEnrollment("CASE X", EC_SCHEDULE_FP_COMPLICATION)).thenReturn(enrollment);
        FamilyPlanningUpdateRequest request = new FamilyPlanningUpdateRequest("CASE X", " ANM X")
                .withFpUpdate("change_fp_product")
                .withCurrentMethod("some method")
                .withFPStartDate("2012-12-08");

        ecSchedulingService.updateFPComplications(request, couple);

        verify(scheduleTrackingService).fulfillCurrentMilestone("CASE X", EC_SCHEDULE_FP_COMPLICATION, parse("2012-12-08"));
    }

    @Test
    public void shouldNotFulfillFPComplicationScheduleMilestoneIfECIsNotEnrolledToSchedule() {
        EligibleCouple couple = new EligibleCouple("CASE X", "EC 1").withDetails(mapOf(HIGH_PRIORITY_COMMCARE_FIELD_NAME, "Yes"));
        when(scheduleTrackingService.getEnrollment("CASE X", EC_SCHEDULE_FP_COMPLICATION)).thenReturn(null);
        FamilyPlanningUpdateRequest request = new FamilyPlanningUpdateRequest("CASE X", " ANM X")
                .withFpUpdate("change_fp_product")
                .withCurrentMethod("some method")
                .withFPStartDate("2012-12-08");

        ecSchedulingService.updateFPComplications(request, couple);

        verify(scheduleTrackingService, times(0)).fulfillCurrentMilestone("CASE X", EC_SCHEDULE_FP_COMPLICATION, parse("2012-12-08"));
    }

    @Test
    public void shouldNotUnEnrollECFromFPComplicationsSchedulesIfFPUpdateHappensAndFPMethodIsNone() {
        EligibleCouple couple = new EligibleCouple("CASE X", "EC 1").withDetails(mapOf(HIGH_PRIORITY_COMMCARE_FIELD_NAME, "Yes"));
        when(scheduleTrackingService.getEnrollment("CASE X", EC_SCHEDULE_FP_COMPLICATION)).thenReturn(null);
        FamilyPlanningUpdateRequest request = new FamilyPlanningUpdateRequest("CASE X", " ANM X")
                .withFpUpdate("change_fp_product")
                .withCurrentMethod("none")
                .withFPStartDate("2012-12-08");

        ecSchedulingService.updateFPComplications(request, couple);

        verify(scheduleTrackingService, times(0)).unenroll("CASE X", asList(EC_SCHEDULE_FP_COMPLICATION));
    }

    @Test
    public void shouldNotUnEnrollECFromFPComplicationsSchedulesIfFPUpdateHappensAndFPMethodIsEmpty() {
        EligibleCouple couple = new EligibleCouple("CASE X", "EC 1").withDetails(mapOf(HIGH_PRIORITY_COMMCARE_FIELD_NAME, "Yes"));
        when(scheduleTrackingService.getEnrollment("CASE X", EC_SCHEDULE_FP_COMPLICATION)).thenReturn(null);
        FamilyPlanningUpdateRequest request = new FamilyPlanningUpdateRequest("CASE X", " ANM X")
                .withFpUpdate("change_fp_product")
                .withCurrentMethod("")
                .withFPStartDate("2012-12-08");

        ecSchedulingService.updateFPComplications(request, couple);

        verify(scheduleTrackingService, times(0)).unenroll("CASE X", asList(EC_SCHEDULE_FP_COMPLICATION));
    }

    @Test
    public void shouldEnrollECToSchedulesWhenFPUpdateHappensAndFPMethodIsNone() {
        EligibleCouple couple = new EligibleCouple("CASE X", "EC 1").withDetails(mapOf(HIGH_PRIORITY_COMMCARE_FIELD_NAME, "Yes"));
        when(scheduleTrackingService.getEnrollment("CASE X", EC_SCHEDULE_FP_COMPLICATION)).thenReturn(null);
        FamilyPlanningUpdateRequest request = new FamilyPlanningUpdateRequest("CASE X", " ANM X")
                .withFpUpdate("change_fp_product")
                .withCurrentMethod("none")
                .withFPStartDate("2012-12-08");

        ecSchedulingService.updateFPComplications(request, couple);

        verify(scheduleTrackingService).enroll(enrollmentFor("CASE X", EC_SCHEDULE_FP_COMPLICATION, parse("2012-12-08")));
    }

    @Test
    public void shouldNotEnrollECToSchedulesWhenFPMethodIsNotChanged() {
        EligibleCouple couple = new EligibleCouple("CASE X", "EC 1").withDetails(mapOf(HIGH_PRIORITY_COMMCARE_FIELD_NAME, "Yes"));
        when(scheduleTrackingService.getEnrollment("CASE X", EC_SCHEDULE_FP_COMPLICATION)).thenReturn(null);
        FamilyPlanningUpdateRequest request = new FamilyPlanningUpdateRequest("CASE X", " ANM X")
                .withFpUpdate("record_ecp")
                .withCurrentMethod("")
                .withFPStartDate("");

        ecSchedulingService.updateFPComplications(request, couple);

        verify(scheduleTrackingService, times(0)).enroll(enrollmentFor("CASE X", EC_SCHEDULE_FP_COMPLICATION, parse("2012-12-08")));
    }

    @Test
    public void shouldEnrollECToSchedulesWhenFPUpdateHappensAndFPMethodIsEmpty() {
        EligibleCouple couple = new EligibleCouple("CASE X", "EC 1").withDetails(mapOf(HIGH_PRIORITY_COMMCARE_FIELD_NAME, "Yes"));
        when(scheduleTrackingService.getEnrollment("CASE X", EC_SCHEDULE_FP_COMPLICATION)).thenReturn(null);
        FamilyPlanningUpdateRequest request = new FamilyPlanningUpdateRequest("CASE X", " ANM X")
                .withFpUpdate("change_fp_product")
                .withCurrentMethod("")
                .withFPStartDate("2012-12-08");

        ecSchedulingService.updateFPComplications(request, couple);

        verify(scheduleTrackingService).enroll(enrollmentFor("CASE X", EC_SCHEDULE_FP_COMPLICATION, parse("2012-12-08")));
    }

    @Test
    public void shouldNotEnrollECToSchedulesWhenFPUpdateHappensAndFPMethodIsNotNoneOrEmpty() {
        EligibleCouple couple = new EligibleCouple("CASE X", "EC 1").withDetails(mapOf(HIGH_PRIORITY_COMMCARE_FIELD_NAME, "Yes"));
        when(scheduleTrackingService.getEnrollment("CASE X", EC_SCHEDULE_FP_COMPLICATION)).thenReturn(null);
        FamilyPlanningUpdateRequest request = new FamilyPlanningUpdateRequest("CASE X", " ANM X")
                .withFpUpdate("change_fp_product")
                .withCurrentMethod("some method")
                .withFPStartDate("2012-12-08");

        ecSchedulingService.updateFPComplications(request, couple);

        verify(scheduleTrackingService, times(0)).enroll(any(EnrollmentRequest.class));
    }

    @Test
    public void shouldNotEnrollAlreadyEnrolledECToSchedulesWhenFPUpdateHappensAndFPMethodIsNone() {
        EligibleCouple couple = new EligibleCouple("CASE X", "EC 1").withDetails(mapOf(HIGH_PRIORITY_COMMCARE_FIELD_NAME, "Yes"));
        EnrollmentRecord record = new EnrollmentRecord("CASE X", EC_SCHEDULE_FP_COMPLICATION, null, null, null, null, null, null, null, null);
        when(scheduleTrackingService.getEnrollment("CASE X", EC_SCHEDULE_FP_COMPLICATION)).thenReturn(record);
        FamilyPlanningUpdateRequest request = new FamilyPlanningUpdateRequest("CASE X", " ANM X")
                .withFpUpdate("change_fp_product")
                .withCurrentMethod("none")
                .withFPStartDate("2012-12-08");

        ecSchedulingService.updateFPComplications(request, couple);

        verify(scheduleTrackingService, times(0)).enroll(any(EnrollmentRequest.class));
    }

    @Test
    public void shouldNotEnrollNormalPriorityECToSchedulesWhenFPUpdateHappensAndFPMethodIsNone() {
        EligibleCouple couple = new EligibleCouple("CASE X", "EC 1").withDetails(mapOf(HIGH_PRIORITY_COMMCARE_FIELD_NAME, "No"));
        when(scheduleTrackingService.getEnrollment("CASE X", EC_SCHEDULE_FP_COMPLICATION)).thenReturn(null);
        FamilyPlanningUpdateRequest request = new FamilyPlanningUpdateRequest("CASE X", " ANM X")
                .withFpUpdate("change_fp_product")
                .withCurrentMethod("some method")
                .withFPStartDate("2012-12-08");

        ecSchedulingService.updateFPComplications(request, couple);

        verify(scheduleTrackingService, times(0)).enroll(any(EnrollmentRequest.class));
    }

    private EnrollmentRequest enrollmentFor(final String caseId, final String scheduleName, final LocalDate lmp) {
        return argThat(new ArgumentMatcher<EnrollmentRequest>() {
            @Override
            public boolean matches(Object o) {
                EnrollmentRequest request = (EnrollmentRequest) o;
                return caseId.equals(request.getExternalId()) && lmp.equals(request.getReferenceDate())
                        && scheduleName.equals(request.getScheduleName());
            }
        });
    }
}
