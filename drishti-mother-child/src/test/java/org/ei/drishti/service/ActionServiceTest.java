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
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.ei.drishti.dto.AlertStatus.normal;
import static org.ei.drishti.dto.AlertStatus.urgent;
import static org.ei.drishti.dto.BeneficiaryType.*;
import static org.mockito.Mockito.never;
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

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        service = new ActionService(allActions, allMothers, allChildren, allEligibleCouples);
    }

    @Test
    public void shouldSaveAlertActionForMother() throws Exception {
        when(allMothers.findByCaseId("Case X")).thenReturn(new Mother("Case X", "EC-CASE-1", "Thayi 1").withAnm("ANM ID M"));

        DateTime dueDate = DateTime.now().minusDays(1);
        DateTime expiryDate = dueDate.plusWeeks(2);
        service.alertForBeneficiary(mother, "Case X", "Ante Natal Care - Normal", "ANC 1", normal, dueDate, expiryDate);

        verify(allActions).addOrUpdateAlert(new Action("Case X", "ANM ID M", ActionData.createAlert(mother, "Ante Natal Care - Normal", "ANC 1", normal, dueDate, expiryDate)));
    }

    @Test
    public void shouldNotSaveAlertActionIfThereIsNoMotherForTheCaseId() throws Exception {
        when(allMothers.findByCaseId("Case X")).thenReturn(null);

        DateTime dueDate = DateTime.now().minusDays(1);
        DateTime expiryDate = dueDate.plusWeeks(2);
        service.alertForBeneficiary(mother, "Case X", "Ante Natal Care - Normal", "ANC 1", normal, dueDate, expiryDate);

        verify(allActions, never()).addOrUpdateAlert(new Action("Case X", "ANM ID M", ActionData.createAlert(mother, "Ante Natal Care - Normal", "ANC 1", normal, dueDate, expiryDate)));
    }

    @Test
    public void shouldDeleteExistingAlertBeforeCreatingNewOne() throws Exception {
        when(allMothers.findByCaseId("Case X")).thenReturn(new Mother("Case X", "EC-CASE-1", "Thayi 1").withAnm("ANM ID M"));

        DateTime dueDate = DateTime.now().minusDays(1);
        DateTime expiryDate = dueDate.plusWeeks(2);
        service.alertForBeneficiary(mother, "Case X", "Ante Natal Care - Normal", "ANC 1", normal, dueDate, expiryDate);

        verify(allActions).addOrUpdateAlert(new Action("Case X", "ANM ID M", ActionData.createAlert(mother, "Ante Natal Care - Normal", "ANC 1", normal, dueDate, expiryDate)));
    }

    @Test
    public void shouldSaveAlertActionForChild() throws Exception {
        when(allChildren.findByCaseId("Case X")).thenReturn(new Child("Case X", "MOTHER-CASE-1", "bcg", "3", "female").withAnm("ANM ID C"));

        DateTime dueDate = DateTime.now().minusDays(1);
        DateTime expiryDate = dueDate.plusWeeks(2);
        service.alertForBeneficiary(child, "Case X", "OPV", "OPV", normal, dueDate, expiryDate);

        verify(allActions).addOrUpdateAlert(new Action("Case X", "ANM ID C", ActionData.createAlert(child, "OPV", "OPV", normal, dueDate, expiryDate)));
    }

    @Test
    public void shouldNotSaveAlertActionIfThereIsNoChildForTheCaseId() throws Exception {
        when(allChildren.findByCaseId("Case X")).thenReturn(null);

        DateTime dueDate = DateTime.now().minusDays(1);
        DateTime expiryDate = dueDate.plusWeeks(2);
        service.alertForBeneficiary(child, "Case X", "OPV", "OPV", normal, dueDate, expiryDate);

        verify(allActions, never()).addOrUpdateAlert(new Action("Case X", "ANM ID C", ActionData.createAlert(child, "OPV", "OPV", normal, dueDate, expiryDate)));
    }

    @Test
    public void shouldSaveAlertActionForEC() throws Exception {
        when(allEligibleCouples.findByCaseId("Case X")).thenReturn(new EligibleCouple("Case X", "EC-CASE-1").withANMIdentifier("ANM ID C"));

        DateTime dueDate = DateTime.now().minusDays(1);
        DateTime expiryDate = dueDate.plusWeeks(2);
        service.alertForBeneficiary(ec, "Case X", "OCP Refill", "OCP Refill", urgent, dueDate, expiryDate);

        verify(allActions).addOrUpdateAlert(new Action("Case X", "ANM ID C", ActionData.createAlert(ec, "OCP Refill", "OCP Refill", urgent, dueDate, expiryDate)));
    }

    @Test
    public void shouldNotSaveAlertActionIfThereIsNoECForTheCaseId() throws Exception {
        when(allEligibleCouples.findByCaseId("Case X")).thenReturn(null);

        DateTime dueDate = DateTime.now().minusDays(1);
        DateTime expiryDate = dueDate.plusWeeks(2);
        service.alertForBeneficiary(ec, "Case X", "OCP Refill", "OCP Refill", urgent, dueDate, expiryDate);

        verify(allActions, never()).addOrUpdateAlert(new Action("Case X", "ANM ID C", ActionData.createAlert(ec, "OCP Refill", "OCP Refill", urgent, dueDate, expiryDate)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfBeneficiaryTypeIsUnknown() throws Exception {
        service.alertForBeneficiary(null, "Case X", "Schedule name", "FP Complications", urgent, null, null);
    }

    @Test
    public void shouldCreateACloseActionForAVisitOfAChild() throws Exception {
        service.markAlertAsClosed("Case X", "ANM 1", "OPV 1", "2012-01-01");

        verify(allActions).add(new Action("Case X", "ANM 1", ActionData.markAlertAsClosed("OPV 1", "2012-01-01")));
    }

    @Test
    public void shouldCreateACloseActionForAVisitOfAMother() throws Exception {
        when(allMothers.findByCaseId("Case X")).thenReturn(new Mother("Case X", "EC-CASE-1", "Thayi 1").withAnm("ANM ID 1").withLocation("bherya", "Sub Center", "PHC X"));

        service.markAlertAsClosed("Case X", "ANM ID 1", "ANC 1", "2012-12-12");

        verify(allActions).add(new Action("Case X", "ANM ID 1", ActionData.markAlertAsClosed("ANC 1", "2012-12-12")));
    }

    @Test
    public void shouldCreateADeleteAllActionForAMother() throws Exception {
        service.markAllAlertsAsInactive("Case X");

        verify(allActions).markAllAsInActiveFor("Case X");
    }

    @Test
    public void shouldMarkAnAlertAsInactive() throws Exception {
        service.markAlertAsInactive("ANM 1","Case X", "Schedule 1");

        verify(allActions).markAlertAsInactiveFor("ANM 1","Case X", "Schedule 1");
    }

    @Test
    public void shouldReturnAlertsBasedOnANMIDAndTimeStamp() throws Exception {
        List<Action> alertActions = Arrays.asList(new Action("Case X", "ANM 1", ActionData.createAlert(mother, "Ante Natal Care - Normal", "ANC 1", normal, DateTime.now(), DateTime.now().plusDays(3))));
        when(allActions.findByANMIDAndTimeStamp("ANM 1", 1010101)).thenReturn(alertActions);

        List<Action> alerts = service.getNewAlertsForANM("ANM 1", 1010101);

        assertEquals(1, alerts.size());
        assertEquals(alertActions, alerts);
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
