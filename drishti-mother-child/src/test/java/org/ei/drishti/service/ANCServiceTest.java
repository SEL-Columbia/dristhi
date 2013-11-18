package org.ei.drishti.service;

import org.ei.drishti.domain.Mother;
import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.repository.AllMothers;
import org.ei.drishti.service.formSubmission.handler.ReportFieldsDefinition;
import org.ei.drishti.service.reporting.MotherReportingService;
import org.ei.drishti.service.scheduling.ANCSchedulesService;
import org.ei.drishti.common.util.EasyMap;
import org.ei.drishti.util.SafeMap;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Map;

import static java.util.Arrays.asList;
import static org.ei.drishti.common.util.DateUtil.today;
import static org.ei.drishti.common.util.EasyMap.create;
import static org.ei.drishti.common.util.EasyMap.mapOf;
import static org.ei.drishti.util.FormSubmissionBuilder.create;
import static org.joda.time.LocalDate.parse;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ANCServiceTest {
    @Mock
    private ActionService actionService;
    @Mock
    private AllMothers allMothers;
    @Mock
    private AllEligibleCouples eligibleCouples;
    @Mock
    private ANCSchedulesService ancSchedulesService;
    @Mock
    protected MotherReportingService motherReportingService;
    @Mock
    private ECService ecService;
    @Mock
    private ReportFieldsDefinition reportFieldsDefinition;

    private ANCService service;

    private Map<String, Map<String, String>> EXTRA_DATA = create("details", mapOf("someKey", "someValue")).put("reporting", mapOf("someKey", "someValue")).map();

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        service = new ANCService(allMothers, eligibleCouples, ancSchedulesService, actionService, motherReportingService, reportFieldsDefinition);
    }

    @Test
    public void shouldRegisterANC() {
        LocalDate lmp = today();

        FormSubmission submission = create()
                .withFormName("anc_registration")
                .withANMId("anm id 1")
                .withEntityId("ec id 1")
                .addFormField("motherId", "Mother 1")
                .addFormField("thayiCardNumber", "thayi 1")
                .addFormField("referenceDate", lmp.toString())
                .addFormField("someKey", "someValue")
                .build();

        Mother mother = new Mother("Mother 1", "ec id 1", "thayi 1").withLMP(lmp);
        when(eligibleCouples.exists("ec id 1")).thenReturn(true);
        when(allMothers.findByCaseId("Mother 1")).thenReturn(mother);
        when(reportFieldsDefinition.get("anc_registration")).thenReturn(asList("someKey"));

        service.registerANC(submission);

        verify(allMothers).update(mother.withAnm("anm id 1"));
        verify(motherReportingService).registerANC(new SafeMap(mapOf("someKey", "someValue")));
    }

    @Test
    public void shouldNotRegisterANCIfECIsNotFound() {
        FormSubmission submission = create()
                .withFormName("anc_registration")
                .withANMId("anm id 1")
                .withEntityId("ec id 1")
                .build();
        when(eligibleCouples.exists("ec id 1")).thenReturn(false);

        service.registerANC(submission);

        verifyZeroInteractions(allMothers);
        verifyZeroInteractions(ancSchedulesService);
        verifyZeroInteractions(motherReportingService);
    }

    @Test
    public void shouldEnrollMotherIntoDefaultScheduleDuringEnrollmentBasedOnLMP() {
        LocalDate lmp = today().minusDays(2);

        FormSubmission submission = create()
                .withFormName("anc_registration")
                .withANMId("anm id 1")
                .withEntityId("ec id 1")
                .addFormField("motherId", "Mother 1")
                .addFormField("referenceDate", lmp.toString())
                .addFormField("someKey", "someValue")
                .build();
        Mother mother = new Mother("Mother 1", "ec id 1", "thayi 1").withLMP(lmp);
        when(eligibleCouples.exists("ec id 1")).thenReturn(true);
        when(allMothers.findByCaseId("Mother 1")).thenReturn(mother);
        when(reportFieldsDefinition.get("anc_registration")).thenReturn(asList("someKey"));

        service.registerANC(submission);

        verify(ancSchedulesService).enrollMother(eq("Mother 1"), eq(lmp));
    }

    @Test
    public void shouldRegisterOutOfAreaANC() {
        LocalDate lmp = today();

        FormSubmission submission = create()
                .withFormName("anc_registration_oa")
                .withANMId("anm id 1")
                .withEntityId("ec id 1")
                .addFormField("motherId", "Mother 1")
                .addFormField("thayiCardNumber", "thayi 1")
                .addFormField("referenceDate", lmp.toString())
                .addFormField("someKey", "someValue")
                .addFormField("isOutOfArea", "true")
                .build();
        Mother mother = new Mother("Mother 1", "ec id 1", "thayi 1").withLMP(lmp);
        when(eligibleCouples.exists("ec id 1")).thenReturn(true);
        when(allMothers.findByCaseId("Mother 1")).thenReturn(mother);

        service.registerOutOfAreaANC(submission);

        verify(allMothers).update(mother.withAnm("anm id 1"));
        verify(ancSchedulesService).enrollMother(eq("Mother 1"), eq(lmp));
    }

    @Test
    public void shouldNotRegisterOutOfAreaANCIfECIsNotFound() {
        FormSubmission submission = create()
                .withFormName("anc_registration_oa")
                .withANMId("anm id 1")
                .withEntityId("ec id 1")
                .build();
        when(eligibleCouples.exists("ec id 1")).thenReturn(false);

        service.registerOutOfAreaANC(submission);

        verifyZeroInteractions(allMothers);
        verifyZeroInteractions(ancSchedulesService);
    }

    @Test
    public void shouldHandleANCVisit() {
        FormSubmission submission = create()
                .withFormName("anc_visit")
                .withANMId("anm id 1")
                .withEntityId("entity id 1")
                .addFormField("ancVisitDate", "2013-01-01")
                .addFormField("ancVisitNumber", "2")
                .addFormField("someKey", "someValue")
                .build();

        Mother mother = new Mother("entity id 1", "ec id 1", "TC1")
                .withDetails(EasyMap.create("someKey", "someValue").map());
        when(allMothers.findByCaseId("entity id 1")).thenReturn(mother);
        when(reportFieldsDefinition.get("anc_visit")).thenReturn(asList("someKey"));

        service.ancVisit(submission);

        verify(ancSchedulesService).ancVisitHasHappened("entity id 1", "anm id 1", 2, "2013-01-01");
        verify(motherReportingService).ancVisit(new SafeMap(mapOf("someKey", "someValue")));
    }

    @Test
    public void shouldHandleANCVisitAndUpdateHyperTensionDetectedForFirstTimeAsTrueWhenHyperTensionOccursForFirstTime() {
        FormSubmission submission = create()
                .withFormName("anc_visit")
                .withANMId("anm id 1")
                .withEntityId("mother id 1")
                .addFormField("thayiCardNumber", "TC1")
                .addFormField("ecCaseId", "ec id 1")
                .addFormField("ancVisitDate", "2013-01-01")
                .addFormField("ancVisitNumber", "2")
                .addFormField("someKey", "someValue")
                .addFormField("bpSystolic", "140")
                .addFormField("bpDiastolic", "90")
                .build();
        Mother mother = new Mother("mother id 1", "ec id 1", "TC1")
                .withDetails(EasyMap.create("someKey", "someValue").put("bpDiastolic", "90").put("bpSystolic", "140").map());
        when(allMothers.findByCaseId("mother id 1")).thenReturn(mother);
        when(reportFieldsDefinition.get("anc_visit")).thenReturn(asList("someKey", "bpSystolic", "bpDiastolic"));

        service.ancVisit(submission);

        verify(ancSchedulesService).ancVisitHasHappened("mother id 1", "anm id 1", 2, "2013-01-01");
        verify(motherReportingService).ancVisit(new SafeMap(create("someKey", "someValue").put("bpDiastolic", "90").put("bpSystolic", "140").map()));

        Mother updatedMother = new Mother("mother id 1", "ec id 1", "TC1").withDetails(
                EasyMap.create("someKey", "someValue")
                        .put("isHypertensionDetectedForFirstTime", "true")
                        .put("bpDiastolic", "90")
                        .put("bpSystolic", "140")
                        .map());
        verify(allMothers).update(updatedMother);
    }

    @Test
    public void shouldHandleANCVisitAndSetHyperTensionDetectedForFirstTimeAsFalseWhenHyperTensionOccursForSecondTime() {
        FormSubmission submission = create()
                .withFormName("anc_visit")
                .withANMId("anm id 1")
                .withEntityId("mother id 1")
                .addFormField("thayiCardNumber", "TC1")
                .addFormField("ecCaseId", "ec id 1")
                .addFormField("ancVisitDate", "2013-01-01")
                .addFormField("ancVisitNumber", "2")
                .addFormField("someKey", "someValue")
                .addFormField("bpSystolic", "140")
                .addFormField("bpDiastolic", "90")
                .build();
        Mother mother = new Mother("mother id 1", "ec id 1", "TC1")
                .withDetails(EasyMap.create("someKey", "someValue")
                        .put("bpDiastolic", "90")
                        .put("bpSystolic", "140")
                        .put("isHypertensionDetectedForFirstTime", "true")
                        .map());
        when(allMothers.findByCaseId("mother id 1")).thenReturn(mother);
        when(reportFieldsDefinition.get("anc_visit")).thenReturn(asList("someKey", "bpSystolic", "bpDiastolic"));

        service.ancVisit(submission);

        verify(ancSchedulesService).ancVisitHasHappened("mother id 1", "anm id 1", 2, "2013-01-01");
        verify(motherReportingService).ancVisit(new SafeMap(create("someKey", "someValue").put("bpDiastolic", "90").put("bpSystolic", "140").map()));

        Mother updatedMother = new Mother("mother id 1", "ec id 1", "TC1").withDetails(
                EasyMap.create("someKey", "someValue")
                        .put("isHypertensionDetectedForFirstTime", "false")
                        .put("bpDiastolic", "90")
                        .put("bpSystolic", "140")
                        .map());
        verify(allMothers).update(updatedMother);
    }

    @Test
    public void shouldHandleANCVisitAndShouldNotUpdateMotherWhenThereIsNoHyperTensionDetected() {
        FormSubmission submission = create()
                .withFormName("anc_visit")
                .withANMId("anm id 1")
                .withEntityId("mother id 1")
                .addFormField("thayiCardNumber", "TC1")
                .addFormField("ecCaseId", "ec id 1")
                .addFormField("ancVisitDate", "2013-01-01")
                .addFormField("ancVisitNumber", "2")
                .addFormField("someKey", "someValue")
                .addFormField("bpSystolic", "120")
                .addFormField("bpDiastolic", "80")
                .build();
        Mother mother = new Mother("mother id 1", "ec id 1", "TC1")
                .withDetails(EasyMap.create("someKey", "someValue").put("bpDiastolic", "80").put("bpSystolic", "120").map());
        when(allMothers.findByCaseId("mother id 1")).thenReturn(mother);
        when(reportFieldsDefinition.get("anc_visit")).thenReturn(asList("someKey", "bpSystolic", "bpDiastolic"));

        service.ancVisit(submission);

        verify(ancSchedulesService).ancVisitHasHappened("mother id 1", "anm id 1", 2, "2013-01-01");
        verify(motherReportingService).ancVisit(new SafeMap(create("someKey", "someValue").put("bpDiastolic", "80").put("bpSystolic", "120").map()));

        Mother updatedMother = new Mother("mother id 1", "ec id 1", "TC1").withDetails(
                EasyMap.create("someKey", "someValue")
                        .put("bpDiastolic", "80")
                        .put("bpSystolic", "120")
                        .map());
        verify(allMothers, never()).update(updatedMother);
    }

    @Test
    public void shouldNotHandleANCVisitIfMotherIsNotFound() {
        FormSubmission submission = create()
                .withFormName("anc_visit")
                .withANMId("anm id 1")
                .withEntityId("entity id 1")
                .build();

        when(allMothers.findByCaseId("entity id 1")).thenReturn(null);

        service.ancVisit(submission);

        verifyZeroInteractions(reportFieldsDefinition);
        verifyZeroInteractions(ancSchedulesService);
        verifyZeroInteractions(motherReportingService);
    }

    @Test
    public void shouldHandleTTProvided() {
        FormSubmission submission = create()
                .withFormName("tt_1")
                .withANMId("anm id 1")
                .withEntityId("entity id 1")
                .addFormField("ttDate", "2013-01-01")
                .addFormField("ttDose", "tt1")
                .addFormField("someKey", "someValue")
                .build();

        when(allMothers.exists("entity id 1")).thenReturn(true);
        when(reportFieldsDefinition.get("tt_1")).thenReturn(asList("someKey"));

        service.ttProvided(submission);

        verify(ancSchedulesService).ttVisitHasHappened("entity id 1", "anm id 1", "tt1", "2013-01-01");
        verify(motherReportingService).ttProvided(new SafeMap(mapOf("someKey", "someValue")));
    }

    @Test
    public void shouldHandleHbTest() {
        FormSubmission submission = create()
                .withFormName("tt_1")
                .withANMId("anm id 1")
                .withEntityId("entity id 1")
                .addFormField("hbTestDate", "2013-01-01")
                .addFormField("anaemicStatus", "Anaemic")
                .build();
        when(allMothers.findByCaseId("entity id 1")).thenReturn(new Mother("entity id 1", "ec entity id 1", "thayi 1").withLMP(parse("2012-01-01")));

        service.hbTest(submission);

        verify(ancSchedulesService).hbTestDone("entity id 1", "anm id 1", "2013-01-01", "Anaemic", parse("2012-01-01"));
    }

    @Test
    public void shouldHandleDeliveryOutcome() {
        FormSubmission submission = create()
                .withFormName("delivery_outcome")
                .withANMId("anm id 1")
                .withEntityId("entity id 1")
                .addFormField("someKey", "someValue")
                .build();
        when(allMothers.exists("entity id 1")).thenReturn(true);
        when(reportFieldsDefinition.get("delivery_outcome")).thenReturn(asList("someKey"));

        service.deliveryOutcome(submission);

        verify(ancSchedulesService).unEnrollFromSchedules("entity id 1");
        verify(motherReportingService).deliveryOutcome(new SafeMap(mapOf("someKey", "someValue")));
    }

    @Test
    public void shouldDoNothingIfMotherIsNotRegisteredWhileDeliveryOutcome() {
        when(allMothers.exists("entity id 1")).thenReturn(false);

        service.deliveryOutcome(create().build());

        verifyZeroInteractions(ancSchedulesService);
        verifyZeroInteractions(reportFieldsDefinition);
        verifyZeroInteractions(motherReportingService);
    }

    @Test
    public void shouldUnEnrollAMotherFromScheduleWhenANCCaseIsClosed() {
        when(allMothers.findByCaseId("entity id 1")).thenReturn(new Mother("entity id 1", "ec entity id 1", "thayi 1"));

        service.close(create().build());

        verify(ancSchedulesService).unEnrollFromSchedules("entity id 1");
    }

    @Test
    public void shouldCloseAMotherWhenANCIsClosed() {
        when(allMothers.findByCaseId("entity id 1")).thenReturn(new Mother("entity id 1", "ec entity id 1", "thayi 1"));

        service.close(create().build());

        verify(allMothers).close("entity id 1");
    }

    @Test
    public void shouldNotUnEnrollAMotherFromScheduleWhenSheIsNotRegistered() {
        when(allMothers.findByCaseId("entity id 1")).thenReturn(null);

        service.close(create().build());

        verifyZeroInteractions(ancSchedulesService);
    }

    @Test
    public void shouldCloseECCaseAlsoWhenANCIsClosedAndReasonIsDeath() {
        when(allMothers.findByCaseId("entity id 1")).thenReturn(new Mother("entity id 1", "ec entity id 1", "thayi 1"));

        service.close(create().addFormField("closeReason", "death_of_woman").build());

        verify(eligibleCouples).close("ec entity id 1");
    }

    @Test
    public void shouldCloseECCaseAlsoWhenANCIsClosedAndReasonIsPermanentRelocation() {
        when(allMothers.findByCaseId("entity id 1")).thenReturn(new Mother("entity id 1", "ec entity id 1", "thayi 1"));

        service.close(create().addFormField("closeReason", "relocation_permanent").build());

        verify(eligibleCouples).close("ec entity id 1");
    }

    @Test
    public void shouldNotCloseECCaseWhenANCIsClosedAndReasonIsNeitherDeathOrPermanentRelocation() {
        when(allMothers.findByCaseId("entity id 1")).thenReturn(new Mother("entity id 1", "ec entity id 1", "thayi 1"));

        service.close(create().addFormField("closeReason", "other_reason").build());

        verifyZeroInteractions(ecService);
    }

    @Test
    public void shouldMarkAllActionsAsInactiveWhenANCIsClosed() {
        when(allMothers.findByCaseId("entity id 1")).thenReturn(new Mother("entity id 1", "ec entity id 1", "thayi 1"));

        service.close(create().build());

        verify(actionService).markAllAlertsAsInactive("entity id 1");
    }

    @Test
    public void shouldUpdateIFASchedulesWhenNumberOfIFATabletsGivenIsMoreThanZero() {
        Mother mother = new Mother("entity id 1", "ec entity id 1", "thayi 1")
                .withDetails(create("some-key", "some-value").put("totalNumberOfIFATabletsGiven", "30").map());
        when(allMothers.findByCaseId("entity id 1")).thenReturn(mother);


        FormSubmission submission = create()
                .withFormName("ifa")
                .addFormField("numberOfIFATabletsGiven", "100")
                .addFormField("ifaTabletsDate", "2013-05-24")
                .build();
        service.ifaTabletsGiven(submission);

        verify(ancSchedulesService).ifaTabletsGiven("entity id 1", "anmId", "100", "2013-05-24");
    }

    @Test
    public void shouldNotDoAnythingWhenIFATabletsAreGivenForNonExistentEC() {
        when(allMothers.findByCaseId("entity id 1")).thenReturn(null);


        FormSubmission submission = create()
                .withFormName("ifa")
                .withEntityId("entity id 1")
                .addFormField("numberOfIFATabletsGiven", "0")
                .build();
        service.ifaTabletsGiven(submission);

        verifyZeroInteractions(ancSchedulesService);
    }

    @Test
    public void shouldCreateTotalNumberOfIFATabletsGivenWhenIFATabletsIsGivenForFirstTime() {
        Mother mother = new Mother("entity id 1", "ec entity id 1", "thayi 1")
                .withDetails(mapOf("some-key", "some-value"));
        when(allMothers.findByCaseId("entity id 1")).thenReturn(mother);

        FormSubmission submission = create()
                .withFormName("ifa")
                .withEntityId("entity id 1")
                .addFormField("numberOfIFATabletsGiven", "30")
                .build();
        service.ifaTabletsGiven(submission);

        Mother updatedMother = new Mother("entity id 1", "ec entity id 1", "thayi 1")
                .withDetails(create("some-key", "some-value").put("totalNumberOfIFATabletsGiven", "30").map());
        verify(allMothers).update(updatedMother);
    }

    @Test
    public void shouldMaintainTotalNumberOfIFATabletsGiven() {
        Mother mother = new Mother("entity id 1", "ec entity id 1", "thayi 1")
                .withDetails(create("some-key", "some-value").put("totalNumberOfIFATabletsGiven", "30").map());
        when(allMothers.findByCaseId("entity id 1")).thenReturn(mother);

        FormSubmission submission = create()
                .withFormName("ifa")
                .withEntityId("entity id 1")
                .addFormField("numberOfIFATabletsGiven", "60")
                .build();
        service.ifaTabletsGiven(submission);

        Mother updatedMother = new Mother("entity id 1", "ec entity id 1", "thayi 1")
                .withDetails(create("some-key", "some-value").put("totalNumberOfIFATabletsGiven", "90").map());
        verify(allMothers).update(updatedMother);
    }
}
