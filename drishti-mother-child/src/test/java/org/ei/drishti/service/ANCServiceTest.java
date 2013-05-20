package org.ei.drishti.service;

import org.ei.drishti.contract.*;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.repository.AllMothers;
import org.ei.drishti.service.formSubmissionHandler.ReportFieldsDefinition;
import org.ei.drishti.service.reporting.MotherReportingService;
import org.ei.drishti.util.FormSubmissionBuilder;
import org.ei.drishti.util.SafeMap;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.model.Time;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.ei.drishti.common.AllConstants.Report.REPORT_EXTRA_DATA_KEY_NAME;
import static org.ei.drishti.common.util.DateUtil.today;
import static org.ei.drishti.util.EasyMap.create;
import static org.ei.drishti.util.EasyMap.mapOf;
import static org.ei.drishti.util.Matcher.objectWithSameFieldsAs;
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
        service = new ANCService(ecService, mothers, eligibleCouples, ancSchedulesService, actionService, motherReportingService, reportFieldsDefinition);
    }

    @Test
    public void shouldRegisterANC() {
        LocalDate lmp = today();

        FormSubmission submission = FormSubmissionBuilder.create()
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
    public void shouldNotRegisterANCIfTheECIsNotFound() {
        FormSubmission submission = FormSubmissionBuilder.create()
                .withFormName("anc_registration")
                .withANMId("anm id 1")
                .withEntityId("ec id 1")
                .build();

        when(eligibleCouples.exists("ec id 1")).thenReturn(false);

        service.registerANC(submission);

        verifyZeroInteractions(mothers);
        verifyZeroInteractions(ancSchedulesService);
        verifyZeroInteractions(actionService);
    }

    @Test
    public void shouldEnrollMotherIntoDefaultScheduleDuringEnrollmentBasedOnLMP() {
        LocalDate lmp = today().minusDays(2);

        FormSubmission submission = FormSubmissionBuilder.create()
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

        verify(ancSchedulesService).enrollMother(eq("Mother 1"), eq(lmp), any(Time.class), any(Time.class));
    }

    @Test
    public void shouldRegisterAOutOfAreaMother() {
        LocalDate lmp = today();

        String thaayiCardNumber = "THAAYI-CARD-NUMBER-1";
        String motherName = "Theresa";
        OutOfAreaANCRegistrationRequest request = new OutOfAreaANCRegistrationRequest("CASE X", motherName, "Husband 1", "ANM X", "Village X", "SubCenter X", "PHC X", thaayiCardNumber, lmp.toString(), "9876543210");
        Map<String, Map<String, String>> extraData = create("details", mapOf("some_field", "some_value")).put(REPORT_EXTRA_DATA_KEY_NAME, Collections.<String, String>emptyMap()).map();
        EligibleCouple couple = new EligibleCouple("EC-CASE-1", "EC Number 1").withANMIdentifier("ANM ID 1").withCouple(motherName, "Husband 1").withLocation("bherya", "Sub Center", "PHC X").asOutOfArea();

        service.registerOutOfAreaANC(request, couple, extraData);
        Map<String, String> details = extraData.get("details");

        verify(mothers).register(objectWithSameFieldsAs(new Mother("CASE X", "EC-CASE-1", thaayiCardNumber)
                .withAnm(request.anmIdentifier()).withLMP(lmp)
                .withLocation("Village X", "SubCenter X", "PHC X").withDetails(details)));
        verify(ancSchedulesService).enrollMother(eq("CASE X"), eq(lmp), any(Time.class), any(Time.class));
        verify(actionService).registerOutOfAreaANC(request.caseId(), couple.caseId(), request.wife(), request.husband(), request.anmIdentifier(), request.village(), request.subCenter(),
                request.phc(), request.thaayiCardNumber(), request.lmpDate(), details);
    }

    @Test
    public void shouldTellANCSchedulesServiceWhenANCCareHasBeenProvided() {
        when(mothers.motherExists("CASE-X")).thenReturn(true);
        when(mothers.updateDetails(eq("CASE-X"), any(Map.class))).thenReturn(new Mother("CASE-X", "EC-CASE-1", "TC 1"));

        AnteNatalCareInformation ancInformation = new AnteNatalCareInformation("CASE-X", "ANM 1", 2, "2012-01-23");
        service.ancHasBeenProvided(ancInformation, EXTRA_DATA_EMPTY);

        verify(ancSchedulesService).ancVisitHasHappened(ancInformation);
    }

    @Test
    public void shouldNotConsiderAVisitAsANCWhenVisitNumberIsZero() {
        when(mothers.motherExists("CASE-X")).thenReturn(true);
        when(mothers.updateDetails(eq("CASE-X"), any(Map.class))).thenReturn(new Mother("CASE-X", "EC-CASE-1", "TC 1"));

        service.ancHasBeenProvided(new AnteNatalCareInformation("CASE-X", "ANM 1", 0, "2012-01-23"), EXTRA_DATA_EMPTY);

        verifyZeroInteractions(ancSchedulesService);
    }

    @Test
    public void shouldTellANCSchedulesServiceThatIFAIsProvidedOnlyWhenNumberOfIFATabletsProvidedIsMoreThanZero() {
        when(mothers.motherExists("CASE-X")).thenReturn(true);
        when(mothers.updateDetails(eq("CASE-X"), any(Map.class))).thenReturn(new Mother("CASE-X", "EC-CASE-1", "TC 1"));

        AnteNatalCareInformation ancInformation = new AnteNatalCareInformation("CASE-X", "ANM 1", 0, "2012-01-23").withNumberOfIFATabletsProvided("10");
        service.ancHasBeenProvided(ancInformation, EXTRA_DATA_EMPTY);

        verify(ancSchedulesService).ifaVisitHasHappened(ancInformation);
    }

    @Test
    public void shouldTellANCSchedulesServiceWhatTTDoseWasProvidedOnlyWhenTTDoseWasProvided() {
        when(mothers.motherExists("CASE-X")).thenReturn(true);
        when(mothers.updateDetails(eq("CASE-X"), any(Map.class))).thenReturn(new Mother("CASE-X", "EC-CASE-1", "TC 1"));

        AnteNatalCareInformation ancInformation = new AnteNatalCareInformation("CASE-X", "ANM 1", 0, "2012-01-23").withNumberOfIFATabletsProvided("10").withTTDose("TT 2");
        service.ancHasBeenProvided(ancInformation, EXTRA_DATA_EMPTY);

        verify(ancSchedulesService).ttVisitHasHappened(ancInformation);
    }

    @Test
    public void shouldNotTellANCSchedulesServiceWhatTTDoseWasProvidedWhenTTDoseWasNotProvided() {
        when(mothers.motherExists("CASE-X")).thenReturn(true);
        when(mothers.updateDetails(eq("CASE-X"), any(Map.class))).thenReturn(new Mother("CASE-X", "EC-CASE-1", "TC 1"));

        AnteNatalCareInformation ancInformation = new AnteNatalCareInformation("CASE-X", "ANM 1", 0, "2012-01-23").withNumberOfIFATabletsProvided("10");
        service.ancHasBeenProvided(ancInformation, EXTRA_DATA_EMPTY);

        verify(ancSchedulesService, times(0)).ttVisitHasHappened(ancInformation);
    }

    @Test
    public void shouldReportANCVisit() {
        AnteNatalCareInformation ancInformation = new AnteNatalCareInformation("CASE-X", "ANM 1", 0, "2012-01-23").withNumberOfIFATabletsProvided("10").withTTDose("TT 2");
        when(mothers.motherExists("CASE-X")).thenReturn(true);
        when(mothers.updateDetails(eq("CASE-X"), any(Map.class))).thenReturn(new Mother("CASE-X", "EC-CASE-1", "TC 1"));

        service.ancHasBeenProvided(ancInformation, EXTRA_DATA);

        verify(motherReportingService).ancHasBeenProvided(new SafeMap(EXTRA_DATA.get("reporting")));
    }

    @Test
    public void shouldNotTellANCSchedulesServiceThatIFAIsProvidedWhenNumberOfIFATabletsProvidedIsEmpty() {
        when(mothers.motherExists("CASE-X")).thenReturn(true);
        when(mothers.updateDetails(eq("CASE-X"), any(Map.class))).thenReturn(new Mother("CASE-X", "EC-CASE-1", "TC 1"));

        AnteNatalCareInformation ancInformation = new AnteNatalCareInformation("CASE-X", "ANM 1", 0, "2012-01-23").withNumberOfIFATabletsProvided("");
        service.ancHasBeenProvided(ancInformation, EXTRA_DATA_EMPTY);

        verify(ancSchedulesService, times(0)).ifaVisitHasHappened(ancInformation);
    }

    @Test
    public void shouldNotTryAndFulfillMilestoneWhenANCCareIsProvidedToAMotherWhoIsNotRegisteredInTheSystem() {
        when(mothers.motherExists("CASE-UNKNOWN-MOM")).thenReturn(false);

        service.ancHasBeenProvided(new AnteNatalCareInformation("CASE-UNKNOWN-MOM", "ANM 1", 0, "2012-01-23"), EXTRA_DATA_EMPTY);

        verifyZeroInteractions(ancSchedulesService);
    }

    @Test
    public void shouldUpdateDetailsAndSendTheUpdatedDetailsAsAnAction() throws Exception {
        Map<String, String> detailsBeforeUpdate = EXTRA_DATA.get("details");
        Map<String, String> updatedDetails = mapOf("someNewKey", "someNewValue");

        when(mothers.motherExists("CASE-X")).thenReturn(true);
        when(mothers.updateDetails("CASE-X", detailsBeforeUpdate)).thenReturn(new Mother("CASE-X", "EC-CASE-1", "TC 1").withAnm("ANM X").withDetails(updatedDetails));

        service.ancHasBeenProvided(new AnteNatalCareInformation("CASE-X", "ANM X", 1, today().toString()).withNumberOfIFATabletsProvided("10").withTTDose("TT DOSE"), EXTRA_DATA);

        verify(mothers).updateDetails("CASE-X", detailsBeforeUpdate);
        verify(actionService).updateMotherDetails("CASE-X", "ANM X", updatedDetails);
        verify(actionService).ancCareProvided("CASE-X", "ANM X", 1, today(), 10, "TT DOSE", detailsBeforeUpdate);
    }

    @Test
    public void shouldUnEnrollAMotherFromScheduleWhenANCCaseIsClosed() {
        when(mothers.motherExists("CASE-X")).thenReturn(true);

        service.closeANCCase(new AnteNatalCareCloseInformation("CASE-X", "ANM X", "Abort"), new SafeMap());

        verify(ancSchedulesService).unEnrollFromSchedules("CASE-X");
        verify(actionService).closeMother("CASE-X", "ANM X", "Abort");
    }

    @Test
    public void shouldCloseAMotherWhenANCCaseIsClosed() {
        when(mothers.motherExists("CASE-X")).thenReturn(true);

        service.closeANCCase(new AnteNatalCareCloseInformation("CASE-X", "ANM X", "Abort"), new SafeMap());

        verify(mothers).close("CASE-X");
    }

    @Test
    public void shouldNotUnEnrollAMotherFromScheduleWhenSheIsNotRegistered() {
        when(mothers.motherExists("CASE-UNKNOWN-MOM")).thenReturn(false);

        service.closeANCCase(new AnteNatalCareCloseInformation("CASE-UNKNOWN-MOM", "ANM X", null), new SafeMap());

        verifyZeroInteractions(ancSchedulesService);
    }

    @Test
    public void shouldCloseECCaseAlsoWhenPNCCaseIsClosedAndReasonIsDeath() {
        when(mothers.motherExists("CASE-X")).thenReturn(true);

        service.closeANCCase(new AnteNatalCareCloseInformation("CASE-X", "ANM X", "death_of_woman"), new SafeMap());

        verify(ecService).closeEligibleCouple(new EligibleCoupleCloseRequest("CASE-X", "ANM X"));
    }

    @Test
    public void shouldCloseECCaseAlsoWhenPNCCaseIsClosedAndReasonIsPermanentRelocation() {
        when(mothers.motherExists("CASE-X")).thenReturn(true);

        service.closeANCCase(new AnteNatalCareCloseInformation("CASE-X", "ANM X", "relocation_permanent"), new SafeMap());

        verify(ecService).closeEligibleCouple(new EligibleCoupleCloseRequest("CASE-X", "ANM X"));
    }

    @Test
    public void shouldNotCloseECCaseWhenPNCCaseIsClosedAndReasonIsNeitherDeathOrPermanentRelocation() {
        when(mothers.motherExists("CASE-X")).thenReturn(true);

        service.closeANCCase(new AnteNatalCareCloseInformation("CASE-X", "ANM X", "other_reason"), new SafeMap());

        verifyZeroInteractions(ecService);
    }

    @Test
    public void shouldUnEnrollMotherFromANCSchedulesWhenDeliveryOutcomeFormIsFilled() throws Exception {
        when(mothers.motherExists("MOTHER-CASE-1")).thenReturn(true);
        when(mothers.updateDetails("MOTHER-CASE-1", EXTRA_DATA.get("details"))).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1").withDetails(EXTRA_DATA.get("details")));

        service.updatePregnancyOutcome(new AnteNatalCareOutcomeInformation("MOTHER-CASE-1", "ANM X", "live_birth", "2012-01-01", "yes", "0"), EXTRA_DATA);

        verify(ancSchedulesService).unEnrollFromSchedules("MOTHER-CASE-1");
    }

    @Test
    public void shouldUpdateMotherDetailsWhenDeliveryOutcomeFormIsFilled() throws Exception {
        when(mothers.motherExists("MOTHER-CASE-1")).thenReturn(true);
        when(mothers.updateDetails("MOTHER-CASE-1", EXTRA_DATA.get("details"))).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1").withDetails(EXTRA_DATA.get("details")));

        service.updatePregnancyOutcome(new AnteNatalCareOutcomeInformation("MOTHER-CASE-1", "ANM X", "live_birth", "2012-01-01", "yes", "0"), EXTRA_DATA);

        verify(mothers).updateDetails("MOTHER-CASE-1", EXTRA_DATA.get("details"));
    }

    @Test
    public void shouldUpdateMotherDeliveryDetailsWhenDeliveryOutcomeFormIsFilled() throws Exception {
        when(mothers.motherExists("MOTHER-CASE-1")).thenReturn(true);
        when(mothers.updateDetails("MOTHER-CASE-1", EXTRA_DATA.get("details"))).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1").withDetails(EXTRA_DATA.get("details")));

        service.updatePregnancyOutcome(new AnteNatalCareOutcomeInformation("MOTHER-CASE-1", "ANM X", "live_birth", "2012-01-01", "yes", "0"), EXTRA_DATA);

        verify(mothers).updateDeliveryOutcomeFor("MOTHER-CASE-1", "2012-01-01");
    }

    @Test
    public void shouldReportWhenDeliveryOutcomeFormIsFilled() throws Exception {
        when(mothers.motherExists("MOTHER-CASE-1")).thenReturn(true);
        when(mothers.updateDetails("MOTHER-CASE-1", EXTRA_DATA.get("details"))).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1").withDetails(EXTRA_DATA.get("details")));

        service.updatePregnancyOutcome(new AnteNatalCareOutcomeInformation("MOTHER-CASE-1", "ANM X", "live_birth", "2012-01-01", "yes", "0"), EXTRA_DATA);

        verify(motherReportingService).updatePregnancyOutcome(new SafeMap(EXTRA_DATA.get("reporting")));
    }

    @Test
    public void shouldIgnoreDeliveryOutcomeUploadIfThereIsNoCorrespondingMotherInRepo() throws Exception {
        when(mothers.motherExists("CASE-X")).thenReturn(false);

        service.updatePregnancyOutcome(new AnteNatalCareOutcomeInformation("MOTHER-CASE-1", "ANM X", "live_birth", "2012-01-01", "yes", "0"), EXTRA_DATA);

        verifyZeroInteractions(ancSchedulesService);
        verifyZeroInteractions(actionService);
    }

    @Test
    public void shouldUpdateMotherDetailsAndSendAnActionWhenBirthPlanningDetailsAreUpdateAndMotherExists() throws Exception {
        when(mothers.motherExists("CASE X")).thenReturn(true);
        Map<String, String> updatedDetails = mapOf("aNewKey", "aNewValue");
        when(mothers.updateDetails("CASE X", EXTRA_DATA.get("details"))).thenReturn(new Mother("CASE X", "EC-CASE-1", "TC X").withDetails(updatedDetails));

        service.updateBirthPlanning(new BirthPlanningRequest("CASE X", "ANM X"), EXTRA_DATA);

        verify(mothers).updateDetails("CASE X", EXTRA_DATA.get("details"));
        verify(actionService).updateBirthPlanning("CASE X", "ANM X", updatedDetails);
    }

    @Test
    public void shouldNotSendBirthPlanningUpdatesAsActionWhenMotherNotFoundInDrishti() throws Exception {
        when(mothers.motherExists("CASE X")).thenReturn(false);

        service.updateBirthPlanning(new BirthPlanningRequest("CASE X", "ANM X"), EXTRA_DATA);

        verifyZeroInteractions(actionService);
        verify(mothers, times(0)).updateDetails(any(String.class), any(Map.class));
    }

    @Test
    public void shouldReportSubsetOfANCUpdate() throws Exception {
        when(mothers.motherExists("CASE X")).thenReturn(true);

        service.updateSubsetOfANCInformation(new AnteNatalCareInformationSubset("CASE X", "ANM X"), EXTRA_DATA);

        verify(motherReportingService).subsetOfANCHasBeenProvided(new SafeMap(EXTRA_DATA.get("reporting")));
    }

    @Test
    public void shouldNotReportSubsetOfANCUpdateWhenMotherNotFoundInDrishti() throws Exception {
        when(mothers.motherExists("CASE X")).thenReturn(false);

        service.updateSubsetOfANCInformation(new AnteNatalCareInformationSubset("CASE X", "ANM X"), EXTRA_DATA);

        verifyZeroInteractions(motherReportingService);
    }
}
