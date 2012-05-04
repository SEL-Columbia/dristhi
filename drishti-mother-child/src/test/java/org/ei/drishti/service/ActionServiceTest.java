package org.ei.drishti.service;

import org.ei.drishti.domain.Action;
import org.ei.drishti.domain.ActionData;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.repository.AllActions;
import org.ei.drishti.repository.AllMothers;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ActionServiceTest {
    @Mock
    private AllActions allActions;
    @Mock
    private AllMothers allMothers;

    private ActionService service;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        service = new ActionService(allActions, allMothers);
    }

    @Test
    public void shouldSaveAlertActionForMother() throws Exception {
        when(allMothers.findByCaseId("Case X")).thenReturn(new Mother("Case X", "Thaayi 1", "Theresa").withAnmPhoneNumber("ANM phone no"));

        DateTime dueDate = DateTime.now().minusDays(1);
        service.alertForMother("Case X", "ANC 1", "due", dueDate);

        verify(allActions).add(new Action("Case X", "ANM phone no", ActionData.createAlert("Theresa", "Thaayi 1", "ANC 1", "due", dueDate)));
    }

    @Test
    public void shouldSaveAlertActionForChild() throws Exception {
        DateTime dueDate = DateTime.now().minusDays(1);

        service.alertForChild("Case X", "Child 1", "DEMO ANM", "TC 1", "OPV 1", "due", dueDate);

        verify(allActions).add(new Action("Case X", "DEMO ANM", ActionData.createAlert("Child 1", "TC 1", "OPV 1", "due", dueDate)));
    }

    @Test
    public void shouldCreateADeleteActionForAVisitOfAChild() throws Exception {
        service.deleteAlertForVisitForChild("Case X", "ANM 1", "OPV 1");

        verify(allActions).add(new Action("Case X", "ANM 1", ActionData.deleteAlert("OPV 1")));
    }

    @Test
    public void shouldCreateADeleteActionForAVisitOfAMother() throws Exception {
        when(allMothers.findByCaseId("Case X")).thenReturn(new Mother("Case X", "Thaayi 1", "Theresa").withAnmPhoneNumber("ANM phone no"));

        service.deleteAlertForVisitForMother("Case X", "ANC 1");

        verify(allActions).add(new Action("Case X", "ANM phone no", ActionData.deleteAlert("ANC 1")));
    }

    @Test
    public void shouldCreateADeleteAllActionForAMother() throws Exception {
        when(allMothers.findByCaseId("Case X")).thenReturn(new Mother("Case X", "Thaayi 1", "Theresa").withAnmPhoneNumber("ANM phone no"));

        service.deleteAllAlertsForMother("Case X");

        verify(allActions).addWithDelete(new Action("Case X", "ANM phone no", ActionData.deleteAllAlerts()));
    }

    @Test
    public void shouldCreateADeleteAllActionForAChild() throws Exception {
        service.deleteAllAlertsForChild("Case X", "DEMO ANM");

        verify(allActions).addWithDelete(new Action("Case X", "DEMO ANM", ActionData.deleteAllAlerts()));
    }

    @Test
    public void shouldReturnAlertsBasedOnANMIDAndTimeStamp() throws Exception {
        List<Action> alertActions = Arrays.asList(new Action("Case X", "ANM 1", ActionData.createAlert("Theresa", "Thaayi 1", "ANC 1", "due", DateTime.now())));
        when(allActions.findByANMIDAndTimeStamp("ANM 1", 1010101)).thenReturn(alertActions);

        List<Action> alerts = service.getNewAlertsForANM("ANM 1", 1010101);

        assertEquals(1, alerts.size());
        assertEquals(alertActions, alerts);
    }

    @Test
    public void shouldAddCreateActionForEligibleCoupleRegistration() throws Exception {
        service.registerEligibleCouple("Case X", "EC Number 1", "Wife 1", "Husband 1", "ANM X");

        verify(allActions).add(new Action("Case X", "ANM X", ActionData.createEligibleCouple("Wife 1", "Husband 1", "EC Number 1")));
    }

    @Test
    public void shouldAddDeleteActionForEligibleCoupleClose() throws Exception {
        service.closeEligibleCouple("Case X", "ANM X");

        verify(allActions).addWithDelete(new Action("Case X", "ANM X", ActionData.deleteEligibleCouple()));
    }
}
