package org.ei.drishti.service.scheduling;

import org.ei.drishti.contract.FamilyPlanningUpdateRequest;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.domain.FPProductInformation;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.service.ActionService;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;

import static java.util.Arrays.asList;
import static org.ei.drishti.common.AllConstants.CommonCommCareFields.HIGH_PRIORITY_COMMCARE_FIELD_NAME;
import static org.ei.drishti.common.util.DateUtil.fakeIt;
import static org.ei.drishti.scheduler.DrishtiScheduleConstants.ECSchedulesConstants.EC_SCHEDULE_FP_COMPLICATION;
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
    @Mock
    private ActionService actionService;

    private ECSchedulingService ecSchedulingService;

    public ECSchedulingServiceTest() {
        initMocks(this);
        ecSchedulingService = new ECSchedulingService(scheduleTrackingService, actionService);
    }

    @Test
    public void shouldEnrollHighPriorityECIntoFPComplicationsSchedulesWhenECIsRegisteredAndFPMethodIsNone() {
        ecSchedulingService.enrollToFPComplications("CASE X", "none", "Yes", "2012-01-01");

        verify(scheduleTrackingService).enroll(enrollmentFor("CASE X", EC_SCHEDULE_FP_COMPLICATION, parse("2012-01-01")));
    }

    @Test
    public void shouldEnrollHighPriorityECIntoFPComplicationsSchedulesWhenECIsRegisteredAndFPMethodIsEmpty() {
        ecSchedulingService.enrollToFPComplications("CASE X", "", "Yes", "2012-01-01");

        verify(scheduleTrackingService).enroll(enrollmentFor("CASE X", EC_SCHEDULE_FP_COMPLICATION, parse("2012-01-01")));
    }

    @Test
    public void shouldNotEnrollHighPriorityECIntoFPComplicationsSchedulesWhenECIsRegisteredAndFPMethodIsNeitherNoneNorEmpty() {
        ecSchedulingService.enrollToFPComplications("CASE X", "some method", "Yes", "2012-01-01");

        verifyZeroInteractions(scheduleTrackingService);
    }

    @Test
    public void shouldNotEnrollNormalPriorityECIntoFPComplicationsSchedulesWhenECIsRegistered() {
        ecSchedulingService.enrollToFPComplications("CASE X", "some method", "no", "2012-01-01");

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

    @Test
    public void shouldEnrollECIntoDMPAInjectableRefillScheduleWhenECIsRegisteredAndUsesDMPAInjectableFPMethod() {
        ecSchedulingService.enrollToRenewFPProducts("entity id 1", "dmpa_injectable", "2012-01-01", null, null);

        verify(scheduleTrackingService).enroll(enrollmentFor("entity id 1", "DMPA Injectable Refill", parse("2012-01-01")));
    }

    @Test
    public void shouldEnrollECIntoOCPRefillScheduleWhenECIsRegisteredAndUsesOCPFPMethod() {
        fakeIt(parse("2012-02-01"));
        ecSchedulingService.enrollToRenewFPProducts("entity id 1", "ocp", null, "1", "2012-01-01");
        verify(scheduleTrackingService).enroll(enrollmentFor("entity id 1", "OCP Refill", parse("2012-01-15")));

        ecSchedulingService.enrollToRenewFPProducts("entity id 1", "ocp", null, "2", "2012-01-01");
        verify(scheduleTrackingService).enroll(enrollmentFor("entity id 1", "OCP Refill", parse("2012-02-12")));

        ecSchedulingService.enrollToRenewFPProducts("entity id 1", "ocp", null, "0", "2012-01-01");
        verify(scheduleTrackingService).enroll(enrollmentFor("entity id 1", "OCP Refill", parse("2012-02-01")));
    }

    @Test
    public void shouldEnrollECIntoCondomRefillScheduleWhenECIsRegisteredAndUsesCondomFPMethod() {
        fakeIt(parse("2012-01-15"));
        ecSchedulingService.enrollToRenewFPProducts("entity id 1", "condom", "2012-01-15", null, null);

        verify(scheduleTrackingService).enroll(enrollmentFor("entity id 1", "Condom Refill", parse("2012-02-01")));

        fakeIt(parse("2012-12-01"));
        ecSchedulingService.enrollToRenewFPProducts("entity id 1", "condom", "2012-12-01", null, null);

        verify(scheduleTrackingService).enroll(enrollmentFor("entity id 1", "Condom Refill", parse("2013-01-01")));
    }

    @Test
    public void shouldNotEnrollECIntoDMPAInjectableRefillScheduleWhenECIsRegisteredAndDoesNotUseDMPAInjectableFPMethod() {
        ecSchedulingService.enrollToRenewFPProducts("entity id 1", "not dmpa", "2012-01-01", null, null);

        verifyZeroInteractions(scheduleTrackingService);
    }

    @Test
    public void shouldUnEnrollECFromPreviousRefillScheduleWhenFPMethodIsChanged() {
        ecSchedulingService.fpChange(new FPProductInformation("entity id 1", "anm id 1", "condom", "2012-01-01", "1", "2012-01-01", null, null, "ocp", "2012-01-01"));
        verify(scheduleTrackingService).unenroll("entity id 1", asList("OCP Refill"));
        verify(actionService).markAlertAsClosed("entity id 1", "anm id 1", "OCP Refill", "2012-01-01");

        ecSchedulingService.fpChange(new FPProductInformation("entity id 1", "anm id 1", "condom", "2012-01-01", "1", "2012-01-01", null, null, "dmpa_injectable", "2012-01-01"));
        verify(scheduleTrackingService).unenroll("entity id 1", asList("DMPA Injectable Refill"));
        verify(actionService).markAlertAsClosed("entity id 1", "anm id 1", "DMPA Injectable Refill", "2012-01-01");

        ecSchedulingService.fpChange(new FPProductInformation("entity id 1", "anm id 1", "ocp", "2012-01-01", "1", "2012-01-01", null, null, "condom", "2012-01-01"));
        verify(scheduleTrackingService).unenroll("entity id 1", asList("Condom Refill"));
        verify(actionService).markAlertAsClosed("entity id 1", "anm id 1", "Condom Refill", "2012-01-01");
    }

    @Test
    public void shouldEnrollECIntoDMPAInjectableRefillScheduleWhenFPMethodIsChangedToDMPAInjectable() {
        ecSchedulingService.fpChange(new FPProductInformation("entity id 1", "anm id 1", "dmpa_injectable", "2012-01-01", "1", "2012-01-01", null, null, "condom", "2012-01-01"));

        verify(scheduleTrackingService).enroll(enrollmentFor("entity id 1", "DMPA Injectable Refill", parse("2012-01-01")));
    }

    @Test
    public void shouldEnrollECIntoOCPRefillScheduleWhenFPMethodIsChangedToOCP() {
        fakeIt(parse("2012-02-01"));

        ecSchedulingService.fpChange(new FPProductInformation("entity id 1", "anm id 1", "ocp", "2012-01-01", "1", "2012-01-01", null, null, "condom", "2012-01-01"));

        verify(scheduleTrackingService).enroll(enrollmentFor("entity id 1", "OCP Refill", parse("2012-01-15")));
    }

    @Test
    public void shouldEnrollECIntoCondomRefillScheduleWhenFPMethodIsChangedToCondom() {
        fakeIt(parse("2012-01-15"));

        ecSchedulingService.fpChange(new FPProductInformation("entity id 1", "anm id 1", "condom", "2012-01-01", "1", "2012-01-01", null, null, "ocp", "2012-01-01"));

        verify(scheduleTrackingService).enroll(enrollmentFor("entity id 1", "Condom Refill", parse("2012-02-01")));
    }

    @Test
    public void shouldUpdateOCPRefillScheduleWhenOCPPillsAreResupplied() {
        ecSchedulingService.renewFPProduct(new FPProductInformation("entity id 1", "anm id 1", "ocp", null, "1", "2012-01-01", null, "2011-01-12", null, null));

        InOrder inOrder = inOrder(scheduleTrackingService, actionService);
        inOrder.verify(scheduleTrackingService).unenroll("entity id 1", asList("OCP Refill"));
        inOrder.verify(actionService).markAlertAsClosed("entity id 1", "anm id 1", "OCP Refill", "2012-01-01");
        inOrder.verify(scheduleTrackingService).enroll(enrollmentFor("entity id 1", "OCP Refill", parse("2012-01-15")));
    }

    @Test
    public void shouldDoNothingWhenZeroOCPPillsAreResupplied() {
        ecSchedulingService.renewFPProduct(new FPProductInformation("entity id 1", "anm id 1", "ocp", null, "0", "2012-01-02", null, "2011-01-12", null, null));

        verifyZeroInteractions(scheduleTrackingService);
        verifyZeroInteractions(actionService);
    }

    @Test
    public void shouldDoNothingWhenOCPPillsAreNotResupplied() {
        ecSchedulingService.renewFPProduct(new FPProductInformation("entity id 1", "anm id 1", "ocp", null, "", "2012-01-02", null, "2011-01-12", null, null));

        verifyZeroInteractions(scheduleTrackingService);
        verifyZeroInteractions(actionService);
    }

    @Test
    public void shouldUpdateDMPAInjectableRefillScheduleWhenDMPAIsReinjected() {
        ecSchedulingService.renewFPProduct(new FPProductInformation("entity id 1", "anm id 1", "dmpa_injectable", "2012-01-01", null, null, null, "2011-01-12", null, ""));

        InOrder inOrder = inOrder(scheduleTrackingService, actionService);
        inOrder.verify(scheduleTrackingService).unenroll("entity id 1", asList("DMPA Injectable Refill"));
        inOrder.verify(actionService).markAlertAsClosed("entity id 1", "anm id 1", "DMPA Injectable Refill", "2012-01-01");
        inOrder.verify(scheduleTrackingService).enroll(enrollmentFor("entity id 1", "DMPA Injectable Refill", parse("2012-01-01")));
    }

    @Test
    public void shouldDoNothingWhenDMPANotInjected() {
        ecSchedulingService.renewFPProduct(new FPProductInformation("entity id 1", "anm id 1", "dmpa_injectable", "", null, null, null, "2011-01-12", null, ""));

        verifyZeroInteractions(scheduleTrackingService);
        verifyZeroInteractions(actionService);

        ecSchedulingService.renewFPProduct(new FPProductInformation("entity id 1", "anm id 1", "dmpa_injectable", null, null, null, null, "2011-01-12", null, ""));

        verifyZeroInteractions(scheduleTrackingService);
        verifyZeroInteractions(actionService);
    }

    @Test
    public void shouldUpdateECFromCondomRefillScheduleWhenCondomsAreResupplied() {
        fakeIt(parse("2011-01-15"));

        ecSchedulingService.renewFPProduct(new FPProductInformation("entity id 1", "anm id 1", "condom", null, null, null, "20", "2011-01-12", null, ""));

        InOrder inOrder = inOrder(scheduleTrackingService, actionService);
        inOrder.verify(scheduleTrackingService).unenroll("entity id 1", asList("Condom Refill"));
        inOrder.verify(actionService).markAlertAsClosed("entity id 1", "anm id 1", "Condom Refill", "2011-01-12");
        inOrder.verify(scheduleTrackingService).enroll(enrollmentFor("entity id 1", "Condom Refill", parse("2011-02-01")));
    }

    @Test
    public void shouldDoNothingWhenCondomsAreNotResupplied() {
        ecSchedulingService.renewFPProduct(new FPProductInformation("entity id 1", "anm id 1", "condom", null, null, null, "", "2011-01-012", null, ""));

        verifyZeroInteractions(scheduleTrackingService);
        verifyZeroInteractions(actionService);
    }

    private EnrollmentRequest enrollmentFor(final String caseId, final String scheduleName, final LocalDate referenceDate) {
        return argThat(new ArgumentMatcher<EnrollmentRequest>() {
            @Override
            public boolean matches(Object o) {
                EnrollmentRequest request = (EnrollmentRequest) o;
                return caseId.equals(request.getExternalId()) && referenceDate.equals(request.getReferenceDate())
                        && scheduleName.equals(request.getScheduleName());
            }
        });
    }
}
