package org.ei.drishti.service;

import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.domain.FPProductInformation;
import org.ei.drishti.domain.register.*;
import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.service.formSubmission.handler.ReportFieldsDefinition;
import org.ei.drishti.service.reporting.ECReportingService;
import org.ei.drishti.service.scheduling.ECSchedulingService;
import org.ei.drishti.util.FormSubmissionBuilder;
import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
                .addFormField("lmpDate", "2011-01-01")
                .addFormField("uptResult", "negative")
                .addFormField("currentMethod", "iud")
                .addFormField("isHighPriority", "yes")
                .addFormField("submissionDate", "2011-01-01")
                .addFormField("dmpaInjectionDate", "2010-12-20")
                .addFormField("numberOfOCPDelivered", "1")
                .addFormField("ocpRefillDate", "2010-12-25")
                .addFormField("iudPlace", "phc")
                .addFormField("familyPlanningMethodChangeDate", "2011-01-01")
                .build();
        EligibleCouple eligibleCouple = new EligibleCouple("entity id 1", "0").withCouple("Wife 1", "Husband 1");
        when(allEligibleCouples.findByCaseId("entity id 1")).thenReturn(eligibleCouple);
        when(reportFieldsDefinition.get("ec_registration")).thenReturn(asList("someKey"));

        ecService.registerEligibleCouple(submission);

        verify(allEligibleCouples).update(Matchers.eq(eligibleCouple
                .withANMIdentifier("ANM X")
                .withIUDFPDetails(asList(new IUDFPDetails("2011-01-01", "phc", "2011-01-01", "negative")))
                .withCondomFPDetails(Collections.<CondomFPDetails>emptyList())
                .withOCPFPDetails(Collections.<OCPFPDetails>emptyList())
                .withFemaleSterilizationFPDetails(Collections.<SterilizationFPDetails>emptyList())
                .withMaleSterilizationFPDetails(Collections.<SterilizationFPDetails>emptyList())
        ));
        verify(reportingService).registerEC(new SafeMap(mapOf("someKey", "someValue")));
        verify(schedulingService).registerEC(new FPProductInformation("entity id 1", "anm id 1", "iud", null, "2010-12-20", "1", "2010-12-25"
                , null, "2011-01-01", "2011-01-01", null, null, null));
    }

    @Test
    public void shouldRegisterEligibleCoupleAndUpdateCondomDetails() throws Exception {
        FormSubmission submission = FormSubmissionBuilder.create()
                .withFormName("ec_registration")
                .withANMId("anm id 1")
                .withEntityId("entity id 1")
                .addFormField("someKey", "someValue")
                .addFormField("lmpDate", "2011-01-01")
                .addFormField("uptResult", "negative")
                .addFormField("currentMethod", "some method")
                .addFormField("isHighPriority", "yes")
                .addFormField("submissionDate", "2011-01-01")
                .addFormField("dmpaInjectionDate", "2010-12-20")
                .addFormField("familyPlanningMethodChangeDate", "2011-01-01")
                .addFormField("numberOfCondomsSupplied", "20")
                .build();
        EligibleCouple eligibleCouple = new EligibleCouple("entity id 1", "0").withCouple("Wife 1", "Husband 1");
        when(allEligibleCouples.findByCaseId("entity id 1")).thenReturn(eligibleCouple);
        when(reportFieldsDefinition.get("ec_registration")).thenReturn(asList("someKey"));

        ecService.registerEligibleCouple(submission);

        verify(allEligibleCouples).update(eligibleCouple
                .withANMIdentifier("ANM X")
                .withCondomFPDetails(asList(new CondomFPDetails("2011-01-01",
                        asList(create("date", "2011-01-01").put("quantity", "20").map())))));
        verify(reportingService).registerEC(new SafeMap(mapOf("someKey", "someValue")));
        verify(schedulingService).registerEC(new FPProductInformation("entity id 1", "anm id 1", "some method", null, "2010-12-20", null, null
                , "20", "2011-01-01", "2011-01-01", null, null, null));
    }

    @Test
    public void shouldUpdateCurrentFPMethodOfECWhenFPMethodIsChanged() throws Exception {
        List<CondomFPDetails> condomFPDetails = new ArrayList<>();
        condomFPDetails.add(new CondomFPDetails(
                "2010-01-01", asList(create("date", "2010-01-01").put("quantity", "20").map())));
        EligibleCouple ec = new EligibleCouple("entity id 1", "EC Number 1")
                .withDetails(create("currentMethod", "ocp").put("caste", "c_others").map())
                .withIUDFPDetails(Collections.<IUDFPDetails>emptyList())
                .withCondomFPDetails(condomFPDetails)
                .withOCPFPDetails(asList(new OCPFPDetails(
                        "2010-06-01", asList(create("date", "2011-06-01").put("quantity", "20").map()), "2011-06-01", "negative")))
                .withFemaleSterilizationFPDetails(Collections.<SterilizationFPDetails>emptyList())
                .withMaleSterilizationFPDetails(Collections.<SterilizationFPDetails>emptyList());

        when(allEligibleCouples.findByCaseId("entity id 1")).thenReturn(ec);
        FormSubmission submission = FormSubmissionBuilder.create()
                .withFormName("fp_change")
                .addFormField("currentMethod", "ocp")
                .addFormField("newMethod", "condom")
                .addFormField("familyPlanningMethodChangeDate", "2011-01-01")
                .addFormField("numberOfCondomsSupplied", "20")
                .build();
        EligibleCouple expectedEc = new EligibleCouple("entity id 1", "EC Number 1")
                .withDetails(create("currentMethod", "condom")
                        .put("caste", "c_others")
                        .map())
                .withIUDFPDetails(Collections.<IUDFPDetails>emptyList())
                .withOCPFPDetails(asList(new OCPFPDetails(
                        "2010-06-01", asList(create("date", "2011-06-01").put("quantity", "20").map()), "2011-06-01", "negative")))
                .withFemaleSterilizationFPDetails(Collections.<SterilizationFPDetails>emptyList())
                .withMaleSterilizationFPDetails(Collections.<SterilizationFPDetails>emptyList())
                .withCondomFPDetails(asList(new CondomFPDetails(
                        "2010-01-01", asList(create("date", "2010-01-01").put("quantity", "20").map())),
                        new CondomFPDetails("2011-01-01",
                                asList(create("date", "2011-01-01").put("quantity", "20").map()))));

        ecService.reportFPChange(submission);

        verify(allEligibleCouples).update(expectedEc);
    }

    @Test
    public void shouldSendDataToReportingServiceDuringReportFPChange() throws Exception {
        EligibleCouple ec = new EligibleCouple("entity id 1", "EC Number 1").withDetails(create("caste", "sc").put("economicStatus", "bpl").map())
                .withIUDFPDetails(new ArrayList<IUDFPDetails>())
                .withCondomFPDetails(new ArrayList<CondomFPDetails>())
                .withOCPFPDetails(new ArrayList<OCPFPDetails>())
                .withFemaleSterilizationFPDetails(new ArrayList<SterilizationFPDetails>())
                .withMaleSterilizationFPDetails(new ArrayList<SterilizationFPDetails>());

        when(allEligibleCouples.findByCaseId("entity id 1")).thenReturn(ec);
        FormSubmission submission = FormSubmissionBuilder.create()
                .withFormName("fp_change")
                .addFormField("someKey", "someValue")
                .addFormField("newMethod", "condom")
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
    public void shouldUpdateECAndECSchedulesWhenFPFollowupOccurs() throws Exception {
        ArrayList<SterilizationFPDetails> maleSterilizationFPDetails = new ArrayList<>();
        maleSterilizationFPDetails.add(new MaleSterilizationFPDetails("nsv", "2010-01-01", null));
        List<String> followUpVisitDates = new ArrayList<>();
        followUpVisitDates.add("2010-10-20");
        maleSterilizationFPDetails.add(new MaleSterilizationFPDetails("nsv", "2010-10-01", followUpVisitDates));
        EligibleCouple ec = new EligibleCouple("entity id 1", "EC Number 1")
                .withIUDFPDetails(Collections.<IUDFPDetails>emptyList())
                .withCondomFPDetails(Collections.<CondomFPDetails>emptyList())
                .withOCPFPDetails(Collections.<OCPFPDetails>emptyList())
                .withMaleSterilizationFPDetails(maleSterilizationFPDetails)
                .withFemaleSterilizationFPDetails(Collections.<SterilizationFPDetails>emptyList());
        when(allEligibleCouples.findByCaseId("entity id 1")).thenReturn(ec);
        FormSubmission submission = FormSubmissionBuilder.create()
                .withFormName("fp_followup")
                .withANMId("anm id 1")
                .addFormField("currentMethod", "male_sterilization")
                .addFormField("submissionDate", "2011-01-01")
                .addFormField("fpFollowupDate", "2010-12-20")
                .addFormField("needsFollowup", "yes")
                .addFormField("needsReferralFollowup", "no")
                .build();

        ecService.handleFPFollowup(submission);

        ArrayList<SterilizationFPDetails> expectedMaleSterilizationDetails = new ArrayList<>();
        expectedMaleSterilizationDetails.add(new MaleSterilizationFPDetails("nsv", "2010-01-01", null));
        expectedMaleSterilizationDetails.add(new MaleSterilizationFPDetails("nsv", "2010-10-01", asList("2010-10-20", "2010-12-20")));
        EligibleCouple expectedEC = new EligibleCouple("entity id 1", "EC Number 1")
                .withMaleSterilizationFPDetails(expectedMaleSterilizationDetails)
                .withIUDFPDetails(Collections.<IUDFPDetails>emptyList())
                .withCondomFPDetails(Collections.<CondomFPDetails>emptyList())
                .withOCPFPDetails(Collections.<OCPFPDetails>emptyList())
                .withFemaleSterilizationFPDetails(Collections.<SterilizationFPDetails>emptyList());
        verify(schedulingService).fpFollowup(new FPProductInformation("entity id 1", "anm id 1", "male_sterilization", null, null, null, null, null, "2011-01-01", null, "2010-12-20", "yes", "no"));
        verify(allEligibleCouples).update(eq(expectedEC));
    }

    @Test
    public void shouldUpdateECAndECSchedulesWhenFPFollowupOccursForFemaleSterilization() throws Exception {
        ArrayList<SterilizationFPDetails> femaleSterilizationFPDetails = new ArrayList<>();
        femaleSterilizationFPDetails.add(new FemaleSterilizationFPDetails("minilap", "2010-01-01", null));
        List<String> followUpVisitDates = new ArrayList<>();
        followUpVisitDates.add("2010-10-20");
        femaleSterilizationFPDetails.add(new FemaleSterilizationFPDetails("minilap", "2010-10-01", followUpVisitDates));
        EligibleCouple ec = new EligibleCouple("entity id 1", "EC Number 1")
                .withIUDFPDetails(Collections.<IUDFPDetails>emptyList())
                .withCondomFPDetails(Collections.<CondomFPDetails>emptyList())
                .withOCPFPDetails(Collections.<OCPFPDetails>emptyList())
                .withMaleSterilizationFPDetails(Collections.<SterilizationFPDetails>emptyList())
                .withFemaleSterilizationFPDetails(femaleSterilizationFPDetails);
        when(allEligibleCouples.findByCaseId("entity id 1")).thenReturn(ec);
        FormSubmission submission = FormSubmissionBuilder.create()
                .withFormName("fp_followup")
                .withANMId("anm id 1")
                .addFormField("currentMethod", "female_sterilization")
                .addFormField("submissionDate", "2011-01-01")
                .addFormField("fpFollowupDate", "2010-12-20")
                .addFormField("needsFollowup", "yes")
                .addFormField("needsReferralFollowup", "no")
                .build();

        ecService.handleFPFollowup(submission);

        List<SterilizationFPDetails> expectedFemaleSterilizationDetails = new ArrayList<>();
        expectedFemaleSterilizationDetails.add(new FemaleSterilizationFPDetails("minilap", "2010-01-01", null));
        expectedFemaleSterilizationDetails.add(new FemaleSterilizationFPDetails("minilap", "2010-10-01", asList("2010-10-20", "2010-12-20")));
        EligibleCouple expectedEC = new EligibleCouple("entity id 1", "EC Number 1")
                .withMaleSterilizationFPDetails(Collections.<SterilizationFPDetails>emptyList())
                .withIUDFPDetails(Collections.<IUDFPDetails>emptyList())
                .withCondomFPDetails(Collections.<CondomFPDetails>emptyList())
                .withOCPFPDetails(Collections.<OCPFPDetails>emptyList())
                .withFemaleSterilizationFPDetails(femaleSterilizationFPDetails);
        verify(schedulingService).fpFollowup(new FPProductInformation("entity id 1", "anm id 1", "female_sterilization", null, null, null, null, null, "2011-01-01", null, "2010-12-20", "yes", "no"));
        verify(allEligibleCouples).update(eq(expectedEC));
    }

    @Test
    public void shouldNotDoAnythingDuringFPFollowupWhenNoECIsFound() throws Exception {
        when(allEligibleCouples.findByCaseId("entity id 1")).thenReturn(null);
        FormSubmission submission = FormSubmissionBuilder.create().build();

        ecService.handleFPFollowup(submission);

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

        ecService.handleReferralFollowup(submission);

        verify(allEligibleCouples).findByCaseId("entity id 1");
        verifyZeroInteractions(reportingService);
        verifyZeroInteractions(schedulingService);
    }

    @Test
    public void shouldUpdateECSchedulesWhenFPReferralFollowupIsReported() throws Exception {
        ArrayList<SterilizationFPDetails> maleSterilizationFPDetails = new ArrayList<>();
        maleSterilizationFPDetails.add(new MaleSterilizationFPDetails("nsv", "2010-01-01", null));
        List<String> followUpVisitDates = new ArrayList<>();
        followUpVisitDates.add("2010-10-20");
        maleSterilizationFPDetails.add(new MaleSterilizationFPDetails("nsv", "2010-10-01", followUpVisitDates));
        EligibleCouple ec = new EligibleCouple("entity id 1", "EC Number 1")
                .withIUDFPDetails(Collections.<IUDFPDetails>emptyList())
                .withCondomFPDetails(Collections.<CondomFPDetails>emptyList())
                .withOCPFPDetails(Collections.<OCPFPDetails>emptyList())
                .withMaleSterilizationFPDetails(maleSterilizationFPDetails)
                .withFemaleSterilizationFPDetails(Collections.<SterilizationFPDetails>emptyList());
        when(allEligibleCouples.findByCaseId("entity id 1")).thenReturn(ec);
        FormSubmission submission = FormSubmissionBuilder.create()
                .withFormName("fp_referral_followup")
                .withANMId("anm id 1")
                .addFormField("currentMethod", "male_sterilization")
                .addFormField("referralFollowupDate", "2010-12-20")
                .addFormField("submissionDate", "2010-12-24")
                .addFormField("needsFollowup", "yes")
                .addFormField("needsReferralFollowup", "no")
                .build();

        ecService.handleReferralFollowup(submission);

        ArrayList<SterilizationFPDetails> expectedMaleSterilizationDetails = new ArrayList<>();
        expectedMaleSterilizationDetails.add(new MaleSterilizationFPDetails("nsv", "2010-01-01", null));
        expectedMaleSterilizationDetails.add(new MaleSterilizationFPDetails("nsv", "2010-10-01", asList("2010-10-20", "2010-12-20")));
        EligibleCouple expectedEC = new EligibleCouple("entity id 1", "EC Number 1")
                .withMaleSterilizationFPDetails(expectedMaleSterilizationDetails)
                .withIUDFPDetails(Collections.<IUDFPDetails>emptyList())
                .withCondomFPDetails(Collections.<CondomFPDetails>emptyList())
                .withOCPFPDetails(Collections.<OCPFPDetails>emptyList())
                .withFemaleSterilizationFPDetails(Collections.<SterilizationFPDetails>emptyList());
        verify(schedulingService).reportReferralFollowup(new FPProductInformation("entity id 1", "anm id 1", null, null, null, null, null, null, "2010-12-24", null, "2010-12-20", "yes", "no"));
        verify(allEligibleCouples).update(eq(expectedEC));
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
