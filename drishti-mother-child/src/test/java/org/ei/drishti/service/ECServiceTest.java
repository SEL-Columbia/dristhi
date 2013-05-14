package org.ei.drishti.service;

import org.ei.drishti.contract.EligibleCoupleCloseRequest;
import org.ei.drishti.contract.OutOfAreaANCRegistrationRequest;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.domain.FPProductInformation;
import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.service.formSubmissionHandler.ReportFieldsDefinition;
import org.ei.drishti.service.reporting.ECReportingService;
import org.ei.drishti.service.scheduling.ECSchedulingService;
import org.ei.drishti.util.FormSubmissionBuilder;
import org.ei.drishti.util.IdGenerator;
import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.ei.drishti.util.EasyMap.mapOf;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ECServiceTest {
    @Mock
    private AllEligibleCouples allEligibleCouples;
    @Mock
    private ActionService actionService;
    @Mock
    private IdGenerator idGenerator;
    @Mock
    private ECReportingService reportingService;
    @Mock
    private ECSchedulingService schedulingService;
    @Mock
    private ReportFieldsDefinition reportFieldsDefinition;

    private ECService ecService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        ecService = new ECService(allEligibleCouples, actionService, reportingService, idGenerator, schedulingService, reportFieldsDefinition);
    }

    @Test
    public void shouldRegisterEligibleCouple() throws Exception {
        FormSubmission submission = FormSubmissionBuilder.create()
                .withFormName("ec_registration")
                .withANMId("anm id 1")
                .withEntityId("entity id 1")
                .addFormField("someKey", "someValue")
                .addFormField("currentMethod", "some method")
                .addFormField("isHighPriority", "yes")
                .addFormField("submissionDate", "2011-01-01")
                .addFormField("dmpaInjectionDate", "2010-12-20")
                .addFormField("numberOfOCPDelivered", "1")
                .addFormField("ocpRefillDate", "2010-12-25")
                .addFormField("numberOfCondomsSupplied", "20")
                .build();
        EligibleCouple eligibleCouple = new EligibleCouple("entity id 1", "0").withCouple("Wife 1", "Husband 1");
        when(allEligibleCouples.findByCaseId("entity id 1")).thenReturn(eligibleCouple);
        when(reportFieldsDefinition.get("ec_registration")).thenReturn(asList("someKey"));

        ecService.registerEligibleCouple(submission);

        verify(allEligibleCouples).update(eligibleCouple.withANMIdentifier("ANM X"));
        verify(reportingService).registerEC(new SafeMap(mapOf("someKey", "someValue")));
        verify(schedulingService).registerEC(new FPProductInformation("entity id 1", "anm id 1", "some method", null, "2010-12-20", "1", "2010-12-25"
                , "20", "2011-01-01", null, null, null, null));
    }

    @Test
    public void shouldRegisterEligibleCoupleForOutOfAreaANC() throws Exception {
        Map<String, Map<String, String>> extraData = mapOf("details", Collections.<String, String>emptyMap());
        UUID ecCaseId = randomUUID();
        when(idGenerator.generateUUID()).thenReturn(ecCaseId);

        ecService.registerEligibleCoupleForOutOfAreaANC(new OutOfAreaANCRegistrationRequest("CASE X", "Wife 1", "Husband 1", "ANM X", "Village X", "SubCenter X", "PHC X", "TC 1", "2012-05-05", "9876543210"), extraData);

        EligibleCouple couple = new EligibleCouple(ecCaseId.toString(), "0").withCouple("Wife 1", "Husband 1")
                .withANMIdentifier("ANM X").withLocation("Village X", "SubCenter X", "PHC X").withDetails(extraData.get("details")).asOutOfArea();
        verify(allEligibleCouples).register(couple);
    }

    @Test
    public void shouldSendDataToReportingServiceDuringReportFPChange() throws Exception {
        EligibleCouple ec = new EligibleCouple("entity id 1", "EC Number 1");
        when(allEligibleCouples.findByCaseId("entity id 1")).thenReturn(ec);
        FormSubmission submission = FormSubmissionBuilder.create()
                .withFormName("fp_change")
                .addFormField("someKey", "someValue")
                .build();
        when(reportFieldsDefinition.get("fp_change")).thenReturn(asList("someKey"));

        ecService.reportFPChange(submission);

        verify(allEligibleCouples).findByCaseId("entity id 1");
        verify(reportingService).fpChange(new SafeMap(mapOf("someKey", "someValue")));
    }

    @Test
    public void shouldNotDoAnythingDuringReportFPChangeIfNoECIsFound() throws Exception {
        when(allEligibleCouples.findByCaseId("entity id 1")).thenReturn(null);
        FormSubmission submission = FormSubmissionBuilder.create().build();

        ecService.reportFPChange(submission);

        verify(allEligibleCouples).findByCaseId("entity id 1");
        verifyZeroInteractions(reportingService);
        verifyZeroInteractions(schedulingService);
    }

    @Test
    public void shouldUpdateECSchedulesWhenFPMethodIsChanged() throws Exception {
        EligibleCouple ec = new EligibleCouple("entity id 1", "EC Number 1");
        when(allEligibleCouples.findByCaseId("entity id 1")).thenReturn(ec);
        FormSubmission submission = FormSubmissionBuilder.create()
                .withFormName("fp_change")
                .withANMId("anm id 1")
                .addFormField("currentMethod", "previous method")
                .addFormField("newMethod", "new method")
                .addFormField("submissionDate", "2011-01-01")
                .addFormField("familyPlanningMethodChangeDate", "2011-01-02")
                .addFormField("numberOfOCPDelivered", "1")
                .addFormField("numberOfCondomsSupplied", "20")
                .build();
        when(reportFieldsDefinition.get("fp_change")).thenReturn(asList("someKey"));

        ecService.reportFPChange(submission);

        verify(schedulingService).fpChange(
                new FPProductInformation("entity id 1", "anm id 1", "new method",
                        "previous method", null, "1", null, "20", "2011-01-01", "2011-01-02", null, null, null));
    }

    @Test
    public void shouldUpdateECSchedulesWhenFPProductIsRenewed() throws Exception {
        EligibleCouple ec = new EligibleCouple("entity id 1", "EC Number 1");
        when(allEligibleCouples.findByCaseId("entity id 1")).thenReturn(ec);
        FormSubmission submission = FormSubmissionBuilder.create()
                .withFormName("renew_fp_product")
                .withANMId("anm id 1")
                .addFormField("currentMethod", "fp method")
                .addFormField("submissionDate", "2011-01-01")
                .addFormField("numberOfOCPDelivered", "1")
                .addFormField("ocpRefillDate", "2010-12-25")
                .addFormField("dmpaInjectionDate", "2010-12-20")
                .addFormField("numberOfCondomsSupplied", "20")
                .build();

        ecService.renewFPProduct(submission);

        verify(schedulingService).renewFPProduct(new FPProductInformation("entity id 1", "anm id 1", "fp method", null, "2010-12-20", "1", "2010-12-25", "20", "2011-01-01", null, null, null, null));
    }

    @Test
    public void shouldNotDoAnythingDuringRenewFPProductWhenNoECIsFound() throws Exception {
        when(allEligibleCouples.findByCaseId("entity id 1")).thenReturn(null);
        FormSubmission submission = FormSubmissionBuilder.create().build();

        ecService.renewFPProduct(submission);

        verify(allEligibleCouples).findByCaseId("entity id 1");
        verifyZeroInteractions(reportingService);
        verifyZeroInteractions(schedulingService);
    }

    @Test
    public void shouldUpdateECSchedulesWhenFPFollowupOccurs() throws Exception {
        EligibleCouple ec = new EligibleCouple("entity id 1", "EC Number 1");
        when(allEligibleCouples.findByCaseId("entity id 1")).thenReturn(ec);
        FormSubmission submission = FormSubmissionBuilder.create()
                .withFormName("fp_followup")
                .withANMId("anm id 1")
                .addFormField("currentMethod", "fp method")
                .addFormField("submissionDate", "2011-01-01")
                .addFormField("fpFollowupDate", "2010-12-20")
                .addFormField("needsFollowup", "yes")
                .addFormField("needsReferralFollowup", "no")
                .build();

        ecService.reportFPFollowup(submission);

        verify(schedulingService).fpFollowup(new FPProductInformation("entity id 1", "anm id 1", "fp method", null, null, null, null, null, "2011-01-01", null, "2010-12-20", "yes", "no"));
    }

    @Test
    public void shouldNotDoAnythingDuringFPFollowupWhenNoECIsFound() throws Exception {
        when(allEligibleCouples.findByCaseId("entity id 1")).thenReturn(null);
        FormSubmission submission = FormSubmissionBuilder.create().build();

        ecService.reportFPFollowup(submission);

        verify(allEligibleCouples).findByCaseId("entity id 1");
        verifyZeroInteractions(reportingService);
        verifyZeroInteractions(schedulingService);
    }

    @Test
    public void shouldCloseEligibleCouple() throws Exception {
        when(allEligibleCouples.exists("CASE X")).thenReturn(true);

        ecService.closeEligibleCouple(new EligibleCoupleCloseRequest("CASE X", "ANM X"));

        verify(allEligibleCouples).close("CASE X");
        verify(actionService).closeEligibleCouple("CASE X", "ANM X");
    }

    @Test
    public void shouldNotCloseEligibleCoupleWhenECDoesNotExist() throws Exception {
        when(allEligibleCouples.exists("CASE X")).thenReturn(false);

        ecService.closeEligibleCouple(new EligibleCoupleCloseRequest("CASE X", "ANM X"));

        verify(allEligibleCouples, times(0)).close("CASE X");
        verifyZeroInteractions(actionService);
    }

    @Test
    public void shouldNotDoAnythingWhenFPComplicationIsReportedAndECIsNotFound() throws Exception {
        when(allEligibleCouples.findByCaseId("entity id 1")).thenReturn(null);
        FormSubmission submission = FormSubmissionBuilder.create().withFormName("fp_complications").build();

        ecService.reportFPComplications(submission);

        verify(allEligibleCouples).findByCaseId("entity id 1");
        verifyZeroInteractions(reportingService);
        verifyZeroInteractions(schedulingService);
    }

    @Test
    public void shouldUpdateECSchedulesWhenFPComplicationsIsReported() throws Exception {
        EligibleCouple ec = new EligibleCouple("entity id 1", "EC Number 1");
        when(allEligibleCouples.findByCaseId("entity id 1")).thenReturn(ec);
        FormSubmission submission = FormSubmissionBuilder.create()
                .withFormName("fp_complications")
                .withANMId("anm id 1")
                .addFormField("currentMethod", "fp method")
                .addFormField("complicationDate", "2010-12-25")
                .addFormField("submissionDate", "2010-12-24")
                .addFormField("needsFollowup", "yes")
                .addFormField("needsReferralFollowup", "no")
                .build();

        ecService.reportFPComplications(submission);

        verify(schedulingService).reportFPComplications(new FPProductInformation("entity id 1", "anm id 1", null, null, null, null, null, null, "2010-12-24", null, "2010-12-25", "yes", "no"));
    }

    @Test
    public void shouldNotDoAnythingWhenFPReferralFollowupIsReportedAndECIsNotFound() throws Exception {
        when(allEligibleCouples.findByCaseId("entity id 1")).thenReturn(null);
        FormSubmission submission = FormSubmissionBuilder.create().withFormName("fp_referral_followup").build();

        ecService.reportReferralFollowup(submission);

        verify(allEligibleCouples).findByCaseId("entity id 1");
        verifyZeroInteractions(reportingService);
        verifyZeroInteractions(schedulingService);
    }

    @Test
    public void shouldUpdateECSchedulesWhenFPReferralFollowupIsReported() throws Exception {
        EligibleCouple ec = new EligibleCouple("entity id 1", "EC Number 1");
        when(allEligibleCouples.findByCaseId("entity id 1")).thenReturn(ec);
        FormSubmission submission = FormSubmissionBuilder.create()
                .withFormName("fp_referral_followup")
                .withANMId("anm id 1")
                .addFormField("referralFollowupDate", "2010-12-25")
                .addFormField("submissionDate", "2010-12-24")
                .addFormField("needsFollowup", "yes")
                .addFormField("needsReferralFollowup", "no")
                .build();

        ecService.reportReferralFollowup(submission);

        verify(schedulingService).reportReferralFollowup(new FPProductInformation("entity id 1", "anm id 1", null, null, null, null, null, null, "2010-12-24", null, "2010-12-25", "yes", "no"));
    }
}
