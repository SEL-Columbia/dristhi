package org.ei.drishti.service;

import org.ei.drishti.domain.Action;
import org.ei.drishti.domain.Child;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.dto.ActionData;
import org.ei.drishti.repository.AllActions;
import org.ei.drishti.repository.AllChildren;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.repository.AllMothers;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.util.DateUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.ei.drishti.dto.AlertPriority.normal;
import static org.ei.drishti.dto.BeneficiaryType.child;
import static org.ei.drishti.dto.BeneficiaryType.mother;
import static org.ei.drishti.util.EasyMap.mapOf;
import static org.mockito.Mockito.*;
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

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        service = new ActionService(allActions, allMothers, allChildren, allEligibleCouples);
    }

    @Test
    public void shouldSaveAlertActionForMother() throws Exception {
        when(allMothers.findByCaseId("Case X")).thenReturn(new Mother("Case X", "Thaayi 1", "Theresa").withAnm("ANM ID M", "ANM phone no"));

        DateTime dueDate = DateTime.now().minusDays(1);
        DateTime expiryDate = dueDate.plusWeeks(2);
        service.alertForBeneficiary(mother, "Case X", "ANC 1", normal, dueDate, expiryDate);

        verify(allActions).add(new Action("Case X", "ANM ID M", ActionData.createAlert(mother, "ANC 1", normal, dueDate, expiryDate)));
    }

    @Test
    public void shouldSaveAlertActionForChild() throws Exception {
        when(allChildren.findByCaseId("Case X")).thenReturn(new Child("Case X", "Thaayi 1", "Kid", Arrays.<String>asList(), "female").withAnm("ANM ID C"));

        DateTime dueDate = DateTime.now().minusDays(1);
        DateTime expiryDate = dueDate.plusWeeks(2);
        service.alertForBeneficiary(child, "Case X", "OPV", normal, dueDate, expiryDate);

        verify(allActions).add(new Action("Case X", "ANM ID C", ActionData.createAlert(child, "OPV", normal, dueDate, expiryDate)));
    }

    @Test
    public void shouldCreateACloseActionForAVisitOfAChild() throws Exception {
        service.markAlertAsClosedForVisitForChild("Case X", "ANM 1", "OPV 1");

        verify(allActions).add(new Action("Case X", "ANM 1", ActionData.markAlertAsClosed("OPV 1")));
    }

    @Test
    public void shouldCreateACloseActionForAVisitOfAMother() throws Exception {
        when(allMothers.findByCaseId("Case X")).thenReturn(new Mother("Case X", "Thaayi 1", "Theresa").withAnm("ANM ID 1", "ANM phone no").withLocation("bherya", "Sub Center", "PHC X"));

        service.markAlertAsClosedForVisitForMother("Case X", "ANM ID 1", "ANC 1");

        verify(allActions).add(new Action("Case X", "ANM ID 1", ActionData.markAlertAsClosed("ANC 1")));
    }

    @Test
    public void shouldCreateADeleteAllActionForAMother() throws Exception {
        when(allMothers.findByCaseId("Case X")).thenReturn(new Mother("Case X", "Thaayi 1", "Theresa").withAnm("ANM ID 1", "ANM phone no").withLocation("bherya", "Sub Center", "PHC X"));

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
    public void shouldAddActionForBeneficiaryRegistration() throws Exception {
        when(allEligibleCouples.findByECNumberAndVillage("EC Number 1", "Village X")).thenReturn(new EligibleCouple("Case EC 1", "EC Number 1"));

        HashMap<String, String> details = new HashMap<>();
        details.put("some_field", "some_value");

        service.registerPregnancy("Case X", "EC Number 1", "Thaayi 1", "ANM X", "Village X", DateUtil.today(), false, "PHC", details);

        verify(allActions).add(new Action("Case X", "ANM X", ActionData.createBeneficiary("Case EC 1", "Thaayi 1", DateUtil.today(), false, "PHC", details)));
    }

    @Test
    public void shouldNotAddActionForBeneficiaryRegistrationIfECNotFound() throws Exception {
        when(allEligibleCouples.findByECNumberAndVillage("EC Number 1", "Village X")).thenReturn(null);

        service.registerPregnancy("Case X", "EC Number 1", "Thaayi 1", "ANM X", "Village X", null, true, "PHC", new HashMap<String, String>());

        verifyZeroInteractions(allActions);
    }

    @Test
    public void shouldUpdateBeneficiaryWhenECIsRegistered() throws Exception {
        when(allMothers.findByCaseId("Case X")).thenReturn(new Mother("Case X", "TC 1", "Theresa").withAnm("ANM X", "ANM phone number").withECNumber("EC Number 1").withLocation("bherya", "Sub Center", "PHC X"));
        when(allEligibleCouples.findByECNumberAndVillage("EC Number 1", "bherya")).thenReturn(new EligibleCouple("Case EC 1", "EC Number 1"));

        service.updateDeliveryOutcome("Case X", "delivery");

        verify(allActions).add(new Action("Case X", "ANM X", ActionData.updateBeneficiary("delivery")));
    }

    @Test
    public void shouldUpdateBeneficiaryWhenECIsNotRegistered() throws Exception {
        when(allMothers.findByCaseId("Case X")).thenReturn(new Mother("Case X", "TC 1", "Theresa").withAnm("ANM X", "ANM phone number").withECNumber("EC Number 1").withLocation("bherya", "Sub Center", "PHC X"));
        when(allEligibleCouples.findByECNumberAndVillage("EC Number 1", "bherya")).thenReturn(null);

        service.updateDeliveryOutcome("Case X", "delivery");

        verifyZeroInteractions(allActions);
    }

    @Test
    public void shouldRegisterChildBirth() throws Exception {
        when(allMothers.findByThaayiCardNumber("MotherThaayiCard 1")).thenReturn(new Mother("MotherCaseId", "MotherThaayiCard 1", "Theresa"));

        service.registerChildBirth("ChildCase Y", "ANM X", "MotherThaayiCard 1", DateUtil.today(), "female");

        verify(allActions).add(new Action("ChildCase Y", "ANM X", ActionData.registerChildBirth("MotherCaseId", DateUtil.today(), "female")));
    }

    @Test
    public void shouldNotRegisterChildBirthWhenMotherIsNotFound() throws Exception {
        when(allMothers.findByThaayiCardNumber("MotherThaayiCard 1")).thenReturn(null);

        service.registerChildBirth("ChildCase Y", "ANM X", "MotherThaayiCard 1", DateUtil.today(), "female");

        verifyZeroInteractions(allActions);
    }

    @Test
    public void shouldSendAnUpdateECDetailsAction() throws Exception {
        service.updateEligibleCoupleDetails("CASE X", "ANM X", mapOf("someKey", "someValue"));

        verify(allActions).add(new Action("CASE X", "ANM X", ActionData.updateEligibleCoupleDetails(mapOf("someKey", "someValue"))));
    }

    @Test
    public void shouldSendAnANCCareProvidedAction() throws Exception {
        service.updateMotherDetails("CASE X", "ANM X", mapOf("someKey", "someValue"));

        verify(allActions).add(new Action("CASE X", "ANM X", ActionData.updateMotherDetails(mapOf("someKey", "someValue"))));
    }
}
