package org.ei.drishti.repository.it;

import org.ei.drishti.domain.Action;
import org.ei.drishti.domain.ActionData;
import org.ei.drishti.repository.AllActions;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;

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
        Action alertAction = new Action("Case X", "ANM phone no", ActionData.createAlert("Theresa", "bherya", "Thaayi 1", "ANC 1", "due", DateTime.now()));

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
        Action firstAction = new Action("Case X", "ANM 1", ActionData.createAlert("Theresa", "bherya", "Thaayi 1", "ANC 1", "due", DateTime.now()));
        allActions.add(firstAction);

        Action secondAction = new Action("Case Y", "ANM 1", ActionData.createAlert("Theresa", "bherya", "Thaayi 1", "ANC 1", "due", DateTime.now()));
        allActions.add(secondAction);

        Action thirdAction = new Action("Case Z", "ANM 1", ActionData.createAlert("Theresa", "bherya", "Thaayi 1", "ANC 1", "due", DateTime.now()));
        allActions.add(thirdAction);

        assertEquals(asList(firstAction, secondAction, thirdAction), allActions.findByANMIDAndTimeStamp("ANM 1", 0));
        assertEquals(asList(secondAction,thirdAction), allActions.findByANMIDAndTimeStamp("ANM 1", firstAction.timestamp()));
        assertEquals(asList(thirdAction), allActions.findByANMIDAndTimeStamp("ANM 1", secondAction.timestamp()));

        assertEquals(0, allActions.findByANMIDAndTimeStamp("ANM 1", thirdAction.timestamp()).size());
    }

    @Test
    public void shouldFindAlertsOnlyForTheANMSpecified() throws Exception {
        Action firstAction = new Action("Case X", "ANM 1", ActionData.createAlert("Theresa", "bherya", "Thaayi 1", "ANC 1", "due", DateTime.now()));
        allActions.add(firstAction);

        Action secondAction = new Action("Case Y", "ANM 2", ActionData.createAlert("Theresa", "bherya", "Thaayi 1", "ANC 1", "due", DateTime.now()));
        allActions.add(secondAction);

        assertEquals(asList(firstAction), allActions.findByANMIDAndTimeStamp("ANM 1", 0));
        assertEquals(asList(secondAction), allActions.findByANMIDAndTimeStamp("ANM 2", 0));
    }

    @Test
    public void shouldFetchAlertsSortedByTimestamp() throws Exception {
        Action earlierAction = new Action("Case X", "ANM 1", ActionData.createAlert("Theresa", "bherya", "Thaayi 1", "ANC 1", "due", DateTime.now()));
        Thread.sleep(100);
        Action laterAction = new Action("Case X", "ANM 1", ActionData.createAlert("Theresa", "bherya", "Thaayi 1", "ANC 1", "due", DateTime.now()));
        Thread.sleep(100);
        Action latestAction = new Action("Case X", "ANM 1", ActionData.createAlert("Theresa", "bherya", "Thaayi 1", "ANC 1", "due", DateTime.now()));

        allActions.add(laterAction);
        allActions.add(latestAction);
        allActions.add(earlierAction);

        assertEquals(asList(earlierAction, laterAction, latestAction), allActions.findByANMIDAndTimeStamp("ANM 1", 0));
    }

    @Test
    public void shouldRemoveAllExistingAlertsForTheCaseFromRepositoryBeforeInsertingADeleteAllAlert() {
        Action firstAlertAction = new Action("Case X", "ANM 1", ActionData.createAlert("Theresa", "bherya", "Thaayi 1", "ANC 1", "due", DateTime.now()));
        Action secondAlertAction = new Action("Case X", "ANM 1", ActionData.createAlert("Theresa", "bherya", "Thaayi 1", "ANC 2", "late", DateTime.now()));
        Action thirdAlertAction = new Action("Case X", "ANM 1", ActionData.createAlert("Theresa", "bherya", "Thaayi 1", "ANC 3", "due", DateTime.now()));
        Action fourthNonAlertActionForSameMother = new Action("Case X", "ANM 1", ActionData.createBeneficiary("EC Case 1", "Thaayi 1", DateUtil.today()));
        Action actionOfSameANMForAnotherMother = new Action("Case ABC", "ANM 1", ActionData.createAlert("Theresa", "bherya", "Thaayi 1", "ANC 3", "due", DateTime.now()));
        Action actionOfAnotherANM = new Action("Case Y", "ANM 2", ActionData.createAlert("Theresa", "bherya", "Thaayi 1", "ANC 1", "due", DateTime.now()));
        allActions.add(firstAlertAction);
        allActions.add(secondAlertAction);
        allActions.add(thirdAlertAction);
        allActions.add(fourthNonAlertActionForSameMother);
        allActions.add(actionOfAnotherANM);
        allActions.add(actionOfSameANMForAnotherMother);

        Action deleteAllAction = new Action("Case X", "ANM 1", ActionData.deleteAllAlerts());
        allActions.addWithDelete(deleteAllAction, "alert");

        assertEquals(asList(fourthNonAlertActionForSameMother, actionOfSameANMForAnotherMother, deleteAllAction), allActions.findByANMIDAndTimeStamp("ANM 1", 0));
        assertEquals(asList(actionOfAnotherANM), allActions.findByANMIDAndTimeStamp("ANM 2", 0));
    }
}
