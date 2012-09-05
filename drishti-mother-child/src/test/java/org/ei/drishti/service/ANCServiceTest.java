package org.ei.drishti.service;

import org.ei.drishti.contract.AnteNatalCareCloseInformation;
import org.ei.drishti.contract.AnteNatalCareEnrollmentInformation;
import org.ei.drishti.contract.AnteNatalCareInformation;
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
        Map<String, Map<String, String>> extraData = create("details", mapOf("some_field", "some_value")).put("reporting", Collections.<String, String>emptyMap()).map();

        service.registerANCCase(enrollmentInfo, extraData);
        Map<String, String> details = extraData.get("details");

        verify(motherReportingService).registerANC(new SafeMap(extraData.get("reporting")), "bherya", "Sub Center");
        verify(mothers).register(objectWithSameFieldsAs(new Mother("CASE-1", thaayiCardNumber, motherName)
                .withAnm(enrollmentInfo.anmIdentifier(), "12345").withLMP(lmp).withECNumber("EC Number 1")
                .withLocation("bherya", "Sub Center", "PHC X").withDetails(details)));
        verify(actionService).registerPregnancy("CASE-1", "EC-CASE-1", thaayiCardNumber, "ANM ID 1", lmp, details);
    }

    @Test
    public void shouldNotRegisterAMotherIfTheECIsNotFound() {
        AnteNatalCareEnrollmentInformation enrollmentInfo = new AnteNatalCareEnrollmentInformation("CASE-1", "EC-CASE-1", "THAAYI-CARD-NUMBER-1", "12345", "ANM ID 1", null);
        when(eligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(null);

        service.registerANCCase(enrollmentInfo, mapOf("reporting", Collections.<String, String>emptyMap()));

        verifyZeroInteractions(mothers);
        verifyZeroInteractions(ancSchedulesService);
        verifyZeroInteractions(actionService);
    }

    @Test
    public void shouldEnrollAMotherIntoDefaultScheduleDuringEnrollmentBasedOnLMP() {
        LocalDate lmp = today().minusDays(2);

        final String thaayiCardNumber = "THAAYI-CARD-NUMBER-1";
        String motherName = "Theresa";
        AnteNatalCareEnrollmentInformation enrollmentInfo = new AnteNatalCareEnrollmentInformation("CASE-1", "EC-CASE-1", thaayiCardNumber, "12345", "ANM ID 1", lmp.toDate());
        when(eligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple("EC-CASE-1", "EC Number 1").withANMIdentifier("ANM ID 1").withCouple(motherName, "Husband 1").withLocation("bherya", "Sub Center", "PHC X"));

        service.registerANCCase(enrollmentInfo, create("reporting", Collections.<String, String>emptyMap()).put("details", Collections.<String, String>emptyMap()).map());

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
        extraData.put("reporting", new HashMap<String, String>());
        service.registerANCCase(enrollmentInfo, extraData);

        verify(ancSchedulesService).enrollMother(eq("CASE-1"), eq(today()), any(Time.class), any(Time.class));
    }

    @Test
    public void shouldTellANCSchedulesServiceWhenANCCareHasBeenProvided() {
        when(mothers.motherExists("CASE-X")).thenReturn(true);
        when(mothers.updateDetails(eq("CASE-X"), any(Map.class))).thenReturn(new Mother("CASE-X", "TC 1", "SomeName"));

        AnteNatalCareInformation ancInformation = new AnteNatalCareInformation("CASE-X", "ANM 1", 2);
        service.ancCareHasBeenProvided(ancInformation, EXTRA_DATA_EMPTY);

        verify(ancSchedulesService).ancVisitHasHappened(ancInformation);
    }

    @Test
    public void shouldNotConsiderAVisitAsANCWhenVisitNumberIsZero() {
        when(mothers.motherExists("CASE-X")).thenReturn(true);
        when(mothers.updateDetails(eq("CASE-X"), any(Map.class))).thenReturn(new Mother("CASE-X", "TC 1", "SomeName"));

        service.ancCareHasBeenProvided(new AnteNatalCareInformation("CASE-X", "ANM 1", 0), EXTRA_DATA_EMPTY);

        verifyZeroInteractions(ancSchedulesService);
    }

    @Test
    public void shouldTellANCSchedulesServiceThatIFAIsProvidedOnlyWhenNumberOfIFATabletsProvidedIsMoreThanZero() {
        when(mothers.motherExists("CASE-X")).thenReturn(true);
        when(mothers.updateDetails(eq("CASE-X"), any(Map.class))).thenReturn(new Mother("CASE-X", "TC 1", "SomeName"));

        AnteNatalCareInformation ancInformation = new AnteNatalCareInformation("CASE-X", "ANM 1", 0).withNumberOfIFATabletsProvided(10);
        service.ancCareHasBeenProvided(ancInformation, EXTRA_DATA_EMPTY);

        verify(ancSchedulesService).ifaVisitHasHappened(ancInformation);
    }

    @Test
    public void shouldNotTryAndFulfillMilestoneWhenANCCareIsProvidedToAMotherWhoIsNotRegisteredInTheSystem() {
        when(mothers.motherExists("CASE-UNKNOWN-MOM")).thenReturn(false);

        service.ancCareHasBeenProvided(new AnteNatalCareInformation("CASE-UNKNOWN-MOM", "ANM 1", 0), EXTRA_DATA_EMPTY);

        verifyZeroInteractions(ancSchedulesService);
    }

    @Test
    public void shouldUpdateDetailsAndSendTheUpdatedDetailsAsAnAction() throws Exception {
        Map<String, String> detailsBeforeUpdate = EXTRA_DATA.get("details");
        Map<String, String> updatedDetails = mapOf("someNewKey", "someNewValue");

        when(mothers.motherExists("CASE-X")).thenReturn(true);
        when(mothers.updateDetails("CASE-X", detailsBeforeUpdate)).thenReturn(new Mother("CASE-X", "TC 1", "SomeName").withAnm("ANM X", "1234").withDetails(updatedDetails));

        service.ancCareHasBeenProvided(new AnteNatalCareInformation("CASE-X", "ANM X", 1).withNumberOfIFATabletsProvided(10), EXTRA_DATA);

        verify(mothers).updateDetails("CASE-X", detailsBeforeUpdate);
        verify(actionService).updateMotherDetails("CASE-X", "ANM X", updatedDetails);
        verify(actionService).ancCareProvided("CASE-X", "ANM X", 1, today(), 10);
    }

    @Test
    public void shouldUnEnrollAMotherFromScheduleWhenANCCaseIsClosed() {
        when(mothers.motherExists("CASE-X")).thenReturn(true);

        service.closeANCCase(new AnteNatalCareCloseInformation("CASE-X", "ANM X", "Abort"), new SafeMap());

        verify(ancSchedulesService).closeCase("CASE-X");
        verify(actionService).closeANC("CASE-X", "ANM X", "Abort");
    }

    @Test
    public void shouldNotUnEnrollAMotherFromScheduleWhenSheIsNotRegistered() {
        when(mothers.motherExists("CASE-UNKNOWN-MOM")).thenReturn(false);

        service.closeANCCase(new AnteNatalCareCloseInformation("CASE-UNKNOWN-MOM", "ANM X", null), new SafeMap());

        verifyZeroInteractions(ancSchedulesService);
    }
}
