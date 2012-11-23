package org.ei.drishti.service;

import org.ei.drishti.contract.*;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.repository.AllMothers;
import org.ei.drishti.service.reporting.MotherReportingService;
import org.ei.drishti.util.SafeMap;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.motechproject.model.Time;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.ei.drishti.common.AllConstants.Report.REPORT_EXTRA_MAPS_KEY_NAME;
import static org.ei.drishti.util.EasyMap.create;
import static org.ei.drishti.util.EasyMap.mapOf;
import static org.ei.drishti.util.Matcher.objectWithSameFieldsAs;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.util.DateUtil.today;

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

    private ANCService service;

    private Map<String, Map<String, String>> EXTRA_DATA_EMPTY = new HashMap<>();
    private Map<String, Map<String, String>> EXTRA_DATA = mapOf("details", mapOf("someKey", "someValue"));

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        service = new ANCService(mothers, eligibleCouples, ancSchedulesService, actionService, motherReportingService);
    }

    @Test
    public void shouldSaveAMothersInformationDuringEnrollment() {
        LocalDate lmp = today();

        String thaayiCardNumber = "THAAYI-CARD-NUMBER-1";
        String motherName = "Theresa";
        AnteNatalCareEnrollmentInformation enrollmentInfo = new AnteNatalCareEnrollmentInformation("CASE-1", "EC-CASE-1", thaayiCardNumber, "12345", "ANM ID 1", lmp.toDate());
        when(eligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple("EC-CASE-1", "EC Number 1").withANMIdentifier("ANM ID 1").withCouple(motherName, "Husband 1").withLocation("bherya", "Sub Center", "PHC X"));
        Map<String, Map<String, String>> extraData = create("details", mapOf("some_field", "some_value")).put(REPORT_EXTRA_MAPS_KEY_NAME, Collections.<String, String>emptyMap()).map();

        service.registerANCCase(enrollmentInfo, extraData);
        Map<String, String> details = extraData.get("details");

        verify(motherReportingService).registerANC(new SafeMap(extraData.get(REPORT_EXTRA_MAPS_KEY_NAME)), "bherya", "Sub Center");
        verify(mothers).register(objectWithSameFieldsAs(new Mother("CASE-1", "EC-CASE-1", thaayiCardNumber, motherName)
                .withAnm(enrollmentInfo.anmIdentifier(), "12345").withLMP(lmp)
                .withLocation("bherya", "Sub Center", "PHC X").withDetails(details)));
        verify(actionService).registerPregnancy("CASE-1", "EC-CASE-1", thaayiCardNumber, "ANM ID 1", lmp, details);
    }

    @Test
    public void shouldNotRegisterAMotherIfTheECIsNotFound() {
        AnteNatalCareEnrollmentInformation enrollmentInfo = new AnteNatalCareEnrollmentInformation("CASE-1", "EC-CASE-1", "THAAYI-CARD-NUMBER-1", "12345", "ANM ID 1", null);
        when(eligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(null);

        service.registerANCCase(enrollmentInfo, mapOf(REPORT_EXTRA_MAPS_KEY_NAME, Collections.<String, String>emptyMap()));

        verifyZeroInteractions(mothers);
        verifyZeroInteractions(ancSchedulesService);
        verifyZeroInteractions(actionService);
    }

    @Test
    public void shouldRegisterAOutOfAreaMother() {
        LocalDate lmp = today();

        String thaayiCardNumber = "THAAYI-CARD-NUMBER-1";
        String motherName = "Theresa";
        OutOfAreaANCRegistrationRequest request = new OutOfAreaANCRegistrationRequest("CASE X", motherName, "Husband 1", "ANM X", "Village X", "SubCenter X", "PHC X", thaayiCardNumber, lmp.toString(), "9876543210");
        Map<String, Map<String, String>> extraData = create("details", mapOf("some_field", "some_value")).put(REPORT_EXTRA_MAPS_KEY_NAME, Collections.<String, String>emptyMap()).map();
        EligibleCouple couple = new EligibleCouple("EC-CASE-1", "EC Number 1").withANMIdentifier("ANM ID 1").withCouple(motherName, "Husband 1").withLocation("bherya", "Sub Center", "PHC X").asOutOfArea();

        service.registerOutOfAreaANC(request, couple, extraData);
        Map<String, String> details = extraData.get("details");

        verify(mothers).register(objectWithSameFieldsAs(new Mother("CASE X", "EC-CASE-1", thaayiCardNumber, motherName)
                .withAnm(request.anmIdentifier(), "9876543210").withLMP(lmp)
                .withLocation("Village X", "SubCenter X", "PHC X").withDetails(details)));
        verify(ancSchedulesService).enrollMother(eq("CASE X"), eq(lmp), any(Time.class), any(Time.class));
        verify(actionService).registerOutOfAreaANC(request.caseId(), couple.caseId(), request.wife(), request.husband(), request.anmIdentifier(), request.village(), request.subCenter(),
                request.phc(), request.thaayiCardNumber(), request.lmpDate(), details);
    }

    @Test
    public void shouldEnrollAMotherIntoDefaultScheduleDuringEnrollmentBasedOnLMP() {
        LocalDate lmp = today().minusDays(2);

        final String thaayiCardNumber = "THAAYI-CARD-NUMBER-1";
        String motherName = "Theresa";
        AnteNatalCareEnrollmentInformation enrollmentInfo = new AnteNatalCareEnrollmentInformation("CASE-1", "EC-CASE-1", thaayiCardNumber, "12345", "ANM ID 1", lmp.toDate());
        when(eligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple("EC-CASE-1", "EC Number 1").withANMIdentifier("ANM ID 1").withCouple(motherName, "Husband 1").withLocation("bherya", "Sub Center", "PHC X"));

        service.registerANCCase(enrollmentInfo, create(REPORT_EXTRA_MAPS_KEY_NAME, Collections.<String, String>emptyMap()).put("details", Collections.<String, String>emptyMap()).map());

        InOrder inOrder = inOrder(actionService, ancSchedulesService);
        inOrder.verify(actionService).registerPregnancy("CASE-1", "EC-CASE-1", thaayiCardNumber, "ANM ID 1", lmp, Collections.<String, String>emptyMap());
        inOrder.verify(ancSchedulesService).enrollMother(eq("CASE-1"), eq(lmp), any(Time.class), any(Time.class));
    }

    @Test
    public void shouldEnrollAMotherUsingCurrentDateIfLMPDateIsNotFound() {
        final String thaayiCardNumber = "THAAYI-CARD-NUMBER-1";
        String motherName = "Theresa";
        AnteNatalCareEnrollmentInformation enrollmentInfo = new AnteNatalCareEnrollmentInformation("CASE-1", "EC-CASE-1", thaayiCardNumber, "12345", "ANM ID 1", null);
        when(eligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple("EC-CASE-1", "EC Number 1").withANMIdentifier("ANM ID 1").withCouple(motherName, "Husband 1").withLocation("bherya", "Sub Center", "PHC X"));

        HashMap<String, Map<String, String>> extraData = new HashMap<>();
        extraData.put(REPORT_EXTRA_MAPS_KEY_NAME, new HashMap<String, String>());
        service.registerANCCase(enrollmentInfo, extraData);

        verify(ancSchedulesService).enrollMother(eq("CASE-1"), eq(today()), any(Time.class), any(Time.class));
    }

    @Test
    public void shouldTellANCSchedulesServiceWhenANCCareHasBeenProvided() {
        when(mothers.motherExists("CASE-X")).thenReturn(true);
        when(mothers.updateDetails(eq("CASE-X"), any(Map.class))).thenReturn(new Mother("CASE-X", "EC-CASE-1", "TC 1", "SomeName"));

        AnteNatalCareInformation ancInformation = new AnteNatalCareInformation("CASE-X", "ANM 1", 2, "2012-01-23");
        service.ancCareHasBeenProvided(ancInformation, EXTRA_DATA_EMPTY);

        verify(ancSchedulesService).ancVisitHasHappened(ancInformation);
    }

    @Test
    public void shouldNotConsiderAVisitAsANCWhenVisitNumberIsZero() {
        when(mothers.motherExists("CASE-X")).thenReturn(true);
        when(mothers.updateDetails(eq("CASE-X"), any(Map.class))).thenReturn(new Mother("CASE-X", "EC-CASE-1", "TC 1", "SomeName"));

        service.ancCareHasBeenProvided(new AnteNatalCareInformation("CASE-X", "ANM 1", 0, "2012-01-23"), EXTRA_DATA_EMPTY);

        verifyZeroInteractions(ancSchedulesService);
    }

    @Test
    public void shouldTellANCSchedulesServiceThatIFAIsProvidedOnlyWhenNumberOfIFATabletsProvidedIsMoreThanZero() {
        when(mothers.motherExists("CASE-X")).thenReturn(true);
        when(mothers.updateDetails(eq("CASE-X"), any(Map.class))).thenReturn(new Mother("CASE-X", "EC-CASE-1", "TC 1", "SomeName"));

        AnteNatalCareInformation ancInformation = new AnteNatalCareInformation("CASE-X", "ANM 1", 0, "2012-01-23").withNumberOfIFATabletsProvided("10");
        service.ancCareHasBeenProvided(ancInformation, EXTRA_DATA_EMPTY);

        verify(ancSchedulesService).ifaVisitHasHappened(ancInformation);
    }

    @Test
    public void shouldTellANCSchedulesServiceWhatTTDoseWasProvidedOnlyWhenTTDoseWasProvided() {
        when(mothers.motherExists("CASE-X")).thenReturn(true);
        when(mothers.updateDetails(eq("CASE-X"), any(Map.class))).thenReturn(new Mother("CASE-X", "EC-CASE-1", "TC 1", "SomeName"));

        AnteNatalCareInformation ancInformation = new AnteNatalCareInformation("CASE-X", "ANM 1", 0, "2012-01-23").withNumberOfIFATabletsProvided("10").withTTDose("TT 2");
        service.ancCareHasBeenProvided(ancInformation, EXTRA_DATA_EMPTY);

        verify(ancSchedulesService).ttVisitHasHappened(ancInformation);
    }

    @Test
    public void shouldNotTellANCSchedulesServiceWhatTTDoseWasProvidedWhenTTDoseWasNotProvided() {
        when(mothers.motherExists("CASE-X")).thenReturn(true);
        when(mothers.updateDetails(eq("CASE-X"), any(Map.class))).thenReturn(new Mother("CASE-X", "EC-CASE-1", "TC 1", "SomeName"));

        AnteNatalCareInformation ancInformation = new AnteNatalCareInformation("CASE-X", "ANM 1", 0, "2012-01-23").withNumberOfIFATabletsProvided("10");
        service.ancCareHasBeenProvided(ancInformation, EXTRA_DATA_EMPTY);

        verify(ancSchedulesService, times(0)).ttVisitHasHappened(ancInformation);
    }

    @Test
    public void shouldNotTellANCSchedulesServiceThatIFAIsProvidedWhenNumberOfIFATabletsProvidedIsEmpty() {
        when(mothers.motherExists("CASE-X")).thenReturn(true);
        when(mothers.updateDetails(eq("CASE-X"), any(Map.class))).thenReturn(new Mother("CASE-X", "EC-CASE-1", "TC 1", "SomeName"));

        AnteNatalCareInformation ancInformation = new AnteNatalCareInformation("CASE-X", "ANM 1", 0, "2012-01-23").withNumberOfIFATabletsProvided("");
        service.ancCareHasBeenProvided(ancInformation, EXTRA_DATA_EMPTY);

        verify(ancSchedulesService, times(0)).ifaVisitHasHappened(ancInformation);
    }

    @Test
    public void shouldNotTryAndFulfillMilestoneWhenANCCareIsProvidedToAMotherWhoIsNotRegisteredInTheSystem() {
        when(mothers.motherExists("CASE-UNKNOWN-MOM")).thenReturn(false);

        service.ancCareHasBeenProvided(new AnteNatalCareInformation("CASE-UNKNOWN-MOM", "ANM 1", 0, "2012-01-23"), EXTRA_DATA_EMPTY);

        verifyZeroInteractions(ancSchedulesService);
    }

    @Test
    public void shouldUpdateDetailsAndSendTheUpdatedDetailsAsAnAction() throws Exception {
        Map<String, String> detailsBeforeUpdate = EXTRA_DATA.get("details");
        Map<String, String> updatedDetails = mapOf("someNewKey", "someNewValue");

        when(mothers.motherExists("CASE-X")).thenReturn(true);
        when(mothers.updateDetails("CASE-X", detailsBeforeUpdate)).thenReturn(new Mother("CASE-X", "EC-CASE-1", "TC 1", "SomeName").withAnm("ANM X", "1234").withDetails(updatedDetails));

        service.ancCareHasBeenProvided(new AnteNatalCareInformation("CASE-X", "ANM X", 1, today().toString()).withNumberOfIFATabletsProvided("10").withTTDose("TT DOSE"), EXTRA_DATA);

        verify(mothers).updateDetails("CASE-X", detailsBeforeUpdate);
        verify(actionService).updateMotherDetails("CASE-X", "ANM X", updatedDetails);
        verify(actionService).ancCareProvided("CASE-X", "ANM X", 1, today(), 10, "TT DOSE", detailsBeforeUpdate);
    }

    @Test
    public void shouldUnEnrollAMotherFromScheduleWhenANCCaseIsClosed() {
        when(mothers.motherExists("CASE-X")).thenReturn(true);

        service.closeANCCase(new AnteNatalCareCloseInformation("CASE-X", "ANM X", "Abort"), new SafeMap());

        verify(ancSchedulesService).unEnrollFromSchedules("CASE-X");
        verify(actionService).closeANC("CASE-X", "ANM X", "Abort");
    }

    @Test
    public void shouldNotUnEnrollAMotherFromScheduleWhenSheIsNotRegistered() {
        when(mothers.motherExists("CASE-UNKNOWN-MOM")).thenReturn(false);

        service.closeANCCase(new AnteNatalCareCloseInformation("CASE-UNKNOWN-MOM", "ANM X", null), new SafeMap());

        verifyZeroInteractions(ancSchedulesService);
    }

    @Test
    public void shouldUnEnrollMotherFromANCSchedulesWhenDeliveryOutcomeFormIsFilled() throws Exception {
        when(mothers.motherExists("MOTHER-CASE-1")).thenReturn(true);
        when(mothers.updateDetails("MOTHER-CASE-1", EXTRA_DATA.get("details"))).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1", "Theresa").withDetails(EXTRA_DATA.get("details")));

        service.updatePregnancyOutcome(new AnteNatalCareOutcomeInformation("MOTHER-CASE-1", "ANM X", "live_birth", "2012-01-01", "0"), EXTRA_DATA);

        verify(ancSchedulesService).unEnrollFromSchedules("MOTHER-CASE-1");
    }

    @Test
    public void shouldUpdateMotherDetailsWhenDeliveryOutcomeFormIsFilled() throws Exception {
        when(mothers.motherExists("MOTHER-CASE-1")).thenReturn(true);
        when(mothers.updateDetails("MOTHER-CASE-1", EXTRA_DATA.get("details"))).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1", "Theresa").withDetails(EXTRA_DATA.get("details")));

        service.updatePregnancyOutcome(new AnteNatalCareOutcomeInformation("MOTHER-CASE-1", "ANM X", "live_birth", "2012-01-01", "0"), EXTRA_DATA);

        verify(mothers).updateDetails("MOTHER-CASE-1", EXTRA_DATA.get("details"));
    }

    @Test
    public void shouldIgnoreDeliveryOutcomeUploadIfThereIsNoCorrespondingMotherInRepo() throws Exception {
        when(mothers.motherExists("CASE-X")).thenReturn(false);

        service.updatePregnancyOutcome(new AnteNatalCareOutcomeInformation("MOTHER-CASE-1", "ANM X", "live_birth", "2012-01-01", "0"), EXTRA_DATA);

        verifyZeroInteractions(ancSchedulesService);
        verifyZeroInteractions(actionService);
    }

    @Test
    public void shouldUpdateMotherDetailsAndSendAnActionWhenBirthPlanningDetailsAreUpdateAndMotherExists() throws Exception {
        when(mothers.motherExists("CASE X")).thenReturn(true);
        Map<String, String> updatedDetails = mapOf("aNewKey", "aNewValue");
        when(mothers.updateDetails("CASE X", EXTRA_DATA.get("details"))).thenReturn(new Mother("CASE X", "EC-CASE-1", "TC X", "Theresa").withDetails(updatedDetails));

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
}
