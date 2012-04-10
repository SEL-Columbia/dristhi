package org.ei.drishti.repository.it;

import org.ei.drishti.domain.AlertAction;
import org.ei.drishti.domain.AlertData;
import org.ei.drishti.repository.AllAlertActions;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-drishti.xml")
public class AllAlertActionsIntegrationTest {
    @Autowired
    AllAlertActions alertActions;

    @Before
    public void setUp() throws Exception {
        alertActions.removeAll();
    }

    @Test
    public void shouldSaveAReminder() throws Exception {
        AlertAction alertAction = new AlertAction("Case X", "ANM phone no", AlertData.create("Theresa", "Thaayi 1", "ANC 1", "due", DateTime.now()));

        alertActions.add(alertAction);

        List<AlertAction> allTheAlertActionsInDB = alertActions.getAll();
        assertEquals(1, allTheAlertActionsInDB.size());
        assertEquals(alertAction, allTheAlertActionsInDB.get(0));
    }

    @Test
    public void shouldNotFindAnyAlertsIfNoneExistForGivenANM() throws Exception {
        assertEquals(0, alertActions.findByANMIDAndTimeStamp("ANM 1", 0).size());
    }

    @Test
    public void shouldReturnAlertActionsBasedOnANMIDAndTimeStamp() throws Exception {
        AlertAction firstAction = new AlertAction("Case X", "ANM 1", AlertData.create("Theresa", "Thaayi 1", "ANC 1", "due", DateTime.now()));
        alertActions.add(firstAction);

        AlertAction secondAction = new AlertAction("Case Y", "ANM 1", AlertData.create("Theresa", "Thaayi 1", "ANC 1", "due", DateTime.now()));
        alertActions.add(secondAction);

        AlertAction thirdAction = new AlertAction("Case Z", "ANM 1", AlertData.create("Theresa", "Thaayi 1", "ANC 1", "due", DateTime.now()));
        alertActions.add(thirdAction);

        assertEquals(Arrays.asList(firstAction, secondAction,thirdAction), alertActions.findByANMIDAndTimeStamp("ANM 1", 0));
        assertEquals(Arrays.asList(secondAction,thirdAction), alertActions.findByANMIDAndTimeStamp("ANM 1", firstAction.timestamp()));
        assertEquals(Arrays.asList(thirdAction), alertActions.findByANMIDAndTimeStamp("ANM 1", secondAction.timestamp()));

        assertEquals(0, alertActions.findByANMIDAndTimeStamp("ANM 1", thirdAction.timestamp()).size());
    }

    @Test
    public void shouldFindAlertsOnlyForTheANMSpecified() throws Exception {
        AlertAction firstAction = new AlertAction("Case X", "ANM 1", AlertData.create("Theresa", "Thaayi 1", "ANC 1", "due", DateTime.now()));
        alertActions.add(firstAction);

        AlertAction secondAction = new AlertAction("Case Y", "ANM 2", AlertData.create("Theresa", "Thaayi 1", "ANC 1", "due", DateTime.now()));
        alertActions.add(secondAction);

        assertEquals(Arrays.asList(firstAction), alertActions.findByANMIDAndTimeStamp("ANM 1", 0));
        assertEquals(Arrays.asList(secondAction), alertActions.findByANMIDAndTimeStamp("ANM 2", 0));
    }
}
