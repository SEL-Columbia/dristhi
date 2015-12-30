package org.opensrp.register.ziggy.service;

import static java.util.Arrays.asList;
import static org.joda.time.LocalDate.parse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.opensrp.common.util.DateUtil.today;
import static org.opensrp.common.util.EasyMap.create;
import static org.opensrp.common.util.EasyMap.mapOf;
import static org.opensrp.register.util.FormSubmissionBuilder.create;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.opensrp.form.domain.FormSubmission;
import org.opensrp.register.ziggy.domain.Mother;
import org.opensrp.register.ziggy.repository.AllMothers;
import org.opensrp.register.ziggy.scheduling.ANCSchedulesService;
import org.opensrp.register.ziggy.service.ANCService;

public class ANCServiceTest {
    @Mock
    private AllMothers allMothers;
    @Mock
    private ANCSchedulesService ancSchedulesService;

    private ANCService service;

    private Map<String, Map<String, String>> EXTRA_DATA = create("details", mapOf("someKey", "someValue")).put("reporting", mapOf("someKey", "someValue")).map();

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        service = new ANCService(allMothers, ancSchedulesService);
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
        when(allMothers.findByCaseId("Mother 1")).thenReturn(mother);

        service.registerANC(submission);

        verify(allMothers).update(mother.withAnm("anm id 1"));
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
        when(allMothers.findByCaseId("Mother 1")).thenReturn(mother);

        service.registerANC(submission);

        verify(ancSchedulesService).enrollMother(eq("Mother 1"), eq(lmp), eq(submission.instanceId()));
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
        when(allMothers.findByCaseId("Mother 1")).thenReturn(mother);

        service.registerOutOfAreaANC(submission);

        verify(allMothers).update(mother.withAnm("anm id 1"));
        verify(ancSchedulesService).enrollMother(eq("Mother 1"), eq(lmp), eq(submission.instanceId()));
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
                .addFormField("weight", "55")
                .addFormField("bpSystolic", "120")
                .addFormField("bpDiastolic", "80")
                .build();

        Mother mother = new Mother("entity id 1", "ec id 1", "TC1")
                .withDetails(mapOf("someKey", "someValue"));
        when(allMothers.findByCaseId("entity id 1")).thenReturn(mother);

        service.ancVisit(submission);

        verify(allMothers).update(mother
                .withANCVisits(asList(
                        create("ancVisitDate", "2013-01-01")
                                .put("weight", "55")
                                .put("bpSystolic", "120")
                                .put("bpDiastolic", "80")
                                .map()
                )));
        verify(ancSchedulesService).ancVisitHasHappened("entity id 1", "anm id 1", 2, "2013-01-01", submission.instanceId());
    }

    @Test
    public void shouldUpdateANCVisitInformationWhenItAlreadyExists() {
        FormSubmission submission = create()
                .withFormName("anc_visit")
                .withANMId("anm id 1")
                .withEntityId("entity id 1")
                .addFormField("ancVisitDate", "2013-01-01")
                .addFormField("ancVisitNumber", "2")
                .addFormField("someKey", "someValue")
                .addFormField("weight", "55")
                .addFormField("bpSystolic", "120")
                .addFormField("bpDiastolic", "80")
                .build();
        List<Map<String, String>> ancVisits = new ArrayList<>();
        ancVisits.add(create("ancVisitDate", "2012-09-01")
                .put("weight", "55")
                .put("bpSystolic", "121")
                .put("bpDiastolic", "81")
                .put("ancVisitNumber", "1")
                .map());
        Mother mother = new Mother("entity id 1", "ec id 1", "TC1")
                .withANCVisits(ancVisits)
                .withDetails(mapOf("someKey", "someValue"));
        when(allMothers.findByCaseId("entity id 1")).thenReturn(mother);

        service.ancVisit(submission);

        Mother updatedMother = new Mother("entity id 1", "ec id 1", "TC1")
                .withDetails(mapOf("someKey", "someValue"))
                .withANCVisits(asList(
                        create("ancVisitDate", "2012-09-01")
                                .put("weight", "55")
                                .put("bpSystolic", "121")
                                .put("bpDiastolic", "81")
                                .put("ancVisitNumber", "1")
                                .map(),
                        create("ancVisitDate", "2013-01-01")
                                .put("weight", "55")
                                .put("bpSystolic", "120")
                                .put("bpDiastolic", "80")
                                .put("ancVisitNumber", "2")
                                .map()
                ));

        verify(allMothers).update(updatedMother);
        verify(ancSchedulesService).ancVisitHasHappened("entity id 1", "anm id 1", 2, "2013-01-01", submission.instanceId());
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
                .withDetails(create("someKey", "someValue").put("bpDiastolic", "90").put("bpSystolic", "140").map());
        when(allMothers.findByCaseId("mother id 1")).thenReturn(mother);

        service.ancVisit(submission);

        verify(ancSchedulesService).ancVisitHasHappened("mother id 1", "anm id 1", 2, "2013-01-01", submission.instanceId());

        Mother updatedMother = new Mother("mother id 1", "ec id 1", "TC1")
                .withANCVisits(asList(create("ancVisitDate", "2013-01-01")
                        .put("weight", null)
                        .put("bpSystolic", "140")
                        .put("bpDiastolic", "90")
                        .put("ancVisitNumber", "2")
                        .map()))
                .withDetails(create("someKey", "someValue")
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
                .withDetails(create("someKey", "someValue")
                        .put("bpDiastolic", "90")
                        .put("bpSystolic", "140")
                        .put("isHypertensionDetectedForFirstTime", "true")
                        .map());
        when(allMothers.findByCaseId("mother id 1")).thenReturn(mother);

        service.ancVisit(submission);

        verify(ancSchedulesService).ancVisitHasHappened("mother id 1", "anm id 1", 2, "2013-01-01", submission.instanceId());
        Mother updatedMother = new Mother("mother id 1", "ec id 1", "TC1")
                .withANCVisits(asList(create("ancVisitDate", "2013-01-01")
                        .put("weight", null)
                        .put("bpSystolic", "140")
                        .put("bpDiastolic", "90")
                        .put("ancVisitNumber", "2")
                        .map()))
                .withDetails(create("someKey", "someValue")
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
                .withDetails(create("someKey", "someValue").put("bpDiastolic", "80").put("bpSystolic", "120").map());
        when(allMothers.findByCaseId("mother id 1")).thenReturn(mother);

        service.ancVisit(submission);

        verify(ancSchedulesService).ancVisitHasHappened("mother id 1", "anm id 1", 2, "2013-01-01", submission.instanceId());

        Mother updatedMother = new Mother("mother id 1", "ec id 1", "TC1").withDetails(
                create("someKey", "someValue")
                        .put("bpDiastolic", "80")
                        .put("bpSystolic", "120")
                        .map()
        );
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

        verifyZeroInteractions(ancSchedulesService);
    }

    @Test
    public void shouldHandleTTProvided() {
        FormSubmission submission = create()
                .withFormName("tt_2")
                .withANMId("anm id 1")
                .withEntityId("entity id 1")
                .addFormField("ttDate", "2013-01-01")
                .addFormField("ttDose", "tt2")
                .addFormField("someKey", "someValue")
                .build();
        List<Map<String, String>> ttDoses = new ArrayList<>();
        ttDoses.add(create("ttDate", "2012-12-24")
                .put("ttDose", "tt1")
                .map());
        Mother mother = new Mother("entity id 1", "ec entity id 1", "thayi 1")
                .withDetails(mapOf("some-key", "some-value"))
                .withTTDoses(ttDoses);

        when(allMothers.findByCaseId("entity id 1")).thenReturn(mother);

        service.ttProvided(submission);

        Mother updatedMother = new Mother("entity id 1", "ec entity id 1", "thayi 1")
                .withDetails(create("some-key", "some-value").map())
                .withTTDoses(asList(
                        create("ttDate", "2012-12-24")
                                .put("ttDose", "tt1")
                                .map(),
                        create("ttDate", "2013-01-01")
                                .put("ttDose", "tt2")
                                .map()
                ));
        verify(allMothers).update(updatedMother);
        verify(ancSchedulesService).ttVisitHasHappened("entity id 1", "anm id 1", "tt2", "2013-01-01", submission.instanceId());
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

        verify(ancSchedulesService).hbTestDone("entity id 1", "anm id 1", "2013-01-01", "Anaemic", parse("2012-01-01"), submission.instanceId());
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

        service.deliveryOutcome(submission);

        verify(ancSchedulesService).unEnrollFromAllSchedules("entity id 1", submission.instanceId());
    }

    @Test
    public void shouldHandleDeliveryPlan() {
        FormSubmission submission = create()
                .withFormName("delivery_plan")
                .withANMId("anm id 1")
                .withEntityId("entity id 1")
                .addFormField("someKey", "someValue")
                .addFormField("submissionDate", "2013-01-01")
                .build();
        when(allMothers.exists("entity id 1")).thenReturn(true);

        service.deliveryPlanned(submission);

        verify(ancSchedulesService).deliveryHasBeenPlanned("entity id 1", "anm id 1", "2013-01-01", submission.instanceId());
    }

    @Test
    public void shouldDoNothingIfMotherIsNotRegisteredWhileDeliveryOutcome() {
        when(allMothers.exists("entity id 1")).thenReturn(false);

        service.deliveryOutcome(create().build());

        verifyZeroInteractions(ancSchedulesService);
    }

    @Test
    public void shouldUnEnrollAMotherFromScheduleWhenANCCaseIsClosed() {
        when(allMothers.findByCaseId("entity id 1")).thenReturn(new Mother("entity id 1", "ec entity id 1", "thayi 1"));

        FormSubmission fs = create().build();
        service.close(fs);

        verify(ancSchedulesService).unEnrollFromAllSchedules(eq("entity id 1"), eq(fs.instanceId()));
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
    public void shouldMarkAllActionsAsInactiveWhenANCIsClosed() {
        when(allMothers.findByCaseId("entity id 1")).thenReturn(new Mother("entity id 1", "ec entity id 1", "thayi 1"));

        FormSubmission fs = create().build();
        service.close(fs);

        verify(ancSchedulesService).unEnrollFromAllSchedules("entity id 1", fs.instanceId());
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

        verify(ancSchedulesService).ifaTabletsGiven("entity id 1", "anmId", "100", "2013-05-24", submission.instanceId());
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
                .addFormField("ifaTabletsDate", "2013-05-24")
                .build();
        service.ifaTabletsGiven(submission);

        Mother updatedMother = new Mother("entity id 1", "ec entity id 1", "thayi 1")
                .withDetails(create("some-key", "some-value").put("totalNumberOfIFATabletsGiven", "30").map())
                .withIFATablets(asList(
                        create("ifaTabletsDate", "2013-05-24")
                                .put("numberOfIFATabletsGiven", "30")
                                .map()
                ));
        verify(allMothers).update(updatedMother);
    }

    @Test
    public void shouldUpdateIFATabletsInformationWhenTheyAreGiven() {
        List<Map<String, String>> ifaTablets = new ArrayList<>();
        ifaTablets.add(create("ifaTabletsDate", "2013-04-24")
                .put("numberOfIFATabletsGiven", "30")
                .map());
        Mother mother = new Mother("entity id 1", "ec entity id 1", "thayi 1")
                .withDetails(mapOf("some-key", "some-value"))
                .withIFATablets(ifaTablets);
        when(allMothers.findByCaseId("entity id 1")).thenReturn(mother);

        FormSubmission submission = create()
                .withFormName("ifa")
                .withEntityId("entity id 1")
                .addFormField("numberOfIFATabletsGiven", "30")
                .addFormField("ifaTabletsDate", "2013-05-24")
                .build();
        service.ifaTabletsGiven(submission);

        Mother updatedMother = new Mother("entity id 1", "ec entity id 1", "thayi 1")
                .withDetails(create("some-key", "some-value").put("totalNumberOfIFATabletsGiven", "30").map())
                .withIFATablets(asList(
                        create("ifaTabletsDate", "2013-04-24")
                                .put("numberOfIFATabletsGiven", "30")
                                .map(),
                        create("ifaTabletsDate", "2013-05-24")
                                .put("numberOfIFATabletsGiven", "30")
                                .map()
                ));
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
                .addFormField("ifaTabletsDate", "2013-05-24")
                .build();
        service.ifaTabletsGiven(submission);

        Mother updatedMother = new Mother("entity id 1", "ec entity id 1", "thayi 1")
                .withDetails(create("some-key", "some-value").put("totalNumberOfIFATabletsGiven", "90").map())
                .withIFATablets(asList(
                        create("ifaTabletsDate", "2013-05-24")
                                .put("numberOfIFATabletsGiven", "60")
                                .map()
                ));
        verify(allMothers).update(updatedMother);
    }

    @Test
    public void shouldHandleANCInvestigations() {
        FormSubmission submission = create()
                .withFormName("anc_investigations")
                .withANMId("anm id 1")
                .withEntityId("entity id 1")
                .addFormField("testDate", "2013-01-01")
                .addFormField("testResultsToEnter", "urine_sugar mp hiv")
                .addFormField("testsResultPositive", "urine_sugar")
                .addFormField("bileSalts", "absent")
                .addFormField("bilePigments", "absent")
                .addFormField("womanBloodGroup", "ab_positive")
                .addFormField("rhIncompatibleCouple", "no")
                .build();

        Mother mother = new Mother("entity id 1", "ec id 1", "TC1")
                .withDetails(mapOf("someKey", "someValue"));
        when(allMothers.findByCaseId("entity id 1")).thenReturn(mother);

        service.ancInvestigations(submission);

        Mother updatedMother = new Mother("entity id 1", "ec id 1", "TC1")
                .withDetails(mapOf("someKey", "someValue"))
                .withANCInvestigations(asList(
                        create("testDate", "2013-01-01")
                                .put("testResultsToEnter", "urine_sugar mp hiv")
                                .put("testsResultPositive", "urine_sugar")
                                .put("bileSalts", "absent")
                                .put("bilePigments", "absent")
                                .put("womanBloodGroup", "ab_positive")
                                .put("rhIncompatibleCouple", "no")
                                .map()
                ));

        verify(allMothers).update(updatedMother);
    }

    @Test
    public void shouldUpdateANCInvestigationsWhenItAlreadyExists() {
        FormSubmission submission = create()
                .withFormName("anc_investigations")
                .withANMId("anm id 1")
                .withEntityId("entity id 1")
                .addFormField("testDate", "2013-01-01")
                .addFormField("testResultsToEnter", "urine_sugar mp hiv")
                .addFormField("testsResultPositive", "urine_sugar")
                .addFormField("bileSalts", "absent")
                .addFormField("bilePigments", "absent")
                .addFormField("womanBloodGroup", "ab_positive")
                .addFormField("rhIncompatibleCouple", "no")
                .build();
        List<Map<String, String>> ancInvestigations = new ArrayList<>();
        ancInvestigations.add(create("testDate", "2012-09-01")
                .put("bileSalts", "present")
                .put("bilePigments", "present")
                .map());

        Mother mother = new Mother("entity id 1", "ec id 1", "TC1")
                .withANCInvestigations(ancInvestigations)
                .withDetails(mapOf("someKey", "someValue"));
        when(allMothers.findByCaseId("entity id 1")).thenReturn(mother);

        service.ancInvestigations(submission);

        Mother updatedMother = new Mother("entity id 1", "ec id 1", "TC1")
                .withDetails(mapOf("someKey", "someValue"))
                .withANCInvestigations(asList(
                        create("testDate", "2012-09-01")
                                .put("bileSalts", "present")
                                .put("bilePigments", "present")
                                .map(),
                        create("testDate", "2013-01-01")
                                .put("testResultsToEnter", "urine_sugar mp hiv")
                                .put("testsResultPositive", "urine_sugar")
                                .put("bileSalts", "absent")
                                .put("bilePigments", "absent")
                                .put("womanBloodGroup", "ab_positive")
                                .put("rhIncompatibleCouple", "no")
                                .map()
                ));

        verify(allMothers).update(updatedMother);
    }
}
