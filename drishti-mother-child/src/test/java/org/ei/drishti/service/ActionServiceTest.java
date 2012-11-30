package org.ei.drishti.service;

import com.google.gson.Gson;
import org.ei.drishti.domain.Action;
import org.ei.drishti.domain.Child;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.dto.ActionData;
import org.ei.drishti.dto.MonthSummaryDatum;
import org.ei.drishti.repository.AllActions;
import org.ei.drishti.repository.AllChildren;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.repository.AllMothers;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.util.DateUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.ei.drishti.dto.AlertPriority.normal;
import static org.ei.drishti.dto.AlertPriority.urgent;
import static org.ei.drishti.dto.BeneficiaryType.child;
import static org.ei.drishti.dto.BeneficiaryType.ec;
import static org.ei.drishti.dto.BeneficiaryType.mother;
import static org.ei.drishti.util.EasyMap.mapOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ActionServiceTest {
    @Mock
    private AllActions allActions;
    @Mock
    private AllMothers allMothers;
    @Mock
    private AllChildren allChildren;
    @Mock
    private AllEligibleCouples allEligibleCouples;

    private ActionService service;

    private Map<String, String> EXTRA_DETAILS = mapOf("someKey", "someValue");

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        service = new ActionService(allActions, allMothers, allChildren, allEligibleCouples);
    }

    @Test
    public void shouldSaveAlertActionForMother() throws Exception {
        when(allMothers.findByCaseId("Case X")).thenReturn(new Mother("Case X", "EC-CASE-1", "Thaayi 1", "Theresa").withAnm("ANM ID M", "ANM phone no"));

        DateTime dueDate = DateTime.now().minusDays(1);
        DateTime expiryDate = dueDate.plusWeeks(2);
        service.alertForBeneficiary(mother, "Case X", "ANC 1", normal, dueDate, expiryDate);

        verify(allActions).add(new Action("Case X", "ANM ID M", ActionData.createAlert(mother, "ANC 1", normal, dueDate, expiryDate)));
    }

    @Test
    public void shouldSaveAlertActionForChild() throws Exception {
        when(allChildren.findByCaseId("Case X")).thenReturn(new Child("Case X", "EC-CASE-1", "MOTHER-CASE-1", "Thaayi 1", "Kid", Arrays.<String>asList(), "female").withAnm("ANM ID C"));

        DateTime dueDate = DateTime.now().minusDays(1);
        DateTime expiryDate = dueDate.plusWeeks(2);
        service.alertForBeneficiary(child, "Case X", "OPV", normal, dueDate, expiryDate);

        verify(allActions).add(new Action("Case X", "ANM ID C", ActionData.createAlert(child, "OPV", normal, dueDate, expiryDate)));
    }

    @Test
    public void shouldSaveAlertActionForEC() throws Exception {
        when(allEligibleCouples.findByCaseId("Case X")).thenReturn(new EligibleCouple("Case X", "EC-CASE-1").withANMIdentifier("ANM ID C"));

        DateTime dueDate = DateTime.now().minusDays(1);
        DateTime expiryDate = dueDate.plusWeeks(2);
        service.alertForBeneficiary(ec, "Case X", "FP Complications", urgent, dueDate, expiryDate);

        verify(allActions).add(new Action("Case X", "ANM ID C", ActionData.createAlert(ec, "FP Complications", urgent, dueDate, expiryDate)));
    }

    @Test (expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfBenefiicaryTypeIsUnknown() throws Exception {
        service.alertForBeneficiary(null, "Case X", "FP Complications", urgent, null, null);
    }

    @Test
    public void shouldCreateACloseActionForAVisitOfAChild() throws Exception {
        service.markAlertAsClosedForVisitForChild("Case X", "ANM 1", "OPV 1", "2012-01-01");

        verify(allActions).add(new Action("Case X", "ANM 1", ActionData.markAlertAsClosed("OPV 1", "2012-01-01")));
    }

    @Test
    public void shouldCreateACloseActionForAVisitOfAMother() throws Exception {
        when(allMothers.findByCaseId("Case X")).thenReturn(new Mother("Case X", "EC-CASE-1", "Thaayi 1", "Theresa").withAnm("ANM ID 1", "ANM phone no").withLocation("bherya", "Sub Center", "PHC X"));

        service.markAlertAsClosedForVisitForMother("Case X", "ANM ID 1", "ANC 1", "2012-12-12");

        verify(allActions).add(new Action("Case X", "ANM ID 1", ActionData.markAlertAsClosed("ANC 1", "2012-12-12")));
    }

    @Test
    public void shouldCreateADeleteAllActionForAMother() throws Exception {
        when(allMothers.findByCaseId("Case X")).thenReturn(new Mother("Case X", "EC-CASE-1", "Thaayi 1", "Theresa").withAnm("ANM ID 1", "ANM phone no").withLocation("bherya", "Sub Center", "PHC X"));

        service.deleteAllAlertsForMother("Case X");

        verify(allActions).addWithDelete(new Action("Case X", "ANM ID 1", ActionData.deleteAllAlerts()), "alert");
    }

    @Test
    public void shouldCreateADeleteAllActionForAChild() throws Exception {
        service.deleteAllAlertsForChild("Case X", "DEMO ANM");

        verify(allActions).addWithDelete(new Action("Case X", "DEMO ANM", ActionData.deleteAllAlerts()), "alert");
    }

    @Test
    public void shouldReturnAlertsBasedOnANMIDAndTimeStamp() throws Exception {
        List<Action> alertActions = Arrays.asList(new Action("Case X", "ANM 1", ActionData.createAlert(mother, "ANC 1", normal, DateTime.now(), DateTime.now().plusDays(3))));
        when(allActions.findByANMIDAndTimeStamp("ANM 1", 1010101)).thenReturn(alertActions);

        List<Action> alerts = service.getNewAlertsForANM("ANM 1", 1010101);

        assertEquals(1, alerts.size());
        assertEquals(alertActions, alerts);
    }

    @Test
    public void shouldAddCreateActionForEligibleCoupleRegistration() throws Exception {
        service.registerEligibleCouple("Case X", "EC Number 1", "Wife 1", "Husband 1", "ANM X", "Village X", "SubCenter X", "PHC X", new HashMap<String, String>());

        verify(allActions).add(new Action("Case X", "ANM X", ActionData.createEligibleCouple("Wife 1", "Husband 1", "EC Number 1", "Village X", "SubCenter X", "PHC X", new HashMap<String, String>())));
    }

    @Test
    public void shouldAddDeleteActionForEligibleCoupleClose() throws Exception {
        service.closeEligibleCouple("Case X", "ANM X");

        verify(allActions).addWithDelete(new Action("Case X", "ANM X", ActionData.deleteEligibleCouple()), "alert");
    }

    @Test
    public void shouldAddDeleteActionForChildClose() throws Exception {
        service.closeChild("Case X", "ANM X");

        verify(allActions).add(new Action("Case X", "ANM X", ActionData.deleteChild()));
    }

    @Test
    public void shouldAddActionForBeneficiaryRegistration() throws Exception {
        HashMap<String, String> details = new HashMap<>();
        details.put("some_field", "some_value");

        service.registerPregnancy("Case X", "Case EC 1", "Thaayi 1", "ANM X", DateUtil.today(), details);

        verify(allActions).add(new Action("Case X", "ANM X", ActionData.registerPregnancy("Case EC 1", "Thaayi 1", DateUtil.today(), details)));
    }

    @Test
    public void shouldCloseANCCase() throws Exception {
        service.closeANC("Case X", "ANM X", "delivery");

        verify(allActions).add(new Action("Case X", "ANM X", ActionData.closeANC("delivery")));
    }

    @Test
    public void shouldRegisterChildBirth() throws Exception {
        when(allMothers.findByThaayiCardNumber("MotherThaayiCard 1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "MotherThaayiCard 1", "Theresa"));

        service.registerChildBirth("ChildCase Y", "ANM X", "MOTHER-CASE-1", "MotherThaayiCard 1", DateUtil.today(), "female", EXTRA_DETAILS);

        verify(allActions).add(new Action("ChildCase Y", "ANM X", ActionData.registerChildBirth("MOTHER-CASE-1", "MotherThaayiCard 1", DateUtil.today(), "female", EXTRA_DETAILS)));
    }

    @Test
    public void shouldSendAnUpdateECDetailsAction() throws Exception {
        service.updateEligibleCoupleDetails("CASE X", "ANM X", mapOf("someKey", "someValue"));

        verify(allActions).add(new Action("CASE X", "ANM X", ActionData.updateEligibleCoupleDetails(mapOf("someKey", "someValue"))));
    }

    @Test
    public void shouldSendAnUpdateMotherDetailsAction() throws Exception {
        service.updateMotherDetails("CASE X", "ANM X", mapOf("someKey", "someValue"));

        verify(allActions).add(new Action("CASE X", "ANM X", ActionData.updateMotherDetails(mapOf("someKey", "someValue"))));
    }

    @Test
    public void shouldSendAnANCCareProvidedAction() throws Exception {
        service.ancCareProvided("CASE X", "ANM X", 1, LocalDate.parse("2012-01-01"), 20, "TT DOSE", mapOf("someKey", "someValue"));

        verify(allActions).add(new Action("CASE X", "ANM X", ActionData.ancCareProvided(1, LocalDate.parse("2012-01-01"), 20, "TT DOSE", mapOf("someKey", "someValue"))));
    }

    @Test
    public void shouldAddCreateActionForOutOfAreaANCRegistration() throws Exception {
        Map<String, String> extraData = mapOf("someKey", "someValue");
        LocalDate lmp = LocalDate.parse("2012-05-05");

        service.registerOutOfAreaANC("CASE X", "ecCaseId", "Wife 1", "Husband 1", "ANM X", "Village X", "SubCenter X", "PHC X", "TC 1", lmp, extraData);

        verify(allActions).add(new Action("CASE X", "ANM X",
                ActionData.registerOutOfAreaANC("ecCaseId", "Wife 1", "Husband 1", "Village X", "SubCenter X", "PHC X", "TC 1", lmp, extraData)));
    }

    @Test
    public void shouldSendPNCVisitHappenedAction() throws Exception {
        Map<String, String> details = mapOf("someKey", "someValue");
        LocalDate visitDate = LocalDate.parse("2012-01-01");
        service.pncVisitHappened(mother, "MOTHER-CASE-1", "ANM X", visitDate, 1, "10", details);

        verify(allActions).add(new Action("MOTHER-CASE-1", "ANM X", ActionData.pncVisitHappened(mother, visitDate, 1, "10", details)));

        service.pncVisitHappened(child, "CHILD-CASE-1", "ANM X", visitDate, 1, "10", details);

        verify(allActions).add(new Action("CHILD-CASE-1", "ANM X", ActionData.pncVisitHappened(child, visitDate, 1, "10", details)));
    }

    @Test
    public void shouldSendBirthPlanningUpdates() {
        Map<String, String> details = mapOf("aKey", "aValue");

        service.updateBirthPlanning("CASE X", "ANM X", details);

        verify(allActions).add(new Action("CASE X", "ANM X", ActionData.updateBirthPlanning(details)));
    }

    @Test
    public void shouldSendImmunizationsUpdatesWhenMotherFoundInDrishti() {
        Map<String, String> details = mapOf("aKey", "aValue");

        service.updateImmunizations("CASE X", "ANM X", details, "bcg opv0", LocalDate.parse("2012-01-01"), "1");

        verify(allActions).add(new Action("CASE X", "ANM X", ActionData.updateImmunizations("bcg opv0", LocalDate.parse("2012-01-01"), "1", details)));
    }

    @Test
    public void shouldReportForIndicator() {
        ActionData summaryActionData = ActionData.reportForIndicator("ANC", "30", new Gson().toJson(asList(new MonthSummaryDatum("3", "2012", "2", "2", asList("CASE 5", "CASE 6")))));

        service.reportForIndicator("ANM X", summaryActionData);

        verify(allActions).add(new Action("", "ANM X", summaryActionData));
    }

    @Test
    public void shouldDeleteAllActionsWithTargetReport() {
        service.deleteReportActions();

        verify(allActions).deleteAllByTarget("report");
    }
}
