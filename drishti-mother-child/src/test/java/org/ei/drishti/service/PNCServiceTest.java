package org.ei.drishti.service;

import org.ei.drishti.domain.Child;
import org.ei.drishti.domain.Mother;
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

import java.util.Collections;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.ei.drishti.util.EasyMap.create;
import static org.ei.drishti.util.EasyMap.mapOf;
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
    private AllMothers mothers;
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
        service = new PNCService(actionService, childSchedulesService, pncSchedulesService, allEligibleCouples, mothers, children,
                motherReportingService, childReportingService, reportFieldsDefinition);
    }

    @Test
    public void shouldEnrollPNCIntoSchedulesDuringDeliveryOutcomeIfWomanOrMotherSurvives() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        when(mothers.exists("mother id 1")).thenReturn(true);
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
        when(mothers.exists("mother id 1")).thenReturn(false);
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
        when(mothers.exists("mother id 1")).thenReturn(true);
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
        when(mothers.exists("mother id 1")).thenReturn(true);
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
    public void shouldEnrollPNCIntoSchedulesDuringPNCRegistrationIfWomanSurvives() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        when(mothers.findByEcCaseId("ec id 1")).thenReturn(asList(new Mother("mother id 1", "ec id 1", "tc 1")));
        FormSubmission submission = create()
                .withFormName("pnc_registration_oa")
                .withANMId("anm id 1")
                .withEntityId("ec id 1")
                .addFormField("referenceDate", "2012-01-01")
                .addFormField("didWomanSurvive", "yes")
                .build();

        service.pncRegistration(submission);

        verify(pncSchedulesService).deliveryOutcome("mother id 1", "2012-01-01");
    }

    @Test
    public void shouldNotEnrollPNCIntoSchedulesDuringPNCRegistrationIfMotherDoesNotExists() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        when(mothers.findByEcCaseId("ec id 1")).thenReturn(Collections.EMPTY_LIST);
        FormSubmission submission = create()
                .withFormName("pnc_registration_oa")
                .withANMId("anm id 1")
                .withEntityId("ec id 1")
                .addFormField("referenceDate", "2012-01-01")
                .addFormField("didWomanSurvive", "")
                .build();

        service.pncRegistration(submission);

        verifyZeroInteractions(pncSchedulesService);
    }

    @Test
    public void shouldNotEnrollPNCIntoSchedulesPNCRegistrationIfWomanDied() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        when(mothers.findByEcCaseId("ec id 1")).thenReturn(asList(new Mother("mother id 1", "ec id 1", "tc 1")));
        FormSubmission submission = create()
                .withFormName("pnc_registration_oa")
                .withANMId("anm id 1")
                .withEntityId("ec id 1")
                .addFormField("referenceDate", "2012-01-01")
                .addFormField("didWomanSurvive", "no")
                .build();

        service.pncRegistration(submission);

        verifyZeroInteractions(pncSchedulesService);
    }

    @Test
    public void shouldUpdateANMIdOnMotherWhenOAPNCIsRegistered() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        Mother mother = new Mother("mother id 1", "ec id 1", "TC1");
        when(mothers.findByEcCaseId("ec id 1")).thenReturn(asList(mother));
        when(children.findByMotherId("mother id 1")).thenReturn(Collections.<Child>emptyList());
        FormSubmission submission = create()
                .withFormName("pnc_registration_oa")
                .withANMId("anm id 1")
                .withEntityId("ec id 1")
                .addFormField("referenceDate", "2012-01-01")
                .build();

        service.pncOAChildRegistration(submission);

        verify(mothers).update(mother.withAnm("anm id 1"));
    }

    @Test
    public void shouldEnrollEveryChildIntoSchedulesDuringPNCRegistration() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        when(mothers.findByEcCaseId("ec id 1")).thenReturn(asList(new Mother("mother id 1", "ec id 1", "TC1")));
        Child firstChild = new Child("child id 1", "mother id 1", "opv", "2", "female");
        Child secondChild = new Child("child id 2", "mother id 1", "opv", "2", "male");
        when(children.findByMotherId("mother id 1")).thenReturn(asList(firstChild, secondChild));
        FormSubmission submission = create()
                .withFormName("pnc_registration_oa")
                .withANMId("anm id 1")
                .withEntityId("ec id 1")
                .addFormField("referenceDate", "2012-01-01")
                .build();

        service.pncOAChildRegistration(submission);

        verify(childSchedulesService).enrollChild(firstChild.withAnm("anm id 1").withDateOfBirth("2012-01-01").withThayiCard("TC1"));
        verify(childSchedulesService).enrollChild(secondChild.withAnm("anm id 1").withDateOfBirth("2012-01-01").withThayiCard("TC1"));
    }

    @Test
    public void shouldUpdateEveryChildWithMotherInfoDuringPNCgRegistration() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        when(mothers.findByEcCaseId("ec id 1")).thenReturn(asList(new Mother("mother id 1", "ec id 1", "TC1")));
        Child firstChild = new Child("child id 1", "mother id 1", "opv", "2", "female");
        Child secondChild = new Child("child id 2", "mother id 1", "opv", "2", "male");
        when(children.findByMotherId("mother id 1")).thenReturn(asList(firstChild, secondChild));
        FormSubmission submission = create()
                .withFormName("pnc_registration_oa")
                .withANMId("anm id 1")
                .withEntityId("ec id 1")
                .addFormField("referenceDate", "2012-01-01")
                .build();

        service.pncOAChildRegistration(submission);

        verify(children).update(firstChild.withAnm("anm id 1").withDateOfBirth("2012-01-01").withThayiCard("TC1"));
        verify(children).update(secondChild.withAnm("anm id 1").withDateOfBirth("2012-01-01").withThayiCard("TC1"));
    }

    @Test
    public void shouldNotHandlePNCChildRegistrationWhenMotherIsNotFound() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        when(mothers.findByEcCaseId("ec id 1")).thenReturn(Collections.EMPTY_LIST);
        FormSubmission submission = create()
                .withFormName("pnc_registration_oa")
                .withANMId("anm id 1")
                .withEntityId("ec id 1")
                .addFormField("referenceDate", "2012-01-01")
                .build();

        service.pncOAChildRegistration(submission);

        verifyZeroInteractions(children);
        verifyZeroInteractions(childSchedulesService);
    }

    @Test
    public void shouldUnEnrollAMotherFromScheduleWhenPNCCaseIsClosed() {
        when(mothers.findByCaseId("entity id 1")).thenReturn(new Mother("entity id 1", "ec entity id 1", "thayi 1"));

        service.close(create().build());

        verify(pncSchedulesService).unEnrollFromSchedules("entity id 1");
    }

    @Test
    public void shouldCloseAMotherWhenPNCIsClosed() {
        when(mothers.findByCaseId("entity id 1")).thenReturn(new Mother("entity id 1", "ec entity id 1", "thayi 1"));

        service.close(create().build());

        verify(mothers).close("entity id 1");
    }

    @Test
    public void shouldNotDoAnythingIfMotherIsNotRegistered() {
        when(mothers.findByCaseId("entity id 1")).thenReturn(null);

        service.close(create().build());

        verifyZeroInteractions(pncSchedulesService);
        verifyZeroInteractions(allEligibleCouples);
        verifyZeroInteractions(motherReportingService);
        verifyZeroInteractions(actionService);
    }

    @Test
    public void shouldCloseECCaseAlsoWhenPNCIsClosedAndReasonIsDeath() {
        when(mothers.findByCaseId("entity id 1")).thenReturn(new Mother("entity id 1", "ec entity id 1", "thayi 1"));

        service.close(create().addFormField("closeReason", "death_of_woman").build());

        verify(allEligibleCouples).close("ec entity id 1");
    }

    @Test
    public void shouldCloseECCaseAlsoWhenPNCIsClosedAndReasonIsPermanentRelocation() {
        when(mothers.findByCaseId("entity id 1")).thenReturn(new Mother("entity id 1", "ec entity id 1", "thayi 1"));

        service.close(create().addFormField("closeReason", "relocation_permanent").build());

        verify(allEligibleCouples).close("ec entity id 1");
    }

    @Test
    public void shouldNotCloseECCaseWhenPNCIsClosedAndReasonIsNeitherDeathOrPermanentRelocation() {
        when(mothers.findByCaseId("entity id 1")).thenReturn(new Mother("entity id 1", "ec entity id 1", "thayi 1"));

        service.close(create().addFormField("closeReason", "other_reason").build());

        verifyZeroInteractions(allEligibleCouples);
    }

    @Test
    public void shouldMarkAllActionsAsInactiveWhenPNCIsClosed() {
        when(mothers.findByCaseId("entity id 1")).thenReturn(new Mother("entity id 1", "ec entity id 1", "thayi 1"));

        service.close(create().build());

        verify(actionService).markAllAlertsAsInactive("entity id 1");
    }

    @Test
    public void shouldDoReportingWhenPNCIsClosed() {
        when(mothers.findByCaseId("entity id 1")).thenReturn(new Mother("entity id 1", "ec entity id 1", "thayi 1"));
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

        when(mothers.exists("entity id 1")).thenReturn(false);

        service.pncVisitHappened(submission);

        verify(mothers).exists("entity id 1");
        verifyZeroInteractions(motherReportingService);
        verifyZeroInteractions(childReportingService);
    }

    @Test
    public void shouldReportPNCVisit() throws Exception {
        when(reportFieldsDefinition.get("pnc_visit")).thenReturn(asList("some-key"));
        when(mothers.exists("entity id 1")).thenReturn(true);

        FormSubmission submission = create()
                .withFormName("pnc_visit")
                .addFormField("some-key", "value")
                .build();
        service.pncVisitHappened(submission);

        SafeMap reportFields = new SafeMap(mapOf("some-key", "value"));
        verify(motherReportingService).pncVisitHappened(reportFields);
    }

    @Test
    public void shouldAutoClosePNCCaseWhenMotherExists() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        when(mothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC1").withAnm("ANM 1"));

        service.autoClosePNCCase("MOTHER-CASE-1");

        verify(mothers).close("MOTHER-CASE-1");
        verify(actionService).markAllAlertsAsInactive("MOTHER-CASE-1");
        verifyZeroInteractions(motherReportingService);
    }

    @Test
    public void shouldNotAutoClosePNCCaseWhenMotherDoesNotExist() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        when(mothers.findByCaseId("MOTHER-CASE-1")).thenReturn(null);

        service.autoClosePNCCase("MOTHER-CASE-1");

        verify(mothers, times(0)).close("MOTHER-CASE-1");
        verifyZeroInteractions(actionService);
    }
}
