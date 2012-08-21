package org.ei.drishti.web.controller;

import org.ei.drishti.dto.Action;
import org.ei.drishti.dto.ActionData;
import org.ei.drishti.service.ActionService;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.delivery.schedule.util.SameItems.hasSameItemsAs;

public class ActionControllerTest {

    @Mock
    private ActionService actionService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldGiveAlertActionForANMSinceTimeStamp() throws Exception {
        org.ei.drishti.domain.Action alertAction = new org.ei.drishti.domain.Action("Case X", "ANM 1", ActionData.createAlert("mother", "ANC 1", "normal", DateTime.now(), DateTime.now().plusDays(3)));
        when(actionService.getNewAlertsForANM("ANM 1", 0L)).thenReturn(Arrays.asList(alertAction));

        Action expectedAlertActionItem = ActionConvertor.from(alertAction);
        ActionController alertController = new ActionController(actionService);

        assertThat(Arrays.asList(expectedAlertActionItem), hasSameItemsAs(alertController.getNewActionForANM("ANM 1", 0L)));
    }
}
