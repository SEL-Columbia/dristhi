package org.ei.drishti.repository.it;

import org.ei.drishti.domain.AlertData;
import org.ei.drishti.domain.AlertAction;
import org.ei.drishti.repository.AllAlertActions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
        AlertAction alertAction = new AlertAction("Case X", "ANM phone no", AlertData.create("Theresa", "Thaayi 1", "ANC 1", "due"));

        alertActions.add(alertAction);

        List<AlertAction> allTheAlertActionsInDB = alertActions.getAll();
        assertEquals(1, allTheAlertActionsInDB.size());
        assertEquals(alertAction, allTheAlertActionsInDB.get(0));
    }
}
