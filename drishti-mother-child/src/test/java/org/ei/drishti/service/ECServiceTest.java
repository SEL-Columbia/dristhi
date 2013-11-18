package org.ei.drishti.service;

import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.domain.FPProductInformation;
import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.service.formSubmission.handler.ReportFieldsDefinition;
import org.ei.drishti.service.reporting.ECReportingService;
import org.ei.drishti.service.scheduling.ECSchedulingService;
import org.ei.drishti.util.FormSubmissionBuilder;
import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collections;

import static java.util.Arrays.asList;
import static org.ei.drishti.common.util.EasyMap.create;
import static org.ei.drishti.common.util.EasyMap.mapOf;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ECServiceTest {
    @Mock
    private AllEligibleCouples allEligibleCouples;
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
        ecService = new ECService(allEligibleCouples, schedulingService, reportingService, reportFieldsDefinition);
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
    public void shouldUpdateCurrentFPMethodOfECWhenFPMethodIsChanged() throws Exception {
        EligibleCouple ec = new EligibleCouple("entity id 1", "EC Number 1").withDetails(create("currentMethod", "ocp").put("caste", "c_others").map());
        when(allEligibleCouples.findByCaseId("entity id 1")).thenReturn(ec);
        FormSubmission submission = FormSubmissionBuilder.create()
                .withFormName("fp_change")
                .addFormField("currentMethod", "ocp")
                .addFormField("newMethod", "condom")
                .build();

        ecService.reportFPChange(submission);

        verify(allEligibleCouples).update(ec.withDetails(create("currentMethod", "condom").put("caste", "c_others").map()));
    }

    @Test
    public void shouldSendDataToReportingServiceDuringReportFPChange() throws Exception {
        EligibleCouple ec = new EligibleCouple("entity id 1", "EC Number 1").withDetails(create("caste", "sc").put("economicStatus", "bpl").map());
        when(allEligibleCouples.findByCaseId("entity id 1")).thenReturn(ec);
        FormSubmission submission = FormSubmissionBuilder.create()
                .withFormName("fp_change")
                .addFormField("someKey", "someValue")
                .build();
        when(reportFieldsDefinition.get("fp_change")).thenReturn(asList("someKey"));

        ecService.reportFPChange(submission);

        verify(allEligibleCouples).findByCaseId("entity id 1");
        verify(reportingService).fpChange(new SafeMap(create("someKey", "someValue").put("caste", "sc").put("economicStatus", "bpl").map()));
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
        EligibleCouple ec = new EligibleCouple("entity id 1", "EC Number 1").withDetails(Collections.<String, String>emptyMap());
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
    public void shouldUseSubmissionDateAsChangeDateWhenFPMethodIsChangedAndChangeDateIsBlank() throws Exception {
        EligibleCouple ec = new EligibleCouple("entity id 1", "EC Number 1").withDetails(Collections.<String, String>emptyMap());
        when(allEligibleCouples.findByCaseId("entity id 1")).thenReturn(ec);
        FormSubmission submission = FormSubmissionBuilder.create()
                .withFormName("fp_change")
                .withANMId("anm id 1")
                .addFormField("currentMethod", "previous method")
                .addFormField("newMethod", "none")
                .addFormField("submissionDate", "2011-02-01")
                .build();
        when(reportFieldsDefinition.get("fp_change")).thenReturn(asList("someKey"));

        ecService.reportFPChange(submission);

        verify(schedulingService).fpChange(
                new FPProductInformation("entity id 1", "anm id 1", "none",
                        "previous method", null, null, null, null, "2011-02-01", "2011-02-01", null, null, null));
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

    @Test
    public void shouldCloseEC() throws Exception {
        when(allEligibleCouples.exists("entity id 1")).thenReturn(true);

        FormSubmission submission = FormSubmissionBuilder.create()
                .withFormName("ec_close")
                .addFormField("isECCloseConfirmed", "yes")
                .build();
        ecService.closeEligibleCouple(submission);

        verify(allEligibleCouples).close("entity id 1");
    }

    @Test
    public void shouldNotCloseECWhenANMDoesNotConfirmClose() throws Exception {
        when(allEligibleCouples.exists("entity id 1")).thenReturn(true);

        FormSubmission submission = FormSubmissionBuilder.create()
                .withFormName("ec_close")
                .addFormField("isECCloseConfirmed", "no")
                .build();
        ecService.closeEligibleCouple(submission);

        verify(allEligibleCouples, times(0)).close("entity id 1");
    }

    @Test
    public void shouldNotCloseECWhenECDoesNotExist() throws Exception {
        when(allEligibleCouples.exists("entity id 1")).thenReturn(false);

        FormSubmission submission = FormSubmissionBuilder.create()
                .withFormName("ec_close")
                .build();
        ecService.closeEligibleCouple(submission);

        verify(allEligibleCouples, times(0)).close("entity id 1");
    }
}
