package org.ei.drishti.service;

import org.ei.drishti.contract.AnteNatalCareOutcomeInformation;
import org.ei.drishti.contract.BirthPlanningRequest;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.repository.AllMothers;
import org.ei.drishti.service.formSubmissionHandler.ReportFieldsDefinition;
import org.ei.drishti.service.reporting.MotherReportingService;
import org.ei.drishti.util.SafeMap;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.ei.drishti.common.util.DateUtil.today;
import static org.ei.drishti.util.EasyMap.create;
import static org.ei.drishti.util.EasyMap.mapOf;
import static org.ei.drishti.util.FormSubmissionBuilder.create;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ANCServiceTest {
    @Mock
    private ActionService actionService;
    @Mock
    private AllMothers mothers;
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

    private Map<String, Map<String, String>> EXTRA_DATA_EMPTY = new HashMap<>();
    private Map<String, Map<String, String>> EXTRA_DATA = create("details", mapOf("someKey", "someValue")).put("reporting", mapOf("someKey", "someValue")).map();

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        service = new ANCService(mothers, eligibleCouples, ancSchedulesService, actionService, motherReportingService, reportFieldsDefinition);
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
        when(mothers.findByCaseId("Mother 1")).thenReturn(mother);
        when(reportFieldsDefinition.get("anc_registration")).thenReturn(asList("someKey"));

        service.registerANC(submission);

        verify(mothers).update(mother.withAnm("anm id 1"));
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

        verifyZeroInteractions(mothers);
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
        when(mothers.findByCaseId("Mother 1")).thenReturn(mother);
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
        when(mothers.findByCaseId("Mother 1")).thenReturn(mother);

        service.registerOutOfAreaANC(submission);

        verify(mothers).update(mother.withAnm("anm id 1"));
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

        verifyZeroInteractions(mothers);
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

        when(mothers.exists("entity id 1")).thenReturn(true);
        when(reportFieldsDefinition.get("anc_visit")).thenReturn(asList("someKey"));

        service.ancVisit(submission);

        verify(ancSchedulesService).ancVisitHasHappened("entity id 1", "anm id 1", 2, "2013-01-01");
        verify(motherReportingService).ancVisit(new SafeMap(mapOf("someKey", "someValue")));
    }

    @Test
    public void shouldNotHandleANCVisitIfMotherIsNotFound() {
        FormSubmission submission = create()
                .withFormName("anc_visit")
                .withANMId("anm id 1")
                .withEntityId("entity id 1")
                .build();

        when(mothers.exists("entity id 1")).thenReturn(false);

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

        when(mothers.exists("entity id 1")).thenReturn(true);
        when(reportFieldsDefinition.get("tt_1")).thenReturn(asList("someKey"));

        service.ttProvided(submission);

        verify(ancSchedulesService).ttVisitHasHappened("entity id 1", "anm id 1", "tt1", "2013-01-01");
        verify(motherReportingService).ttProvided(new SafeMap(mapOf("someKey", "someValue")));
    }

    @Test
    public void shouldUnEnrollAMotherFromScheduleWhenANCCaseIsClosed() {
        when(mothers.findByCaseId("entity id 1")).thenReturn(new Mother("entity id 1", "ec entity id 1", "thayi 1"));

        service.closeANCCase(create().build());

        verify(ancSchedulesService).unEnrollFromSchedules("entity id 1");
    }

    @Test
    public void shouldCloseAMotherWhenANCCaseIsClosed() {
        when(mothers.findByCaseId("entity id 1")).thenReturn(new Mother("entity id 1", "ec entity id 1", "thayi 1"));

        service.closeANCCase(create().build());

        verify(mothers).close("entity id 1");
    }

    @Test
    public void shouldNotUnEnrollAMotherFromScheduleWhenSheIsNotRegistered() {
        when(mothers.findByCaseId("entity id 1")).thenReturn(null);

        service.closeANCCase(create().build());

        verifyZeroInteractions(ancSchedulesService);
    }

    @Test
    public void shouldCloseECCaseAlsoWhenPNCCaseIsClosedAndReasonIsDeath() {
        when(mothers.findByCaseId("entity id 1")).thenReturn(new Mother("entity id 1", "ec entity id 1", "thayi 1"));

        service.closeANCCase(create().addFormField("closeReason", "death_of_woman").build());

        verify(eligibleCouples).close("ec entity id 1");
    }

    @Test
    public void shouldCloseECCaseAlsoWhenANCCaseIsClosedAndReasonIsPermanentRelocation() {
        when(mothers.findByCaseId("entity id 1")).thenReturn(new Mother("entity id 1", "ec entity id 1", "thayi 1"));

        service.closeANCCase(create().addFormField("closeReason", "relocation_permanent").build());

        verify(eligibleCouples).close("ec entity id 1");
    }

    @Test
    public void shouldNotCloseECCaseWhenPNCCaseIsClosedAndReasonIsNeitherDeathOrPermanentRelocation() {
        when(mothers.findByCaseId("entity id 1")).thenReturn(new Mother("entity id 1", "ec entity id 1", "thayi 1"));

        service.closeANCCase(create().addFormField("closeReason", "other_reason").build());

        verifyZeroInteractions(ecService);
    }

    @Test
    public void shouldMarkAllActionsAsInactiveWhenANCIsClosed() {
        when(mothers.findByCaseId("entity id 1")).thenReturn(new Mother("entity id 1", "ec entity id 1", "thayi 1"));

        service.closeANCCase(create().build());

        verify(actionService).markAllAlertsAsInactive("entity id 1");
    }

    @Test
    public void shouldUnEnrollMotherFromANCSchedulesWhenDeliveryOutcomeFormIsFilled() throws Exception {
        when(mothers.exists("MOTHER-CASE-1")).thenReturn(true);
        when(mothers.updateDetails("MOTHER-CASE-1", EXTRA_DATA.get("details"))).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1").withDetails(EXTRA_DATA.get("details")));

        service.updatePregnancyOutcome(new AnteNatalCareOutcomeInformation("MOTHER-CASE-1", "ANM X", "live_birth", "2012-01-01", "yes", "0"), EXTRA_DATA);

        verify(ancSchedulesService).unEnrollFromSchedules("MOTHER-CASE-1");
    }

    @Test
    public void shouldUpdateMotherDetailsWhenDeliveryOutcomeFormIsFilled() throws Exception {
        when(mothers.exists("MOTHER-CASE-1")).thenReturn(true);
        when(mothers.updateDetails("MOTHER-CASE-1", EXTRA_DATA.get("details"))).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1").withDetails(EXTRA_DATA.get("details")));

        service.updatePregnancyOutcome(new AnteNatalCareOutcomeInformation("MOTHER-CASE-1", "ANM X", "live_birth", "2012-01-01", "yes", "0"), EXTRA_DATA);

        verify(mothers).updateDetails("MOTHER-CASE-1", EXTRA_DATA.get("details"));
    }

    @Test
    public void shouldUpdateMotherDeliveryDetailsWhenDeliveryOutcomeFormIsFilled() throws Exception {
        when(mothers.exists("MOTHER-CASE-1")).thenReturn(true);
        when(mothers.updateDetails("MOTHER-CASE-1", EXTRA_DATA.get("details"))).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1").withDetails(EXTRA_DATA.get("details")));

        service.updatePregnancyOutcome(new AnteNatalCareOutcomeInformation("MOTHER-CASE-1", "ANM X", "live_birth", "2012-01-01", "yes", "0"), EXTRA_DATA);

        verify(mothers).updateDeliveryOutcomeFor("MOTHER-CASE-1", "2012-01-01");
    }

    @Test
    public void shouldReportWhenDeliveryOutcomeFormIsFilled() throws Exception {
        when(mothers.exists("MOTHER-CASE-1")).thenReturn(true);
        when(mothers.updateDetails("MOTHER-CASE-1", EXTRA_DATA.get("details"))).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1").withDetails(EXTRA_DATA.get("details")));

        service.updatePregnancyOutcome(new AnteNatalCareOutcomeInformation("MOTHER-CASE-1", "ANM X", "live_birth", "2012-01-01", "yes", "0"), EXTRA_DATA);

        verify(motherReportingService).updatePregnancyOutcome(new SafeMap(EXTRA_DATA.get("reporting")));
    }

    @Test
    public void shouldIgnoreDeliveryOutcomeUploadIfThereIsNoCorrespondingMotherInRepo() throws Exception {
        when(mothers.exists("CASE-X")).thenReturn(false);

        service.updatePregnancyOutcome(new AnteNatalCareOutcomeInformation("MOTHER-CASE-1", "ANM X", "live_birth", "2012-01-01", "yes", "0"), EXTRA_DATA);

        verifyZeroInteractions(ancSchedulesService);
        verifyZeroInteractions(actionService);
    }

    @Test
    public void shouldUpdateMotherDetailsAndSendAnActionWhenBirthPlanningDetailsAreUpdateAndMotherExists() throws Exception {
        when(mothers.exists("CASE X")).thenReturn(true);
        Map<String, String> updatedDetails = mapOf("aNewKey", "aNewValue");
        when(mothers.updateDetails("CASE X", EXTRA_DATA.get("details"))).thenReturn(new Mother("CASE X", "EC-CASE-1", "TC X").withDetails(updatedDetails));

        service.updateBirthPlanning(new BirthPlanningRequest("CASE X", "ANM X"), EXTRA_DATA);

        verify(mothers).updateDetails("CASE X", EXTRA_DATA.get("details"));
        verify(actionService).updateBirthPlanning("CASE X", "ANM X", updatedDetails);
    }

    @Test
    public void shouldNotSendBirthPlanningUpdatesAsActionWhenMotherNotFoundInDrishti() throws Exception {
        when(mothers.exists("CASE X")).thenReturn(false);

        service.updateBirthPlanning(new BirthPlanningRequest("CASE X", "ANM X"), EXTRA_DATA);

        verifyZeroInteractions(actionService);
        verify(mothers, times(0)).updateDetails(any(String.class), any(Map.class));
    }

    @Test
    public void shouldUpdateIFASchedulesWhenNumberOfIFATabletsGivenIsMoreThanZero() {
        when(mothers.exists("entity id 1")).thenReturn(true);

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
        when(mothers.exists("entity id 1")).thenReturn(false);

        FormSubmission submission = create()
                .withFormName("ifa")
                .withEntityId("entity id 1")
                .addFormField("numberOfIFATabletsGiven", "0")
                .build();
        service.ifaTabletsGiven(submission);

        verifyZeroInteractions(ancSchedulesService);
    }
}
