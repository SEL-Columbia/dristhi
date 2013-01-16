package org.ei.drishti.repository.it;

import org.ei.drishti.domain.Action;
import org.ei.drishti.dto.ActionData;
import org.ei.drishti.repository.AllActions;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.ei.drishti.dto.BeneficiaryType.mother;
import static org.ei.drishti.dto.AlertPriority.normal;
import static org.ei.drishti.dto.AlertPriority.urgent;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-drishti.xml")
public class AllActionsIntegrationTest {
    @Autowired
    AllActions allActions;

    @Before
    public void setUp() throws Exception {
        allActions.removeAll();
    }

    @Test
    public void shouldSaveAReminder() throws Exception {
        Action alertAction = new Action("Case X", "ANM phone no", alert());

        allActions.add(alertAction);

        List<Action> allTheAlertActionsInDB = allActions.getAll();
        assertEquals(1, allTheAlertActionsInDB.size());
        assertEquals(alertAction, allTheAlertActionsInDB.get(0));
    }

    @Test
    public void shouldNotFindAnyAlertsIfNoneExistForGivenANM() throws Exception {
        assertEquals(0, allActions.findByANMIDAndTimeStamp("ANM 1", 0).size());
    }

    @Test
    public void shouldReturnAlertActionsBasedOnANMIDAndTimeStamp() throws Exception {
        Action firstAction = new Action("Case X", "ANM 1", alert());
        allActions.add(firstAction);

        Action secondAction = new Action("Case Y", "ANM 1", alert());
        allActions.add(secondAction);

        Action thirdAction = new Action("Case Z", "ANM 1", alert());
        allActions.add(thirdAction);

        assertEquals(asList(firstAction, secondAction, thirdAction), allActions.findByANMIDAndTimeStamp("ANM 1", 0));
        assertEquals(asList(secondAction,thirdAction), allActions.findByANMIDAndTimeStamp("ANM 1", firstAction.timestamp()));
        assertEquals(asList(thirdAction), allActions.findByANMIDAndTimeStamp("ANM 1", secondAction.timestamp()));

        assertEquals(0, allActions.findByANMIDAndTimeStamp("ANM 1", thirdAction.timestamp()).size());
    }

    @Test
    public void shouldFindAlertsOnlyForTheANMSpecified() throws Exception {
        Action firstAction = new Action("Case X", "ANM 1", alert());
        allActions.add(firstAction);

        Action secondAction = new Action("Case Y", "ANM 2", alert());
        allActions.add(secondAction);

        assertEquals(asList(firstAction), allActions.findByANMIDAndTimeStamp("ANM 1", 0));
        assertEquals(asList(secondAction), allActions.findByANMIDAndTimeStamp("ANM 2", 0));
    }

    @Test
    public void shouldMarkAllActionsAsInActiveForACase() throws Exception {
        Action firstAction = new Action("Case X", "ANM 1", alert());
        Action secondAction = new Action("Case X", "ANM 1", alert());
        Action thirdAction = new Action("Case Y", "ANM 2", alert());
        allActions.add(firstAction);
        allActions.add(secondAction);
        allActions.add(thirdAction);

        allActions.markAllAsInActiveFor("Case X");

        assertEquals(asList(firstAction.markAsInActive(), secondAction.markAsInActive()), allActions.findByANMIDAndTimeStamp("ANM 1", 0));
        assertEquals(asList(thirdAction), allActions.findByANMIDAndTimeStamp("ANM 2", 0));
    }

    @Test
    public void shouldFetchAlertsSortedByTimestamp() throws Exception {
        Action earlierAction = new Action("Case X", "ANM 1", alert());
        Thread.sleep(100);
        Action laterAction = new Action("Case X", "ANM 1", alert());
        Thread.sleep(100);
        Action latestAction = new Action("Case X", "ANM 1", alert());

        allActions.add(laterAction);
        allActions.add(latestAction);
        allActions.add(earlierAction);

        assertEquals(asList(earlierAction, laterAction, latestAction), allActions.findByANMIDAndTimeStamp("ANM 1", 0));
    }

    @Test
    public void shouldRemoveAllExistingAlertsForTheCaseFromRepositoryBeforeInsertingADeleteAllAlert() {
        Action firstAlertAction = new Action("Case X", "ANM 1", alert());
        Action secondAlertAction = new Action("Case X", "ANM 1", ActionData.createAlert(mother, "ANC 2", urgent, DateTime.now(), DateTime.now().plusDays(3)));
        Action thirdAlertAction = new Action("Case X", "ANM 1", ActionData.createAlert(mother, "ANC 3", normal, DateTime.now(), DateTime.now().plusDays(3)));
        Action fourthNonAlertActionForSameMother = new Action("Case X", "ANM 1", ActionData.registerPregnancy("EC Case 1", "Thaayi 1", DateUtil.today(), new HashMap<String, String>()));
        Action actionOfSameANMForAnotherMother = new Action("Case ABC", "ANM 1", ActionData.createAlert(mother, "ANC 3", normal, DateTime.now(), DateTime.now().plusDays(3)));
        Action actionOfAnotherANM = new Action("Case Y", "ANM 2", alert());
        allActions.add(firstAlertAction);
        allActions.add(secondAlertAction);
        allActions.add(thirdAlertAction);
        allActions.add(fourthNonAlertActionForSameMother);
        allActions.add(actionOfAnotherANM);
        allActions.add(actionOfSameANMForAnotherMother);

        Action deleteAllAction = new Action("Case X", "ANM 1", ActionData.deleteAllAlerts());
        allActions.addWithDeleteByTarget(deleteAllAction, "alert");

        assertEquals(asList(fourthNonAlertActionForSameMother, actionOfSameANMForAnotherMother, deleteAllAction), allActions.findByANMIDAndTimeStamp("ANM 1", 0));
        assertEquals(asList(actionOfAnotherANM), allActions.findByANMIDAndTimeStamp("ANM 2", 0));
    }

    @Test
    public void shouldRemoveAllExistingActionsByTarget() {
        Action firstAlertAction = new Action("Case X", "ANM 1", alert());
        Action secondAlertAction = new Action("Case Y", "ANM 1", alert());
        Action anotherAction = new Action("Case Z", "ANM 1", ActionData.registerPregnancy("EC Case 1", "Thaayi 1", DateUtil.today(), new HashMap<String, String>()));
        allActions.add(firstAlertAction);
        allActions.add(secondAlertAction);
        allActions.add(anotherAction);

        allActions.deleteAllByTarget("alert");

        assertEquals(asList(anotherAction), allActions.findByANMIDAndTimeStamp("ANM 1", 0));
    }

    @Test
    public void shouldNotDoAnythingIfNoActionsAreFoundForATarget() {
        Action alertAction = new Action("Case X", "ANM 1", alert());
        allActions.add(alertAction);

        allActions.deleteAllByTarget("report");

        assertEquals(asList(alertAction), allActions.findByANMIDAndTimeStamp("ANM 1", 0));
    }

    private ActionData alert() {
        return ActionData.createAlert(mother, "ANC 1", normal, DateTime.now(), DateTime.now().plusDays(3));
    }
}
