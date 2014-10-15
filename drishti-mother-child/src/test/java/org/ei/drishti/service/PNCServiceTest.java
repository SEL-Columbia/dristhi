package org.ei.drishti.service;

import org.ei.drishti.common.util.EasyMap;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.domain.PNCVisit;
import org.ei.drishti.domain.register.*;
import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.form.domain.SubFormData;
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
import java.util.Map;

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
                .withSubForm(new SubFormData("child_registration", Collections.<Map<String, String>>emptyList()))
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
                .withSubForm(new SubFormData("child_registration", Collections.<Map<String, String>>emptyList()))
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
                .withSubForm(new SubFormData("child_registration", Collections.<Map<String, String>>emptyList()))
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
                .withSubForm(new SubFormData("child_registration", Collections.<Map<String, String>>emptyList()))
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
                .withSubForm(new SubFormData("child_registration", Collections.<Map<String, String>>emptyList()))
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
                .withSubForm(new SubFormData("child_registration", Collections.<Map<String, String>>emptyList()))
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
        when(allEligibleCouples.findByCaseId("ec id 1")).thenReturn(new EligibleCouple("ec id 1", "123"));
        FormSubmission submission = create()
                .withFormName("pnc_registration_oa")
                .withANMId("anm id 1")
                .withEntityId("ec id 1")
                .addFormField("referenceDate", "2012-01-01")
                .addFormField("didWomanSurvive", "yes")
                .withSubForm(new SubFormData("child_registration_oa", Collections.<Map<String, String>>emptyList()))
                .build();

        service.pncRegistrationOA(submission);

        verify(allMothers).update(new Mother("mother id 1", "ec id 1", "tc 1").withAnm("anm id 1"));
    }

    @Test
    public void shouldUpdateANMInformationOfEligibleCoupleWhenOAPNCIsRegistered() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        when(allMothers.findByEcCaseId("ec id 1")).thenReturn(asList(new Mother("mother id 1", "ec id 1", "tc 1")));
        when(allEligibleCouples.findByCaseId("ec id 1")).thenReturn(new EligibleCouple("ec id 1", "123"));
        FormSubmission submission = create()
                .withFormName("pnc_registration_oa")
                .withANMId("anm id 1")
                .withEntityId("ec id 1")
                .addFormField("referenceDate", "2012-01-01")
                .addFormField("didWomanSurvive", "yes")
                .withSubForm(new SubFormData("child_registration_oa", Collections.<Map<String, String>>emptyList()))
                .build();

        service.pncRegistrationOA(submission);

        verify(allEligibleCouples).update(new EligibleCouple("ec id 1", "123").withANMIdentifier("anm id 1"));
    }

    @Test
    public void shouldUpdateMotherWithChildrenDetailsWhenDeliveryOutcome() {
        when(allMothers.findByCaseId("mother id 1"))
                .thenReturn(new Mother("mother id 1", "ec id 1", "tc 1")
                        .withChildrenDetails(asList(EasyMap.create("id", "child id")
                                .put("gender", "female")
                                .put("weight", "2.5")
                                .put("immunizationsGiven", "bcg")
                                .map())));
        FormSubmission submission = create()
                .withFormName("delivery_outcome")
                .withANMId("anm id 1")
                .withEntityId("mother id 1")
                .addFormField("referenceDate", "2012-01-01")
                .addFormField("didWomanSurvive", "yes")
                .withSubForm(new SubFormData("child_registration",
                        asList(EasyMap.create("id", "child id 1")
                                        .put("gender", "male")
                                        .put("weight", "2")
                                        .put("immunizationsGiven", "bcg")
                                        .map(),
                                EasyMap.create("id", "child id 2")
                                        .put("gender", "female")
                                        .put("weight", "3")
                                        .map()
                        )
                ))
                .build();

        service.deliveryOutcome(submission);

        verify(allMothers).update(new Mother("mother id 1", "ec id 1", "tc 1")
                .withChildrenDetails(asList(EasyMap.create("id", "child id")
                                .put("gender", "female")
                                .put("weight", "2.5")
                                .put("immunizationsGiven", "bcg")
                                .map(),
                        EasyMap.create("id", "child id 1")
                                .put("gender", "male")
                                .put("weight", "2")
                                .put("immunizationsAtBirth", "bcg")
                                .map(),
                        EasyMap.create("id", "child id 2")
                                .put("gender", "female")
                                .put("weight", "3")
                                .put("immunizationsAtBirth", null)
                                .map()
                )));
    }

    @Test
    public void shouldUpdateMotherWithChildrenDetailsWhenPNCRegistrationOA() {
        when(allMothers.findByEcCaseId("ec id 1")).thenReturn(asList(new Mother("mother id 1", "ec id 1", "tc 1")));
        when(allEligibleCouples.findByCaseId("ec id 1")).thenReturn(new EligibleCouple("ec id 1", "123"));
        FormSubmission submission = create()
                .withFormName("pnc_registration_oa")
                .withANMId("anm id 1")
                .withEntityId("ec id 1")
                .addFormField("referenceDate", "2012-01-01")
                .addFormField("didWomanSurvive", "yes")
                .withSubForm(new SubFormData("child_registration_oa",
                        asList(EasyMap.create("id", "child id 1")
                                        .put("gender", "male")
                                        .put("weight", "2")
                                        .map(),
                                EasyMap.create("id", "child id 2")
                                        .put("gender", "female")
                                        .put("weight", "3")
                                        .map()
                        )
                ))
                .build();

        service.pncRegistrationOA(submission);

        verify(allMothers).update(new Mother("mother id 1", "ec id 1", "tc 1")
                .withAnm("anm id 1")
                .withChildrenDetails(asList(EasyMap.create("id", "child id 1")
                                        .put("gender", "male")
                                        .put("weight", "2")
                                        .put("immunizationsAtBirth", null)
                                        .map(),
                                EasyMap.create("id", "child id 2")
                                        .put("gender", "female")
                                        .put("weight", "3")
                                        .put("immunizationsAtBirth", null)
                                        .map()
                        )
                ));
    }

    @Test
    public void shouldNotAddChildrenDetailsInTheCaseOfStillBirthDuringPNCRegistrationOA() throws Exception {
        when(allMothers.findByEcCaseId("ec id 1")).thenReturn(asList(new Mother("mother id 1", "ec id 1", "tc 1")));
        when(allEligibleCouples.findByCaseId("ec id 1")).thenReturn(new EligibleCouple("ec id 1", "123"));
        FormSubmission submission = create()
                .withFormName("pnc_registration_oa")
                .withANMId("anm id 1")
                .withEntityId("ec id 1")
                .addFormField("referenceDate", "2012-01-01")
                .addFormField("deliveryOutcome", "still_birth")
                .addFormField("didWomanSurvive", "yes")
                .withSubForm(new SubFormData("child_registration_oa",
                        asList(EasyMap.create("id", "child id 1").map())))
                .build();

        service.pncRegistrationOA(submission);

        verify(allMothers).update(new Mother("mother id 1", "ec id 1", "tc 1")
                .withAnm("anm id 1"));
    }

    @Test
    public void shouldNotAddChildrenDetailsInTheCaseOfStillBirthDuringDeliveryOutcome() throws Exception {
        when(allMothers.findByCaseId("mother id 1")).thenReturn(new Mother("mother id 1", "ec id 1", "tc 1"));
        FormSubmission submission = create()
                .withFormName("delivery_outcome")
                .withANMId("anm id 1")
                .withEntityId("mother id 1")
                .addFormField("referenceDate", "2012-01-01")
                .addFormField("deliveryOutcome", "still_birth")
                .addFormField("didWomanSurvive", "yes")
                .withSubForm(new SubFormData("child_registration",
                        asList(EasyMap.create("id", "child id 1").map())))
                .build();

        service.deliveryOutcome(submission);

        verify(allMothers).update(new Mother("mother id 1", "ec id 1", "tc 1"));
    }

    @Test
    public void shouldEnrollPNCIntoSchedulesDuringPNCRegistrationIfWomanSurvives() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        when(allMothers.findByEcCaseId("ec id 1")).thenReturn(asList(new Mother("mother id 1", "ec id 1", "tc 1")));
        when(allEligibleCouples.findByCaseId("ec id 1")).thenReturn(new EligibleCouple("ec id 1", "123"));
        FormSubmission submission = create()
                .withFormName("pnc_registration_oa")
                .withANMId("anm id 1")
                .withEntityId("ec id 1")
                .addFormField("referenceDate", "2012-01-01")
                .addFormField("didWomanSurvive", "yes")
                .withSubForm(new SubFormData("child_registration_oa", Collections.<Map<String, String>>emptyList()))
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
                .withSubForm(new SubFormData("child_registration", Collections.<Map<String, String>>emptyList()))
                .build();

        service.pncRegistrationOA(submission);

        verifyZeroInteractions(pncSchedulesService);
    }

    @Test
    public void shouldNotEnrollPNCIntoSchedulesPNCRegistrationIfWomanDied() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        when(allMothers.findByEcCaseId("ec id 1")).thenReturn(asList(new Mother("mother id 1", "ec id 1", "tc 1")));
        when(allEligibleCouples.findByCaseId("ec id 1")).thenReturn(new EligibleCouple("ec id 1", "123"));
        FormSubmission submission = create()
                .withFormName("pnc_registration_oa")
                .withANMId("anm id 1")
                .withEntityId("ec id 1")
                .addFormField("referenceDate", "2012-01-01")
                .addFormField("didWomanSurvive", "no")
                .withSubForm(new SubFormData("child_registration_oa", Collections.<Map<String, String>>emptyList()))
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
                .withSubForm(new SubFormData("child_pnc_visit", Collections.<Map<String, String>>emptyList()))
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
                .withSubForm(new SubFormData("child_pnc_visit", Collections.<Map<String, String>>emptyList()))
                .build();
        service.pncVisitHappened(submission);

        Mother updatedMother = new Mother("mother id 1", "ec id 1", "TC1")
                .withDetails(EasyMap.create("some-key", "some-value")
                        .put("pncVisitDates", "2013-01-01").map())
                .withPNCVisits(asList(new PNCVisit().withDate("2013-01-01").withChildrenDetails(Collections.<Map<String, String>>emptyList())));
        verify(allMothers).update(updatedMother);
    }

    @Test
    public void shouldUpdateMotherWithPNCVisitDetails() throws Exception {
        Mother mother = new Mother("mother id 1", "ec id 1", "TC1")
                .withDetails(EasyMap.mapOf("some-key", "some-value"));
        when(allMothers.findByCaseId("entity id 1")).thenReturn(mother);

        FormSubmission submission = create()
                .withFormName("pnc_visit")
                .addFormField("pncVisitDate", "2013-01-01")
                .addFormField("pncVisitPerson", "ASHA")
                .addFormField("pncVisitPlace", "phc")
                .addFormField("difficulties1", "difficulties 1")
                .addFormField("abdominalProblems", "abdominal Problems")
                .addFormField("vaginalProblems", "vaginal Problems")
                .addFormField("difficulties2", "difficulties 2")
                .addFormField("breastProblems", "breast Problems")
                .withSubForm(new SubFormData("child_pnc_visit",
                        asList(EasyMap.create("id", "child id 1")
                                .put("urineStoolProblems", "vomiting diarrhea")
                                .put("activityProblems", "convulsions")
                                .put("breathingProblems", "breathing_too_fast")
                                .put("skinProblems", "jaundice")
                                .map())
                ))
                .build();
        service.pncVisitHappened(submission);

        Mother updatedMother = new Mother("mother id 1", "ec id 1", "TC1")
                .withDetails(EasyMap.create("some-key", "some-value")
                        .put("pncVisitDates", "2013-01-01")
                        .map())
                .withPNCVisits(asList(new PNCVisit()
                                .withDate("2013-01-01")
                                .withPerson("ASHA")
                                .withPlace("phc")
                                .withDifficulties("difficulties 1")
                                .withAbdominalProblems("abdominal Problems")
                                .withVaginalProblems("vaginal Problems")
                                .withUrinalProblems("difficulties 2")
                                .withBreastProblems("breast Problems")
                                .withChildrenDetails(asList(EasyMap.create("id", "child id 1")
                                                .put("urineStoolProblems", "vomiting diarrhea")
                                                .put("activityProblems", "convulsions")
                                                .put("breathingProblems", "breathing_too_fast")
                                                .put("skinProblems", "jaundice")
                                                .map())
                                )
                ));
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
                .withSubForm(new SubFormData("child_pnc_visit", Collections.<Map<String, String>>emptyList()))
                .build();
        service.pncVisitHappened(submission);

        Mother updatedMother = new Mother("mother id 1", "ec id 1", "TC1")
                .withDetails(
                        EasyMap.create("some-key", "some-value")
                                .put("pncVisitDates", "2013-01-01 2013-01-02")
                                .map()
                )
                .withPNCVisits(asList(new PNCVisit().withDate("2013-01-02").withChildrenDetails(Collections.<Map<String, String>>emptyList())));
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
        when(allEligibleCouples.findByCaseId("ec id 1")).thenReturn(new EligibleCouple("ec id 1", "123"));

        FormSubmission submission = create()
                .withFormName("pnc_registration_oa")
                .addFormField("some-key", "value")
                .withSubForm(new SubFormData("child_registration_oa", Collections.<Map<String, String>>emptyList()))
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
                .addFormField("currentMethod", "female_sterilization")
                .addFormField("familyPlanningMethodChangeDate", "2010-01-01")
                .addFormField("femaleSterilizationType", "minilap")
                .build();
        Mother mother = new Mother("mother id 1", "ec id 1", "thayi card number 1");
        EligibleCouple eligibleCouple = new EligibleCouple("ec id 1", "ec number 1")
                .withIUDFPDetails(Collections.<IUDFPDetails>emptyList())
                .withCondomFPDetails(Collections.<CondomFPDetails>emptyList())
                .withOCPFPDetails(Collections.<OCPFPDetails>emptyList())
                .withFemaleSterilizationFPDetails(new ArrayList<FemaleSterilizationFPDetails>())
                .withMaleSterilizationFPDetails(Collections.<MaleSterilizationFPDetails>emptyList());
        when(allMothers.findByCaseId("mother id 1")).thenReturn(mother);
        when(allEligibleCouples.findByCaseId("ec id 1")).thenReturn(eligibleCouple);

        service.reportPPFamilyPlanning(submission);

        List<FemaleSterilizationFPDetails> femaleSterilizationFPDetails = new ArrayList<>();
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
                .addFormField("currentMethod", "iud")
                .addFormField("familyPlanningMethodChangeDate", "2010-01-01")
                .addFormField("iudPlace", "phc")
                .build();
        Mother mother = new Mother("mother id 1", "ec id 1", "thayi card number 1");
        EligibleCouple eligibleCouple = new EligibleCouple("ec id 1", "ec number 1")
                .withIUDFPDetails(new ArrayList<IUDFPDetails>())
                .withCondomFPDetails(Collections.<CondomFPDetails>emptyList())
                .withOCPFPDetails(Collections.<OCPFPDetails>emptyList())
                .withFemaleSterilizationFPDetails(Collections.<FemaleSterilizationFPDetails>emptyList())
                .withMaleSterilizationFPDetails(Collections.<MaleSterilizationFPDetails>emptyList());
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
                .addFormField("currentMethod", "female_sterilization")
                .addFormField("familyPlanningMethodChangeDate", "2010-01-01")
                .addFormField("femaleSterilizationType", "minilap")
                .build();
        Mother mother = new Mother("mother id 1", "ec id 1", "thayi card number 1");
        EligibleCouple eligibleCouple = new EligibleCouple("ec id 1", "ec number 1")
                .withIUDFPDetails(Collections.<IUDFPDetails>emptyList())
                .withCondomFPDetails(Collections.<CondomFPDetails>emptyList())
                .withOCPFPDetails(Collections.<OCPFPDetails>emptyList())
                .withFemaleSterilizationFPDetails(new ArrayList<FemaleSterilizationFPDetails>())
                .withMaleSterilizationFPDetails(Collections.<MaleSterilizationFPDetails>emptyList());
        when(allMothers.findByCaseId("mother id 1")).thenReturn(mother);
        when(allEligibleCouples.findByCaseId("ec id 1")).thenReturn(eligibleCouple);

        service.reportPPFamilyPlanning(submission);

        ArrayList<FemaleSterilizationFPDetails> femaleSterilizationFPDetails = new ArrayList<>();
        femaleSterilizationFPDetails.add(new FemaleSterilizationFPDetails("minilap", "2010-01-01"));
        EligibleCouple expectedEC = new EligibleCouple("ec id 1", "ec number 1")
                .withIUDFPDetails(Collections.<IUDFPDetails>emptyList())
                .withCondomFPDetails(Collections.<CondomFPDetails>emptyList())
                .withOCPFPDetails(Collections.<OCPFPDetails>emptyList())
                .withMaleSterilizationFPDetails(Collections.<MaleSterilizationFPDetails>emptyList())
                .withFemaleSterilizationFPDetails(femaleSterilizationFPDetails);
        verify(allEligibleCouples).update(expectedEC);
    }

    @Test
    public void shouldUpdateMotherWithNewFamilyPlanningCondomWhenPPFPIsSubmitted() throws Exception {
        FormSubmission submission = create()
                .withFormName("postpartum_family_planning")
                .withEntityId("mother id 1")
                .addFormField("some-key", "value")
                .addFormField("currentMethod", "condom")
                .addFormField("familyPlanningMethodChangeDate", "2010-01-01")
                .addFormField("numberOfCondomsSupplied", "20")
                .build();
        Mother mother = new Mother("mother id 1", "ec id 1", "thayi card number 1");
        EligibleCouple eligibleCouple = new EligibleCouple("ec id 1", "ec number 1")
                .withIUDFPDetails(Collections.<IUDFPDetails>emptyList())
                .withCondomFPDetails(new ArrayList<CondomFPDetails>())
                .withOCPFPDetails(Collections.<OCPFPDetails>emptyList())
                .withFemaleSterilizationFPDetails(Collections.<FemaleSterilizationFPDetails>emptyList())
                .withMaleSterilizationFPDetails(Collections.<MaleSterilizationFPDetails>emptyList());
        when(allMothers.findByCaseId("mother id 1")).thenReturn(mother);
        when(allEligibleCouples.findByCaseId("ec id 1")).thenReturn(eligibleCouple);

        service.reportPPFamilyPlanning(submission);

        List<CondomFPDetails> condomFPDetails = new ArrayList<>();
        condomFPDetails.add(new CondomFPDetails("2010-01-01", asList(EasyMap.create("date", "2010-01-01").put("quantity", "20").map())));
        EligibleCouple expectedEC = new EligibleCouple("ec id 1", "ec number 1")
                .withIUDFPDetails(Collections.<IUDFPDetails>emptyList())
                .withCondomFPDetails(Collections.<CondomFPDetails>emptyList())
                .withOCPFPDetails(Collections.<OCPFPDetails>emptyList())
                .withMaleSterilizationFPDetails(Collections.<MaleSterilizationFPDetails>emptyList())
                .withFemaleSterilizationFPDetails(Collections.<FemaleSterilizationFPDetails>emptyList())
                .withCondomFPDetails(condomFPDetails);
        verify(allEligibleCouples).update(expectedEC);
    }
}
