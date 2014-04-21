package org.ei.drishti.service;

import org.ei.drishti.common.util.EasyMap;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.domain.register.*;
import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.repository.AllChildren;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.repository.AllMothers;
import org.ei.drishti.service.formSubmission.handler.ReportFieldsDefinition;
import org.ei.drishti.service.reporting.ChildReportingService;
import org.ei.drishti.service.reporting.MotherReportingService;
import org.ei.drishti.service.scheduling.ChildSchedulesService;
import org.ei.drishti.service.scheduling.PNCSchedulesService;
import org.ei.drishti.util.SafeMap;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.ei.drishti.common.util.EasyMap.mapOf;
import static org.ei.drishti.util.FormSubmissionBuilder.create;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class PNCServiceTest extends BaseUnitTest {
    @Mock
    private ActionService actionService;
    @Mock
    private ChildSchedulesService childSchedulesService;
    @Mock
    private AllEligibleCouples allEligibleCouples;
    @Mock
    private AllMothers allMothers;
    @Mock
    private AllChildren children;
    @Mock
    private MotherReportingService motherReportingService;
    @Mock
    private ChildReportingService childReportingService;
    @Mock
    private PNCSchedulesService pncSchedulesService;
    @Mock
    private ReportFieldsDefinition reportFieldsDefinition;
    private PNCService service;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        service = new PNCService(actionService, pncSchedulesService, allEligibleCouples, allMothers, children,
                motherReportingService, reportFieldsDefinition);
    }

    @Test
    public void shouldEnrollPNCIntoSchedulesDuringDeliveryOutcomeIfWomanOrMotherSurvives() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        when(allMothers.findByCaseId("mother id 1")).thenReturn(new Mother("mother id 1", "ec id 1", "1234567"));
        FormSubmission submission = create()
                .withFormName("delivery_outcome")
                .withANMId("anm id 1")
                .withEntityId("mother id 1")
                .addFormField("referenceDate", "2012-01-01")
                .addFormField("didWomanSurvive", "yes")
                .addFormField("didMotherSurvive", "")
                .build();
        service.deliveryOutcome(submission);

        verify(pncSchedulesService).deliveryOutcome("mother id 1", "2012-01-01");
    }

    @Test
    public void shouldNotEnrollPNCIntoSchedulesDuringDeliveryOutcomeIfMotherDoesNotExists() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        when(allMothers.exists("mother id 1")).thenReturn(false);
        FormSubmission submission = create()
                .withFormName("delivery_outcome")
                .withANMId("anm id 1")
                .withEntityId("mother id 1")
                .addFormField("referenceDate", "2012-01-01")
                .addFormField("didWomanSurvive", "no")
                .build();
        service.deliveryOutcome(submission);

        verifyZeroInteractions(pncSchedulesService);
    }

    @Test
    public void shouldNotEnrollPNCIntoSchedulesDuringDeliveryOutcomeIfWomanDied() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        when(allMothers.exists("mother id 1")).thenReturn(true);
        FormSubmission submission = create()
                .withFormName("delivery_outcome")
                .withANMId("anm id 1")
                .withEntityId("mother id 1")
                .addFormField("referenceDate", "2012-01-01")
                .addFormField("didWomanSurvive", "no")
                .addFormField("didMotherSurvive", "")
                .build();
        service.deliveryOutcome(submission);

        verifyZeroInteractions(pncSchedulesService);
    }

    @Test
    public void shouldNotEnrollPNCIntoSchedulesDuringDeliveryOutcomeIfMotherDied() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        when(allMothers.exists("mother id 1")).thenReturn(true);
        FormSubmission submission = create()
                .withFormName("delivery_outcome")
                .withANMId("anm id 1")
                .withEntityId("mother id 1")
                .addFormField("referenceDate", "2012-01-01")
                .addFormField("didWomanSurvive", "")
                .addFormField("didMotherSurvive", "no")
                .build();
        service.deliveryOutcome(submission);

        verifyZeroInteractions(pncSchedulesService);
    }

    @Test
    public void shouldCloseMotherAndECDuringDeliveryOutcomeIfMotherDied() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        when(allMothers.exists("mother id 1")).thenReturn(true);
        when(allMothers.findByCaseId("mother id 1")).thenReturn(new Mother("mother id 1", "ec id 1", "1234567"));
        when(allEligibleCouples.findByCaseId("ec id 1")).thenReturn(new EligibleCouple("ec id 1", "123"));

        FormSubmission submission = create()
                .withFormName("delivery_outcome")
                .withANMId("anm id 1")
                .withEntityId("mother id 1")
                .addFormField("referenceDate", "2012-01-01")
                .addFormField("didWomanSurvive", "")
                .addFormField("didMotherSurvive", "no")
                .build();
        service.deliveryOutcome(submission);

        Mother expectedMother = new Mother("mother id 1", "ec id 1", "1234567").setIsClosed(true);
        EligibleCouple expectedEC = new EligibleCouple("ec id 1", "123").setIsClosed(true);

        verify(allMothers).update(expectedMother);
        verify(allEligibleCouples).update(expectedEC);
        verify(actionService).markAllAlertsAsInactive("mother id 1");
        verify(pncSchedulesService).unEnrollFromSchedules("mother id 1");
        verifyZeroInteractions(pncSchedulesService);
    }

    @Test
    public void shouldCloseMotherAndECDuringDeliveryOutcomeIfWomanDied() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        when(allMothers.exists("mother id 1")).thenReturn(true);
        when(allMothers.findByCaseId("mother id 1")).thenReturn(new Mother("mother id 1", "ec id 1", "1234567"));
        when(allEligibleCouples.findByCaseId("ec id 1")).thenReturn(new EligibleCouple("ec id 1", "123"));

        FormSubmission submission = create()
                .withFormName("delivery_outcome")
                .withANMId("anm id 1")
                .withEntityId("mother id 1")
                .addFormField("referenceDate", "2012-01-01")
                .addFormField("didWomanSurvive", "")
                .addFormField("didMotherSurvive", "no")
                .build();
        service.deliveryOutcome(submission);

        Mother expectedMother = new Mother("mother id 1", "ec id 1", "1234567").setIsClosed(true);
        EligibleCouple expectedEC = new EligibleCouple("ec id 1", "123").setIsClosed(true);

        verify(allMothers).update(expectedMother);
        verify(allEligibleCouples).update(expectedEC);
        verify(actionService).markAllAlertsAsInactive("mother id 1");
        verify(pncSchedulesService).unEnrollFromSchedules("mother id 1");
        verifyZeroInteractions(pncSchedulesService);
    }

    @Test
    public void shouldUpdateANMInformationOfMotherWhenOAPNCIsRegistered() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        when(allMothers.findByEcCaseId("ec id 1")).thenReturn(asList(new Mother("mother id 1", "ec id 1", "tc 1")));
        FormSubmission submission = create()
                .withFormName("pnc_registration_oa")
                .withANMId("anm id 1")
                .withEntityId("ec id 1")
                .addFormField("referenceDate", "2012-01-01")
                .addFormField("didWomanSurvive", "yes")
                .build();

        service.pncRegistrationOA(submission);

        verify(allMothers).update(new Mother("mother id 1", "ec id 1", "tc 1").withAnm("anm id 1"));
    }

    @Test
    public void shouldEnrollPNCIntoSchedulesDuringPNCRegistrationIfWomanSurvives() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        when(allMothers.findByEcCaseId("ec id 1")).thenReturn(asList(new Mother("mother id 1", "ec id 1", "tc 1")));
        FormSubmission submission = create()
                .withFormName("pnc_registration_oa")
                .withANMId("anm id 1")
                .withEntityId("ec id 1")
                .addFormField("referenceDate", "2012-01-01")
                .addFormField("didWomanSurvive", "yes")
                .build();

        service.pncRegistrationOA(submission);

        verify(pncSchedulesService).deliveryOutcome("mother id 1", "2012-01-01");
    }

    @Test
    public void shouldNotEnrollPNCIntoSchedulesDuringPNCRegistrationIfMotherDoesNotExists() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        when(allMothers.findByEcCaseId("ec id 1")).thenReturn(Collections.EMPTY_LIST);
        FormSubmission submission = create()
                .withFormName("pnc_registration_oa")
                .withANMId("anm id 1")
                .withEntityId("ec id 1")
                .addFormField("referenceDate", "2012-01-01")
                .addFormField("didWomanSurvive", "")
                .build();

        service.pncRegistrationOA(submission);

        verifyZeroInteractions(pncSchedulesService);
    }

    @Test
    public void shouldNotEnrollPNCIntoSchedulesPNCRegistrationIfWomanDied() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        when(allMothers.findByEcCaseId("ec id 1")).thenReturn(asList(new Mother("mother id 1", "ec id 1", "tc 1")));
        FormSubmission submission = create()
                .withFormName("pnc_registration_oa")
                .withANMId("anm id 1")
                .withEntityId("ec id 1")
                .addFormField("referenceDate", "2012-01-01")
                .addFormField("didWomanSurvive", "no")
                .build();

        service.pncRegistrationOA(submission);

        verifyZeroInteractions(pncSchedulesService);
    }

    @Test
    public void shouldUnEnrollAMotherFromScheduleWhenPNCCaseIsClosed() {
        when(allMothers.findByCaseId("entity id 1")).thenReturn(new Mother("entity id 1", "ec entity id 1", "thayi 1"));

        service.close(create().build());

        verify(pncSchedulesService).unEnrollFromSchedules("entity id 1");
    }

    @Test
    public void shouldCloseAMotherWhenPNCIsClosed() {
        when(allMothers.findByCaseId("entity id 1")).thenReturn(new Mother("entity id 1", "ec entity id 1", "thayi 1"));

        service.close(create().build());

        verify(allMothers).close("entity id 1");
    }

    @Test
    public void shouldNotDoAnythingIfMotherIsNotRegistered() {
        when(allMothers.findByCaseId("entity id 1")).thenReturn(null);

        service.close(create().build());

        verifyZeroInteractions(pncSchedulesService);
        verifyZeroInteractions(allEligibleCouples);
        verifyZeroInteractions(motherReportingService);
        verifyZeroInteractions(actionService);
    }

    @Test
    public void shouldCloseECCaseAlsoWhenPNCIsClosedAndReasonIsDeath() {
        when(allMothers.findByCaseId("entity id 1")).thenReturn(new Mother("entity id 1", "ec entity id 1", "thayi 1"));

        service.close(create().addFormField("closeReason", "death_of_mother").build());

        verify(allEligibleCouples).close("ec entity id 1");
    }

    @Test
    public void shouldCloseECCaseAlsoWhenPNCIsClosedAndReasonIsPermanentRelocation() {
        when(allMothers.findByCaseId("entity id 1")).thenReturn(new Mother("entity id 1", "ec entity id 1", "thayi 1"));

        service.close(create().addFormField("closeReason", "permanent_relocation").build());

        verify(allEligibleCouples).close("ec entity id 1");
    }

    @Test
    public void shouldNotCloseECCaseWhenPNCIsClosedAndReasonIsNeitherDeathOrPermanentRelocation() {
        when(allMothers.findByCaseId("entity id 1")).thenReturn(new Mother("entity id 1", "ec entity id 1", "thayi 1"));

        service.close(create().addFormField("closeReason", "other_reason").build());

        verifyZeroInteractions(allEligibleCouples);
    }

    @Test
    public void shouldMarkAllActionsAsInactiveWhenPNCIsClosed() {
        when(allMothers.findByCaseId("entity id 1")).thenReturn(new Mother("entity id 1", "ec entity id 1", "thayi 1"));

        service.close(create().build());

        verify(actionService).markAllAlertsAsInactive("entity id 1");
    }

    @Test
    public void shouldDoReportingWhenPNCIsClosed() {
        when(allMothers.findByCaseId("entity id 1")).thenReturn(new Mother("entity id 1", "ec entity id 1", "thayi 1"));
        when(reportFieldsDefinition.get("pnc_close")).thenReturn(asList("some-key"));
        FormSubmission submission = create()
                .withFormName("pnc_close")
                .addFormField("some-key", "some-value")
                .build();

        service.close(submission);

        verify(motherReportingService).closePNC(new SafeMap(mapOf("some-key", "some-value")));
    }

    @Test
    public void shouldNotDoAnythingIfMotherIsNotFoundDuringPNCVisit() throws Exception {
        FormSubmission submission = create()
                .withFormName("pnc_visit")
                .build();

        when(allMothers.exists("entity id 1")).thenReturn(false);

        service.pncVisitHappened(submission);

        verify(allMothers).findByCaseId("entity id 1");
        verifyZeroInteractions(motherReportingService);
        verifyZeroInteractions(childReportingService);
    }

    @Test
    public void shouldReportPNCVisit() throws Exception {
        when(reportFieldsDefinition.get("pnc_visit")).thenReturn(asList("some-key"));
        when(allMothers.findByCaseId("entity id 1")).thenReturn(new Mother("mother id 1", "ec id 1", "TC1"));

        FormSubmission submission = create()
                .withFormName("pnc_visit")
                .addFormField("some-key", "value")
                .build();
        service.pncVisitHappened(submission);

        SafeMap reportFields = new SafeMap(mapOf("some-key", "value"));
        verify(motherReportingService).pncVisitHappened(reportFields);
    }

    @Test
    public void shouldMaintainAHistoryOfPNCVisitsThatHappened() throws Exception {
        when(reportFieldsDefinition.get("pnc_visit")).thenReturn(asList("pncVisitDate"));
        Mother mother = new Mother("mother id 1", "ec id 1", "TC1")
                .withDetails(EasyMap.mapOf("some-key", "some-value"));
        when(allMothers.findByCaseId("entity id 1")).thenReturn(mother);

        FormSubmission submission = create()
                .withFormName("pnc_visit")
                .addFormField("pncVisitDate", "2013-01-01")
                .build();
        service.pncVisitHappened(submission);

        Mother updatedMother = new Mother("mother id 1", "ec id 1", "TC1")
                .withDetails(EasyMap.create("some-key", "some-value")
                        .put("pncVisitDates", "2013-01-01").map());
        verify(allMothers).update(updatedMother);
    }

    @Test
    public void shouldAddNewPNCVisitDateToPNCVisitsDatesThatHappened() throws Exception {
        when(reportFieldsDefinition.get("pnc_visit")).thenReturn(asList("pncVisitDate"));
        Mother mother = new Mother("mother id 1", "ec id 1", "TC1")
                .withDetails(EasyMap.create("some-key", "some-value").put("pncVisitDates", "2013-01-01").map());
        when(allMothers.findByCaseId("entity id 1")).thenReturn(mother);

        FormSubmission submission = create()
                .withFormName("pnc_visit")
                .addFormField("pncVisitDate", "2013-01-02")
                .build();
        service.pncVisitHappened(submission);

        Mother updatedMother = new Mother("mother id 1", "ec id 1", "TC1").withDetails(
                EasyMap.create("some-key", "some-value")
                        .put("pncVisitDates", "2013-01-01 2013-01-02")
                        .map());
        verify(allMothers).update(updatedMother);
    }

    @Test
    public void shouldAutoClosePNCCaseWhenMotherExists() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC1").withAnm("ANM 1"));

        service.autoClosePNCCase("MOTHER-CASE-1");

        verify(allMothers).close("MOTHER-CASE-1");
        verify(actionService).markAllAlertsAsInactive("MOTHER-CASE-1");
        verify(actionService).closeMother("MOTHER-CASE-1", "ANM 1", "Auto Close PNC");
        verifyZeroInteractions(motherReportingService);
    }

    @Test
    public void shouldNotAutoClosePNCCaseWhenMotherDoesNotExist() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(null);

        service.autoClosePNCCase("MOTHER-CASE-1");

        verify(allMothers, times(0)).close("MOTHER-CASE-1");
        verifyZeroInteractions(actionService);
    }

    @Test
    public void shouldReportPNCRegistrationOA() throws Exception {
        when(reportFieldsDefinition.get("pnc_registration_oa")).thenReturn(asList("some-key"));
        when(allMothers.findByEcCaseId("entity id 1")).thenReturn(asList(new Mother("entity id 1", "ec id 1", "tc 1")));

        FormSubmission submission = create()
                .withFormName("pnc_registration_oa")
                .addFormField("some-key", "value")
                .build();
        service.pncRegistrationOA(submission);

        SafeMap reportFields = new SafeMap(mapOf("some-key", "value"));
        verify(motherReportingService).pncRegistrationOA(reportFields);
    }

    @Test
    public void shouldUpdateMotherWithNewFamilyPlanningWhenPPFPIsSubmitted() throws Exception {
        FormSubmission submission = create()
                .withFormName("postpartum_family_planning")
                .withEntityId("mother id 1")
                .addFormField("some-key", "value")
                .addFormField("ppFPMethod1", "female_sterilization")
                .addFormField("ppFPMethod1StartDate", "2010-01-01")
//                .addFormField("iudPlace1", "phc")
                .addFormField("femaleSterilizationType1", "minilap")
//                .addFormField("ppFPMethod2", "phc")
//                .addFormField("ppFPMethod2StartDate", "phc")
//                .addFormField("iudPlace2", "phc")
//                .addFormField("femaleSterilizationType2", "phc")
//                .addFormField("maleSterilizationType", "phc")
//                .addFormField("numberOfCondomsSupplied", "phc")
//                .addFormField("numberOfOCPDelivered", "phc")
                .build();
        Mother mother = new Mother("mother id 1", "ec id 1", "thayi card number 1");
        EligibleCouple eligibleCouple = new EligibleCouple("ec id 1", "ec number 1")
                .withIUDFPDetails(Collections.<IUDFPDetails>emptyList())
                .withCondomFPDetails(Collections.<CondomFPDetails>emptyList())
                .withOCPFPDetails(Collections.<OCPFPDetails>emptyList())
                .withFemaleSterilizationFPDetails(new ArrayList<SterilizationFPDetails>())
                .withMaleSterilizationFPDetails(Collections.<SterilizationFPDetails>emptyList());
        when(allMothers.findByCaseId("mother id 1")).thenReturn(mother);
        when(allEligibleCouples.findByCaseId("ec id 1")).thenReturn(eligibleCouple);

        service.reportPPFamilyPlanning(submission);

        List<SterilizationFPDetails> femaleSterilizationFPDetails = new ArrayList<>();
        femaleSterilizationFPDetails.add(new FemaleSterilizationFPDetails("minilap", "2010-01-01"));
        EligibleCouple expectedEC = eligibleCouple.withFemaleSterilizationFPDetails(femaleSterilizationFPDetails);
        verify(allEligibleCouples).update(expectedEC);
    }

    @Test
    public void shouldUpdateMotherWithNewFamilyPlanningIUDWhenPPFPIsSubmitted() throws Exception {
        FormSubmission submission = create()
                .withFormName("postpartum_family_planning")
                .withEntityId("mother id 1")
                .addFormField("some-key", "value")
                .addFormField("ppFPMethod1", "iud")
                .addFormField("ppFPMethod1StartDate", "2010-01-01")
                .addFormField("iudPlace1", "phc")
                .build();
        Mother mother = new Mother("mother id 1", "ec id 1", "thayi card number 1");
        EligibleCouple eligibleCouple = new EligibleCouple("ec id 1", "ec number 1")
                .withIUDFPDetails(new ArrayList<IUDFPDetails>())
                .withCondomFPDetails(Collections.<CondomFPDetails>emptyList())
                .withOCPFPDetails(Collections.<OCPFPDetails>emptyList())
                .withFemaleSterilizationFPDetails(Collections.<SterilizationFPDetails>emptyList())
                .withMaleSterilizationFPDetails(Collections.<SterilizationFPDetails>emptyList());
        when(allMothers.findByCaseId("mother id 1")).thenReturn(mother);
        when(allEligibleCouples.findByCaseId("ec id 1")).thenReturn(eligibleCouple);

        service.reportPPFamilyPlanning(submission);

        ArrayList<IUDFPDetails> iudFPDetails = new ArrayList<>();
        iudFPDetails.add(new IUDFPDetails("2010-01-01", "phc", null, null));
        EligibleCouple expectedEC = eligibleCouple.withIUDFPDetails(iudFPDetails);
        verify(allEligibleCouples).update(expectedEC);
    }

    @Test
    public void shouldUpdateMotherWithNewFamilyPlanningFSWhenPPFPIsSubmitted() throws Exception {
        FormSubmission submission = create()
                .withFormName("postpartum_family_planning")
                .withEntityId("mother id 1")
                .addFormField("some-key", "value")
                .addFormField("ppFPMethod2", "female_sterilization")
                .addFormField("ppFPMethod2StartDate", "2010-01-01")
                .addFormField("femaleSterilizationType2", "minilap")
                .build();
        Mother mother = new Mother("mother id 1", "ec id 1", "thayi card number 1");
        EligibleCouple eligibleCouple = new EligibleCouple("ec id 1", "ec number 1")
                .withIUDFPDetails(Collections.<IUDFPDetails>emptyList())
                .withCondomFPDetails(Collections.<CondomFPDetails>emptyList())
                .withOCPFPDetails(Collections.<OCPFPDetails>emptyList())
                .withFemaleSterilizationFPDetails(new ArrayList<SterilizationFPDetails>())
                .withMaleSterilizationFPDetails(Collections.<SterilizationFPDetails>emptyList());
        when(allMothers.findByCaseId("mother id 1")).thenReturn(mother);
        when(allEligibleCouples.findByCaseId("ec id 1")).thenReturn(eligibleCouple);

        service.reportPPFamilyPlanning(submission);

        ArrayList<SterilizationFPDetails> femaleSterilizationFPDetails = new ArrayList<>();
        femaleSterilizationFPDetails.add(new FemaleSterilizationFPDetails("minilap", "2010-01-01"));
        EligibleCouple expectedEC = new EligibleCouple("ec id 1", "ec number 1")
                .withIUDFPDetails(Collections.<IUDFPDetails>emptyList())
                .withCondomFPDetails(Collections.<CondomFPDetails>emptyList())
                .withOCPFPDetails(Collections.<OCPFPDetails>emptyList())
                .withMaleSterilizationFPDetails(Collections.<SterilizationFPDetails>emptyList())
                .withFemaleSterilizationFPDetails(femaleSterilizationFPDetails);
        verify(allEligibleCouples).update(expectedEC);
    }

    @Test
    public void shouldUpdateMotherWithNewFamilyPlanningCondomWhenPPFPIsSubmitted() throws Exception {
        FormSubmission submission = create()
                .withFormName("postpartum_family_planning")
                .withEntityId("mother id 1")
                .addFormField("some-key", "value")
                .addFormField("ppFPMethod2", "condom")
                .addFormField("ppFPMethod2StartDate", "2010-01-01")
                .addFormField("numberOfCondomsSupplied", "20")
                .build();
        Mother mother = new Mother("mother id 1", "ec id 1", "thayi card number 1");
        EligibleCouple eligibleCouple = new EligibleCouple("ec id 1", "ec number 1")
                .withIUDFPDetails(Collections.<IUDFPDetails>emptyList())
                .withCondomFPDetails(new ArrayList<CondomFPDetails>())
                .withOCPFPDetails(Collections.<OCPFPDetails>emptyList())
                .withFemaleSterilizationFPDetails(Collections.<SterilizationFPDetails>emptyList())
                .withMaleSterilizationFPDetails(Collections.<SterilizationFPDetails>emptyList());
        when(allMothers.findByCaseId("mother id 1")).thenReturn(mother);
        when(allEligibleCouples.findByCaseId("ec id 1")).thenReturn(eligibleCouple);

        service.reportPPFamilyPlanning(submission);

        ArrayList<CondomFPDetails> condomFPDetails = new ArrayList<>();
        condomFPDetails.add(new CondomFPDetails("2010-01-01", asList(EasyMap.create("date", "2010-01-01").put("quantity", "20").map())));
        EligibleCouple expectedEC = new EligibleCouple("ec id 1", "ec number 1")
                .withIUDFPDetails(Collections.<IUDFPDetails>emptyList())
                .withCondomFPDetails(Collections.<CondomFPDetails>emptyList())
                .withOCPFPDetails(Collections.<OCPFPDetails>emptyList())
                .withMaleSterilizationFPDetails(Collections.<SterilizationFPDetails>emptyList())
                .withFemaleSterilizationFPDetails(Collections.<SterilizationFPDetails>emptyList())
                .withCondomFPDetails(condomFPDetails);
        verify(allEligibleCouples).update(expectedEC);
    }


}
